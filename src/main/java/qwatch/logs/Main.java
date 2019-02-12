package qwatch.logs;

import io.vavr.collection.List;
import io.vavr.control.Either;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.model.LogEntry;

public class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String... args) {
    String path = args[0];
    Either<String, List<LogEntry>> either = CsvImporter.importLogEntries(Paths.get(path));
    if (either.isRight()) {
      for (LogEntry entry : either.get()) {
        logger.info("{}", entry);
      }
    } else {
      logger.error("{}", either.getLeft());
    }
  }
}
