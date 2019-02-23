package qwatch.logs.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.Map;
import io.vavr.collection.SortedSet;
import io.vavr.control.Try;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import qwatch.logs.model.LogEntry;
import qwatch.logs.util.ObjectMapperFactory;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class JsonExporter {

  private final Path logDir;
  private static final ObjectMapper mapper = ObjectMapperFactory.newObjectMapper();

  public JsonExporter(Path logDir) {
    this.logDir = logDir;
  }

  public Try<Void> export(Map<LocalDate, SortedSet<LogEntry>> entriesByDay) {
    for (var tuple : entriesByDay) {
      String filename = "log." + DateTimeFormatter.ISO_DATE.format(tuple._1) + ".json";
      Path path = logDir.resolve(filename);
      try {
        Files.deleteIfExists(path);
      } catch (IOException e) {
        return Try.failure(e);
      }
      try (FileWriter w = new FileWriter(path.toFile())) {
        mapper.writeValue(w, tuple._2.toJavaList());
      } catch (IOException e) {
        return Try.failure(e);
      }
    }
    return Try.success(null);
  }
}
