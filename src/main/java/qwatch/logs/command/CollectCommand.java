package qwatch.logs.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import io.vavr.control.Try;
import java.io.FileWriter;
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
import qwatch.logs.CsvImporter;
import qwatch.logs.model.LogEntry;
import qwatch.logs.util.ObjectMapperFactory;

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

  public static class Builder {
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

    public CollectCommand build() {
      return new CollectCommand(this);
    }
  }

  private final ObjectMapper objectMapper = ObjectMapperFactory.newObjectMapper();
  private final Path logDir;

  private CollectCommand(Builder builder) {
    this.logDir = builder.logDir;
  }

  @Override
  public Try<Void> execute() {
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
        return export(seq.get());
      } else {
        return Try.success(null);
      }
    } catch (IOException e) {
      return Try.failure(e);
    }
  }

  private Try<Void> export(Seq<List<LogEntry>> logsSequence) {
    Map<LocalDate, List<LogEntry>> entriesByDay =
        logsSequence
            .flatMap(Function.identity())
            .toList()
            .groupBy(entry -> entry.dateTime().toLocalDate());
    for (Tuple2<LocalDate, List<LogEntry>> t : entriesByDay) {
      String filename = "log." + DateTimeFormatter.ISO_DATE.format(t._1) + ".json";
      Path path = Paths.get("/Users/mincong/Desktop/datadog").resolve(filename);
      try {
        Files.deleteIfExists(path);
      } catch (IOException e) {
        logger.error("{}", e);
      }
      try (FileWriter w = new FileWriter(path.toFile())) {
        objectMapper.writeValue(w, t._2.sortBy(LogEntry::dateTime).toJavaList());
      } catch (IOException e) {
        logger.error("{}", e);
      }
    }
    return Try.success(null);
  }
}
