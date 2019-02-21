package qwatch.logs.command;

import io.vavr.Tuple2;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeSet;
import io.vavr.control.Try;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.ImportCsvTask;
import qwatch.logs.ImportJsonTask;
import qwatch.logs.model.LogEntry;
import qwatch.logs.util.JsonExportUtil;

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
    // Create thread pool
    int nThreads = Runtime.getRuntime().availableProcessors();
    ExecutorService pool = Executors.newFixedThreadPool(nThreads);
    Set<LogEntry> entries = HashSet.empty();

    // Import existing log entries
    Set<Path> jsonPaths = HashSet.empty();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(destDir, "log*.json")) {
      for (Path csv : stream) {
        jsonPaths = jsonPaths.add(csv);
      }
    } catch (IOException e) {
      return Try.failure(e);
    }
    Set<ImportJsonTask> jsonTasks = jsonPaths.map(ImportJsonTask::new).toSet();
    try {
      for (Future<Set<LogEntry>> f : pool.invokeAll(jsonTasks.toJavaSet())) {
        if (!f.isCancelled()) {
          entries = entries.addAll(f.get());
        }
      }
    } catch (InterruptedException e) {
      logger.error("Interrupted", e);
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      logger.error("Failed to get result from future", e);
    }

    // Import new log entries
    Set<Path> csvPaths = HashSet.empty();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(csvDir, "extract-*.csv")) {
      for (Path csv : stream) {
        csvPaths = csvPaths.add(csv);
      }
    } catch (IOException e) {
      return Try.failure(e);
    }
    Set<ImportCsvTask> csvTasks = csvPaths.map(ImportCsvTask::new).toSet();
    try {
      for (Future<Set<LogEntry>> f : pool.invokeAll(csvTasks.toJavaSet())) {
        if (!f.isCancelled()) {
          entries = entries.addAll(f.get());
        }
      }
    } catch (InterruptedException e) {
      logger.error("Interrupted", e);
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      logger.error("Failed to get result from future", e);
    } finally {
      pool.shutdownNow();
    }
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
