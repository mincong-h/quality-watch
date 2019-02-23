package qwatch.logs;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.control.Either;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.model.LogEntry;
import qwatch.logs.util.CsvImporter;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class ImportCsvTask implements Callable<Set<LogEntry>> {

  private static final Logger logger = LoggerFactory.getLogger(ImportCsvTask.class);
  private final Path csv;

  public ImportCsvTask(Path csv) {
    this.csv = csv;
  }

  @Override
  public Set<LogEntry> call() {
    Either<String, List<LogEntry>> result = CsvImporter.importLogEntriesFromFile(csv);
    if (result.isRight()) {
      String size = String.format("%,d", result.get().size());
      logger.info("{}: {} entries", csv, size);
      return result.get().toSet();
    } else {
      logger.warn("{}: failed\n{}", csv, result.getLeft());
      return HashSet.empty();
    }
  }
}
