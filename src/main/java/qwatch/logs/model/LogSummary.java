package qwatch.logs.model;

import com.google.auto.value.AutoValue;

/**
 * @author Mincong Huang
 * @since 1.0
 */
@AutoValue
public abstract class LogSummary {
  public static LogSummary of(long count, String description) {
    return new AutoValue_LogSummary(count, description);
  }

  public abstract long count();

  public abstract String description();
}
