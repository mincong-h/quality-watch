package qwatch.logs.command;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeSet;
import io.vavr.control.Either;
import io.vavr.control.Try;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
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

  private final Path logDir;

  private CollectCommand(Builder builder) {
    this.logDir = builder.logDir;
  }

  @Override
  public Try<Void> execute() {
    Path srcDir = Paths.get("/Users/mincong/datadog");
    Set<LogEntry> entries = JsonImportUtil.importLogEntries(srcDir);

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDir, "extract-*.csv")) {
      List<Either<String, List<LogEntry>>> results = List.empty();
      for (Path csv : stream) {
        Either<String, List<LogEntry>> result = CsvImporter.importLogEntries(csv);
        if (result.isRight()) {
          logger.info("{}: {} entries", csv, result.get().size());
        } else {
          logger.warn("{}: failed\n{}", csv, result.getLeft());
        }
        results = results.append(result);
      }
      Either<Seq<String>, Seq<List<LogEntry>>> seq = Either.sequence(results);
      if (seq.isRight()) {
        entries = entries.addAll(seq.get().flatMap(Function.identity()).toList());
        return export(entries);
      } else {
        return Try.success(null);
      }
    } catch (IOException e) {
      return Try.failure(e);
    }
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
