package qwatch.logs.util;

import io.vavr.collection.List;
import io.vavr.control.Either;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import qwatch.logs.model.LogEntry;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * CSV Importer imports CSV files with Datadog format.
 *
 * @author Mincong Huang
 * @since 1.0
 */
public class CsvImporter {

  public static Either<String, List<LogEntry>> importLogEntries(Path logPath) {
    String content;
    try {
      content = new String(Files.readAllBytes(logPath), UTF_8);
    } catch (IOException e) {
      String message = "Failed to read logs from filepath: " + logPath;
      return Either.left(message);
    }
    Either<String, List<String[]>> parsed = internalParseCsv(content, 4);
    if (parsed.isLeft()) {
      return Either.left(parsed.getLeft());
    }
    java.util.List<LogEntry> entries = new ArrayList<>();
    java.util.List<String> failures = new ArrayList<>();
    boolean isHeader = true;
    for (String[] row : parsed.get()) {
      if (isHeader) {
        isHeader = false;
      } else {
        toLogEntry(row).peek(entries::add).peekLeft(failures::add);
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
   * @return either a LogEntry or a failure
   */
  private static Either<String, LogEntry> toLogEntry(String[] columns) {
    String dateStr = columns[0];
    String service = columns[1];
    String status = columns[2];
    String message = columns[3];
    ZonedDateTime d;
    try {
      d = ZonedDateTime.parse(dateStr);
    } catch (DateTimeParseException e) {
      return Either.left("Unable to parse date: " + dateStr);
    }
    return Either.right(
        LogEntry.newBuilder().dateTime(d).service(service).status(status).message(message).build());
  }

  /**
   * @param content the CSV content to parse
   * @param cols number of columns
   * @return either a list of rows or a failure
   */
  static Either<String, List<String[]>> internalParseCsv(String content, int cols) {
    char sep = ',';
    char qualifier = '"';
    char newline1 = '\n';
    char newline2 = '\r';

    java.util.List<String[]> rows = new ArrayList<>();
    char[] arr = content.toCharArray();
    String[] row = new String[cols];
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
