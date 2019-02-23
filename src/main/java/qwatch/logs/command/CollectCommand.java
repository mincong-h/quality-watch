package qwatch.logs.command;

import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeSet;
import io.vavr.control.Either;
import io.vavr.control.Try;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.model.LogEntry;
import qwatch.logs.util.CsvImporter;
import qwatch.logs.util.JsonExportUtil;
import qwatch.logs.util.JsonImportUtil;

/**
 * Collect command.
 *
 * @author Mincong Huang
 * @since 1.0
 */
public class CollectCommand implements Command<Try<Void>> {

  private static final Logger logger = LoggerFactory.getLogger(CollectCommand.class);
  public static final String NAME = "collect";

  public static CollectCommand.Builder newBuilder() {
    return new Builder();
  }

  public static class Builder implements CommandBuilder<CollectCommand> {
    private Path logDir;

    private Builder() {}

    /**
     * The directory path where logs (CSV) are stored.
     *
     * @param logDir log directory
     * @return this
     */
    public Builder logDir(Path logDir) {
      this.logDir = logDir;
      return this;
    }

    @Override
    public CollectCommand build() {
      return new CollectCommand(this);
    }
  }

  private final Path csvDir;
  private final Path destDir = Paths.get("/Users/mincong/datadog");

  private CollectCommand(Builder builder) {
    this.csvDir = builder.logDir;
  }

  @Override
  public Try<Void> execute() {
    Set<LogEntry> entries;

    // Import existing log entries
    Try<Set<LogEntry>> tryImport = JsonImportUtil.importLogEntries(destDir);
    if (tryImport.isFailure()) {
      logger.error("Failed to import JSON files", tryImport.getCause());
      return null;
    }
    entries = tryImport.get();

    // Import new log entries
    Either<String, Set<LogEntry>> eitherImport = CsvImporter.importLogEntries(csvDir);
    if (eitherImport.isLeft()) {
      logger.error(eitherImport.getLeft());
    }
    entries = entries.addAll(eitherImport.get());
    return export(entries);
  }

  private Try<Void> export(Set<LogEntry> logEntries) {
    Map<LocalDate, SortedSet<LogEntry>> entriesByDay =
        logEntries
            .groupBy(entry -> entry.dateTime().toLocalDate())
            .mapValues(v -> TreeSet.ofAll(LogEntry.BY_DATE, v));
    for (Tuple2<LocalDate, SortedSet<LogEntry>> t : entriesByDay) {
      String filename = "log." + DateTimeFormatter.ISO_DATE.format(t._1) + ".json";
      Path path = Paths.get("/Users/mincong/datadog").resolve(filename);
      JsonExportUtil.export(path, t._2);
    }
    return Try.success(null);
  }
}
