package qwatch.logs.util;

import qwatch.logs.model.BuiltinLogPattern;
import qwatch.logs.model.LogPattern;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class LogPatterns {

  /**
   * Creates an abbreviation for a full message.
   *
   * @param fullMessage full message
   * @return abbreviation
   */
  public static String createSummary(String fullMessage) {
    String head = head(fullMessage);
    for (LogPattern pattern : BuiltinLogPattern.values()) {
      if (pattern.matches(head)) {
        return pattern.longMsg();
      }
    }
    return head;
  }

  static String head(String message) {
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
