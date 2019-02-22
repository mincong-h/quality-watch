package qwatch.logs.command;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.collection.SortedSet;
import io.vavr.control.Try;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.ImportJsonTask;
import qwatch.logs.model.LogEntry;
import qwatch.logs.util.JsonImportUtil;
import qwatch.logs.util.SummaryExtractor;

/**
 * Stats Command.
 *
 * @author Mincong Huang
 * @since 1.0
 */
public class StatsCommand implements Command<Void> {

  private static final Logger logger = LoggerFactory.getLogger(StatsCommand.class);
  public static final String NAME = "stats";

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder implements CommandBuilder<StatsCommand> {

    private int topN;
    private Path logDir;

    /**
     * Sets the top N exceptions to display in the statistics.
     *
     * <p>Top N means the most frequently occurred exceptions.
     *
     * @param topN top N results to display
     * @return this
     */
    public Builder topN(int topN) {
      this.topN = topN;
      return this;
    }

    /**
     * Sets the log directory.
     *
     * @param logDir log directory
     * @return this
     */
    public Builder logDir(Path logDir) {
      this.logDir = logDir;
      return this;
    }

    @Override
    public StatsCommand build() {
      return new StatsCommand(this);
    }
  }

  private final int topN;
  private final Path logDir;

  private StatsCommand(Builder builder) {
    this.topN = builder.topN;
    this.logDir = builder.logDir;
  }

  @Override
  public Void execute() {
    Try<Set<Path>> tryListing = JsonImportUtil.listLogPaths(logDir);
    if (tryListing.isFailure()) {
      String msg = "Failed to import log entries from " + logDir;
      logger.error(msg, tryListing.getCause());
      return null;
    }

    // Create thread pool
    ExecutorService pool = Executors.newWorkStealingPool();
    Set<LogEntry> entries = HashSet.empty();
    Set<ImportJsonTask> tasks = tryListing.get().map(ImportJsonTask::new);

    // Import log entries
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
    } finally {
      pool.shutdownNow();
    }

    if (entries.nonEmpty()) {
      SortedSet<LocalDate> dates =
          entries.map(LogEntry::dateTime).map(ZonedDateTime::toLocalDate).toSortedSet();
      LocalDate start = dates.head();
      LocalDate end = dates.last();
      String size = String.format("%,d", entries.size());
      logger.info("{} entries extracted ({} to {}).", size, start, end);
    } else {
      logger.info("0 entries extracted.");
    }

    String summary = new SummaryExtractor(entries).top(topN);
    logger.info("{}", summary);
    return null;
  }
}
