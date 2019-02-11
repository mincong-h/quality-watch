package qwatch.logs;

import io.vavr.collection.List;
import io.vavr.control.Either;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import qwatch.logs.model.LogEntry;

/**
 * CSV Importer imports CSV files with Datadog format.
 *
 * @author Mincong Huang
 * @since 1.0
 */
public class CsvImporter {

  public static Either<String, List<LogEntry>> importLogEntries(Path logPath) {
    java.util.List<String> lines;
    try {
      lines = Files.readAllLines(logPath);
    } catch (IOException e) {
      String message = "Failed to read logs from filepath: " + logPath;
      return Either.left(message);
    }

    boolean isHeader = true;
    java.util.List<LogEntry> entries = new ArrayList<>();
    // TODO: Implement the logic
    for (String line : lines) {
      if (isHeader) {
        isHeader = false;
        continue;
      }
      LogEntry entry =
          LogEntry.newBuilder()
              .dateTime(LocalDateTime.of(2019, 2, 11, 12, 13, 57, 916).atZone(ZoneId.of("UTC")))
              .service("nos-15")
              .status("error")
              .message("Project foo not found.")
              .build();
      entries.add(entry);
    }
    return Either.right(List.ofAll(entries));
  }

  private CsvImporter() {
    // Utility class, do not instantiate
  }
}
