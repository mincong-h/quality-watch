package qwatch.logs.util;

import io.vavr.control.Option;
import qwatch.logs.model.BuiltinLogPattern;
import qwatch.logs.model.LogPattern;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class LogPatterns {

  /**
   * Finds log pattern for the given message.
   *
   * @param fullMessage full message
   * @return an optional log pattern
   */
  public static Option<LogPattern> findPattern(String fullMessage) {
    String head = head(fullMessage);
    for (LogPattern p : BuiltinLogPattern.values()) {
      if (p.matches(head)) {
        return Option.of(p);
      }
    }
    return Option.none();
  }

  public static String head(String message) {
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

  private LogPatterns() {
    // Utility class, do not instantiate
  }
}
