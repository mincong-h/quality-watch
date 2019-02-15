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
            .groupBy(e -> abbr(e.message()))
            .bimap(Function.identity(), Traversable::size)
            .toStream()
            .sortBy(t -> t._2 * -1)
            .take(n)
            .map(t -> "- " + t._2 + ": " + t._1)
            .collect(Collectors.joining("\n"));
    return "Top " + n + " errors:\n" + list;
  }

  String abbr(String message) {
    String head = head(message);
    if (head.length() > 50) {
      return head.substring(0, 50) + "...";
    } else {
      return head;
    }
  }

  String head(String message) {
    int r = message.indexOf('\r');
    if (r > 0) {
      return message.substring(0, r);
    }
    int n = message.indexOf('\n');
    if (n > 0) {
      return message.substring(0, n);
    }
    return message;
  }
}
