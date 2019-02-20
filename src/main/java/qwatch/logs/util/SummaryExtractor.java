package qwatch.logs.util;

import io.vavr.collection.Traversable;
import java.util.function.Function;
import java.util.stream.Collectors;
import qwatch.logs.model.LogEntry;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class SummaryExtractor {

  private final Traversable<LogEntry> logEntries;

  public SummaryExtractor(Traversable<LogEntry> logEntries) {
    this.logEntries = logEntries;
  }

  public String top(int n) {
    String list =
        logEntries
            .groupBy(LogEntry::summary)
            .bimap(Function.identity(), Traversable::size)
            .toStream()
            .sortBy(t -> t._2 * -1)
            .take(n)
            .map(t -> String.format("- %,6d: %s", t._2, t._1))
            .collect(Collectors.joining("\n"));
    return "Top " + n + " errors:\n" + list;
  }

}
