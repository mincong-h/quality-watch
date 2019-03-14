package qwatch.logs.io;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.model.LogEntry;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class ImportJsonTask implements Callable<Set<LogEntry>> {

  private static final Logger logger = LoggerFactory.getLogger(ImportJsonTask.class);
  private final Path jsonPath;

  public ImportJsonTask(Path jsonPath) {
    this.jsonPath = jsonPath;
  }

  @Override
  public Set<LogEntry> call() {
    var tryImport = JsonImporter.importLogEntriesFromFile(jsonPath);
    return tryImport
        .onSuccess(s -> logger.info("{}: {} entries", jsonPath, String.format("%,d", s.size())))
        .onFailure(e -> logger.error(jsonPath + ": failed to import", e))
        .getOrElse(HashSet.empty());
  }
}
