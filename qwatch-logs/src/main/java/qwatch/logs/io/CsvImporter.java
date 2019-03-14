package qwatch.logs.io;

import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Either;
import io.vavr.control.Try;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.model.LogEntry;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * CSV Importer imports CSV files with Datadog format.
 *
 * @author Mincong Huang
 * @since 1.0
 */
public class CsvImporter {
  private static final Logger logger = LoggerFactory.getLogger(CsvImporter.class);

  private static final String COL_DATE = "date";
  private static final String COL_HOST = "Host";
  private static final String COL_MESSAGE = "message";
  private static final String COL_SERVICE = "Service";
  private static final String COL_STATUS = "Status";
  private static final String[] REQUIRED_COLUMNS = {COL_DATE, COL_HOST, COL_MESSAGE, COL_SERVICE};

  private static Try<Set<Path>> listCsvPaths(Path dir) {
    Set<Path> paths = HashSet.empty();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "extract-*.csv")) {
      for (Path csv : stream) {
        paths = paths.add(csv);
      }
      return Try.success(paths);
    } catch (IOException e) {
      return Try.failure(e);
    }
  }

  public static Either<String, Set<LogEntry>> importLogEntries(Path dir) {
    // Find paths
    var tryListing = listCsvPaths(dir);
    if (tryListing.isFailure()) {
      return Either.left(tryListing.getCause().getMessage());
    }
    var paths = tryListing.get();

    // Import log entries
    Set<LogEntry> entries = HashSet.empty();
    var pool = Executors.newWorkStealingPool();
    var tasks = paths.map(ImportCsvTask::new).toSet();
    try {
      for (var future : pool.invokeAll(tasks.toJavaSet())) {
        if (!future.isCancelled()) {
          entries = entries.addAll(future.get());
        }
      }
    } catch (InterruptedException e) {
      logger.error("Interrupted", e);
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      return Either.left("Failed to get result from future");
    } finally {
      pool.shutdownNow();
    }
    return Either.right(entries);
  }

  public static Either<String, List<LogEntry>> importLogEntriesFromFile(Path logPath) {
    String content;
    try {
      content = new String(Files.readAllBytes(logPath), UTF_8);
    } catch (IOException e) {
      return Either.left("Failed to read logs from filepath: " + logPath);
    }
    var parsed = internalParseCsv(content);
    if (parsed.isLeft()) {
      return Either.left(parsed.getLeft());
    }
    java.util.List<LogEntry> entries = new ArrayList<>();
    java.util.List<String> failures = new ArrayList<>();
    boolean isHeader = true;
    Map<String, Integer> columnMapping = HashMap.empty();
    for (var row : parsed.get()) {
      if (isHeader) {
        var m = toHeaderMapping(row);
        if (m.isLeft()) {
          return Either.left(m.getLeft());
        }
        columnMapping = m.get();
        isHeader = false;
      } else {
        toLogEntry(row, columnMapping).peek(entries::add).peekLeft(failures::add);
      }
    }
    if (failures.isEmpty()) {
      return Either.right(List.ofAll(entries));
    } else {
      return Either.left("Unable to parse CSV file: " + logPath);
    }
  }

  /**
   * Parse a row to {@link LogEntry}.
   *
   * @param columns row to parse
   * @param index column name-index mapping (key: column name, value: column index)
   * @return either a LogEntry or a failure
   */
  private static Either<String, LogEntry> toLogEntry(String[] columns, Map<String, Integer> index) {
    var dateStr = columns[index.get(COL_DATE).get()];
    var service = columns[index.get(COL_SERVICE).get()];
    var message = columns[index.get(COL_MESSAGE).get()];
    var host = columns[index.get(COL_HOST).get()];
    String status;
    ZonedDateTime d;

    // fill fields
    if (index.containsKey(COL_STATUS)) {
      status = columns[index.get(COL_STATUS).get()];
    } else {
      status = "error";
    }
    try {
      d = ZonedDateTime.parse(dateStr).withZoneSameLocal(ZoneId.of("UTC"));
    } catch (DateTimeParseException e) {
      return Either.left("Unable to parse date: " + dateStr);
    }

    // build
    var entry =
        LogEntry.newBuilder()
            .dateTime(d)
            .host(host)
            .service(service)
            .status(status)
            .message(message)
            .build();
    return Either.right(entry);
  }

  static Either<String, Map<String, Integer>> toHeaderMapping(String[] columns) {
    Map<String, Integer> mapping = HashMap.empty();
    for (int i = 0; i < columns.length; i++) {
      mapping = mapping.put(columns[i], i);
    }
    for (var col : REQUIRED_COLUMNS) {
      if (!mapping.containsKey(col)) {
        return Either.left("Missing required column: " + col + " in CSV");
      }
    }
    return Either.right(mapping);
  }

  /**
   * Parses CSV content into a list of rows.
   *
   * <p>This method assumes that the header is present in the first line of the content.
   *
   * @param content the CSV content to parse
   * @return either a list of rows or a failure
   */
  static Either<String, List<String[]>> internalParseCsv(String content) {
    char sep = ',';
    char qualifier = '"';
    char newline1 = '\n';
    char newline2 = '\r';

    var rows = new ArrayList<String[]>();
    var arr = content.toCharArray();
    int cols = 1;

    // Header
    for (int i = 0; arr[i] != newline1 && arr[i] != newline2; i++) {
      if (arr[i] == sep) {
        cols++;
      }
    }

    var row = new String[cols];
    int columnIdx = 0;
    int i = 0;
    while (i < arr.length) {
      if (arr[i] == sep || arr[i] == newline1 || arr[i] == newline2) {
        i++;
      } else if (arr[i] == qualifier) {
        // column with qualifier
        i++;
        int left = i;
        while (i < arr.length) {
          if (arr[i] == qualifier) {
            boolean escaped = i + 1 < arr.length && arr[i + 1] == qualifier;
            if (escaped) {
              i += 2;
            } else {
              break;
            }
          } else {
            i++;
          }
        }
        row[columnIdx] = new String(arr, left, i - left).replaceAll("\"\"", "\"");
        columnIdx++;
        i++; // skip qualifier
        if (columnIdx == cols) {
          rows.add(row);
          row = new String[cols];
          columnIdx = 0;
        }
      } else {
        // column without qualifier
        int left = i;
        while (i < arr.length && arr[i] != sep && arr[i] != newline1 && arr[i] != newline2) {
          i++;
        }
        row[columnIdx] = new String(arr, left, i - left);
        columnIdx++;
        if (columnIdx == cols) {
          rows.add(row);
          row = new String[cols];
          columnIdx = 0;
        }
      }
    }
    return Either.right(List.ofAll(rows));
  }

  private CsvImporter() {
    // Utility class, do not instantiate
  }
}
