package qwatch.logs.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Try;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.model.LogEntry;
import qwatch.logs.util.ObjectMapperFactory;

/**
 * Utility class for JSON export.
 *
 * @author Mincong Huang
 * @since 1.0
 */
public class JsonImporter {

  private static final ObjectMapper mapper = ObjectMapperFactory.newObjectMapper();
  private static final Logger logger = LoggerFactory.getLogger(JsonImporter.class);

  static Try<Set<Path>> listLogPaths(Path dir) {
    Set<Path> paths = HashSet.empty();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "log*.json")) {
      for (Path csv : stream) {
        paths = paths.add(csv);
      }
      return Try.success(paths);
    } catch (IOException e) {
      return Try.failure(e);
    }
  }

  public static Try<Set<LogEntry>> importLogEntries(Path dir) {
    // Find paths
    var tryListing = listLogPaths(dir);
    if (tryListing.isFailure()) {
      return Try.failure(tryListing.getCause());
    }
    var paths = tryListing.get();

    // Import log entries
    Set<LogEntry> entries = HashSet.empty();
    var pool = Executors.newWorkStealingPool();
    var tasks = paths.map(ImportJsonTask::new).toSet();
    try {
      for (var future : pool.invokeAll(tasks.toJavaSet())) {
        if (!future.isCancelled()) {
          entries = entries.addAll(future.get());
        }
      }
    } catch (InterruptedException e) {
      logger.error("Interrupted", e);
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      logger.error("Failed to get result from future", e);
      return Try.failure(e);
    } finally {
      pool.shutdownNow();
    }
    return Try.success(entries);
  }

  public static Try<Set<LogEntry>> importLogEntriesFromFile(Path path) {
    Set<LogEntry> values = HashSet.empty();
    var reader = mapper.readerFor(LogEntry.class);
    try {
      Iterator<LogEntry> iterator = reader.readValues(path.toFile());
      while (iterator.hasNext()) {
        values = values.add(iterator.next());
      }
      return Try.success(values);
    } catch (IOException e) {
      return Try.failure(e);
    }
  }

  private JsonImporter() {
    // Utility class, do not instantiate
  }
}
