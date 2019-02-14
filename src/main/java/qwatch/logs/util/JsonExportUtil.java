package qwatch.logs.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.SortedSet;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.model.LogEntry;

/**
 * Utility class for JSON export.
 *
 * @author Mincong Huang
 * @since 1.0
 */
public class JsonExportUtil {

  private static final Logger logger = LoggerFactory.getLogger(JsonExportUtil.class);
  private static final ObjectMapper mapper = ObjectMapperFactory.newObjectMapper();

  public static void export(Path path, SortedSet<LogEntry> logEntries) {
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      logger.error("{}", e);
    }
    try (FileWriter w = new FileWriter(path.toFile())) {
      mapper.writeValue(w, logEntries.toJavaList());
    } catch (IOException e) {
      logger.error("{}", e);
    }
  }

  private JsonExportUtil() {
    // Utility class, do not instantiate
  }
}
