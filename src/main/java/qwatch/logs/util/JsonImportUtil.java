package qwatch.logs.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Try;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.ImportJsonTask;
import qwatch.logs.model.LogEntry;

/**
 * Utility class for JSON export.
 *
 * @author Mincong Huang
 * @since 1.0
 */
public class JsonImportUtil {

  private static final ObjectMapper mapper = ObjectMapperFactory.newObjectMapper();
  private static final Logger logger = LoggerFactory.getLogger(JsonImportUtil.class);

  public static Try<Set<Path>> listLogPaths(Path dir) {
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
    Set<Path> paths = HashSet.empty();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "log*.json")) {
      for (Path p : stream) {
        paths = paths.add(p);
      }
    } catch (IOException e) {
      return Try.failure(e);
    }
    // Import log entries
    Set<LogEntry> entries = HashSet.empty();
    ExecutorService pool = Executors.newWorkStealingPool();
    Set<ImportJsonTask> tasks = paths.map(ImportJsonTask::new).toSet();
    try {
      for (Future<Set<LogEntry>> f : pool.invokeAll(tasks.toJavaSet())) {
        if (!f.isCancelled()) {
          entries = entries.addAll(f.get());
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
    ObjectReader reader = mapper.readerFor(LogEntry.class);
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

  private JsonImportUtil() {
    // Utility class, do not instantiate
  }
}
