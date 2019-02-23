package qwatch.logs.util;

import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import java.util.function.Function;
import qwatch.logs.model.LogEntry;
import qwatch.logs.model.LogSummary;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class SummaryExtractor {

  private final Traversable<LogEntry> logEntries;

  public SummaryExtractor(Traversable<LogEntry> logEntries) {
    this.logEntries = logEntries;
  }

  public List<LogSummary> top(int n) {
    return logEntries
        .groupBy(LogEntry::summary)
        .bimap(Function.identity(), Traversable::size)
        .toStream()
        .sortBy(t -> t._2 * -1)
        .take(n)
        .map(t -> LogSummary.of(t._2, t._1))
        .toList();
  }
}
