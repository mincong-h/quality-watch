package qwatch.logs.command;

import io.vavr.collection.Map;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeSet;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.io.CsvImporter;
import qwatch.logs.io.JsonExporter;
import qwatch.logs.io.JsonImporter;
import qwatch.logs.model.LogEntry;

/**
 * Collect command.
 *
 * @author Mincong Huang
 * @since 1.0
 */
public class CollectCommand implements Command<Void> {

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
  public Void execute() {
    // Import existing log entries
    var tryImport = JsonImporter.importLogEntries(destDir);
    if (tryImport.isFailure()) {
      logger.error("Failed to import JSON files", tryImport.getCause());
      return null;
    }
    var entries = tryImport.get();

    // Import new log entries
    var eitherImport = CsvImporter.importLogEntries(csvDir);
    if (eitherImport.isLeft()) {
      logger.error(eitherImport.getLeft());
    }
    entries = entries.addAll(eitherImport.get());

    // Export
    Map<LocalDate, SortedSet<LogEntry>> entriesByDay =
        entries
            .groupBy(entry -> entry.dateTime().toLocalDate())
            .mapValues(v -> TreeSet.ofAll(LogEntry.BY_DATE, v));
    var exportResult = new JsonExporter(destDir).export(entriesByDay);
    if (exportResult.isFailure()) {
      logger.error("Failed to export", exportResult.getCause());
    }
    return null;
  }
}
