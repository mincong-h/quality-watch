package qwatch.logs.command;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Try;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.CsvImporter;
import qwatch.logs.model.LogEntry;

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

  private final Path logDir;

  private CollectCommand(Builder builder) {
    this.logDir = builder.logDir;
  }

  @Override
  public Try<Void> execute() {
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDir, "extract-*.csv")) {
      for (Path csv : stream) {
        Either<String, List<LogEntry>> result = CsvImporter.importLogEntries(csv);
        if (result.isRight()) {
          logger.info("{}: {} entries", csv, result.get().size());
        } else {
          logger.warn("{}: failed\n{}", csv, result.getLeft());
        }
      }
      return Try.success(null);
    } catch (IOException e) {
      return Try.failure(e);
    }
  }
}
