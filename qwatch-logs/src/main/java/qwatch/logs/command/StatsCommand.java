package qwatch.logs.command;

import io.vavr.collection.List;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.io.JsonImporter;
import qwatch.logs.model.LogEntry;
import qwatch.logs.model.LogSummary;
import qwatch.logs.util.SummaryExtractor;

/**
 * Stats Command.
 *
 * @author Mincong Huang
 * @since 1.0
 */
public class StatsCommand implements Command<List<LogSummary>> {

  private static final Logger logger = LoggerFactory.getLogger(StatsCommand.class);
  public static final String NAME = "stats";

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder implements CommandBuilder<StatsCommand> {

    private int topN;
    private long days = -1;
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

    /**
     * Sets the last N days to be included in the statistics.
     *
     * <p>By default, all log entries included.
     *
     * @param days the last N days (must be positive)
     * @return this
     */
    public Builder days(long days) {
      this.days = days;
      return this;
    }

    @Override
    public StatsCommand build() {
      return new StatsCommand(this);
    }
  }

  private final int topN;
  private final long days;
  private final Path logDir;

  private StatsCommand(Builder builder) {
    this.topN = builder.topN;
    this.days = builder.days;
    this.logDir = builder.logDir;
  }

  @Override
  public List<LogSummary> execute() {
    // Import log entries
    var tryImport = JsonImporter.importLogEntries(logDir);
    if (tryImport.isFailure()) {
      logger.error("Failed to import JSON files", tryImport.getCause());
      return List.empty();
    }
    var entries = tryImport.get();
    if (entries.nonEmpty()) {
      var dates = entries.map(LogEntry::dateTime).map(ZonedDateTime::toLocalDate).toSortedSet();
      var end = dates.last(); // inclusive
      LocalDate start; // inclusive
      if (days >= 0 && dates.head().isBefore(end.minusDays(days - 1))) {
        start = end.minusDays(days - 1);
      } else {
        start = dates.head();
      }
      entries = entries.filter(e -> !e.dateTime().toLocalDate().isBefore(start));
      var size = String.format("%,d", entries.size());
      logger.info("{} entries extracted ({} to {}).", size, start, end);
    } else {
      logger.info("0 entries extracted.");
    }

    // Summary
    var summaries = new SummaryExtractor(entries).top(topN);
    var detail =
        summaries
            .map(s -> String.format("- %,6d: %s", s.count(), s.description()))
            .collect(Collectors.joining("\n"));
    logger.info("Top {} errors:\n{}", summaries.size(), detail);
    return summaries;
  }
}
