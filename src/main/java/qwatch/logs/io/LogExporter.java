package qwatch.logs.io;

import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.collection.SortedSet;
import io.vavr.control.Try;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import qwatch.logs.model.LogEntry;
import qwatch.logs.util.JsonExportUtil;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class LogExporter {

  private final Path logDir;

  public LogExporter(Path logDir) {
    this.logDir = logDir;
  }

  public Try<Void> export(Map<LocalDate, SortedSet<LogEntry>> entriesByDay) {
    for (Tuple2<LocalDate, SortedSet<LogEntry>> t : entriesByDay) {
      String filename = "log." + DateTimeFormatter.ISO_DATE.format(t._1) + ".json";
      Path path = logDir.resolve(filename);
      JsonExportUtil.export(path, t._2);
    }
    return Try.success(null);
  }
}
