package qwatch.logs.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeSet;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.model.LogEntry;

/**
 * Utility class for JSON export.
 *
 * @author Mincong Huang
 * @since 1.0
 */
public class JsonImportUtil {

  private static final Logger logger = LoggerFactory.getLogger(JsonImportUtil.class);
  private static final ObjectMapper mapper = ObjectMapperFactory.newObjectMapper();

  public static SortedSet<LogEntry> importLogEntries(Path dir) {
    TreeSet<LogEntry> values = TreeSet.empty(Comparator.comparing(LogEntry::dateTime));
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "log*.json")) {
      for (Path jsonFile : stream) {
        values = values.addAll(importLogEntriesFromFile(jsonFile));
      }
    } catch (IOException e) {
      logger.error("Failed to list files in directory: " + dir, e);
    }
    return values;
  }

  private static SortedSet<LogEntry> importLogEntriesFromFile(Path path) {
    TreeSet<LogEntry> values = TreeSet.empty(Comparator.comparing(LogEntry::dateTime));
    ObjectReader reader = mapper.readerFor(LogEntry.class);
    try {
      Iterator<LogEntry> iterator = reader.readValues(path.toFile());
      while (iterator.hasNext()) {
        values = values.add(iterator.next());
      }
    } catch (IOException e) {
      logger.error("Failed to read file " + path, e);
    }
    return values;
  }

  private JsonImportUtil() {
    // Utility class, do not instantiate
  }
}
