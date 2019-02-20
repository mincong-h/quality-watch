package qwatch.logs.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Try;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import qwatch.logs.model.LogEntry;

/**
 * Utility class for JSON export.
 *
 * @author Mincong Huang
 * @since 1.0
 */
public class JsonImportUtil {

  private static final ObjectMapper mapper = ObjectMapperFactory.newObjectMapper();

  public static Try<Set<LogEntry>> importLogEntries(Path dir) {
    Set<LogEntry> values = HashSet.empty();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "log*.json")) {
      for (Path jsonFile : stream) {
        Try<Set<LogEntry>> result = importLogEntriesFromFile(jsonFile);
        if (result.isFailure()) {
          return result;
        }
        values = values.addAll(result.get());
      }
    } catch (IOException e) {
      return Try.failure(e);
    }
    return Try.success(values);
  }

  static Try<Set<LogEntry>> importLogEntriesFromFile(Path path) {
    Set<LogEntry> values = HashSet.empty();
    ObjectReader reader = mapper.readerFor(LogEntry.class);
    try {
      Iterator<LogEntry> iterator = reader.readValues(path.toFile());
      while (iterator.hasNext()) {
        values = values.add(iterator.next());
      }
      return Try.success(values);
    } catch (IOException e) {
      return Try.failure(e);
    }
  }

  private JsonImportUtil() {
    // Utility class, do not instantiate
  }
}
