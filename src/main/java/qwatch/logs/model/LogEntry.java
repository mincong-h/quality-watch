package qwatch.logs.model;

import com.google.auto.value.AutoValue;
import java.time.ZonedDateTime;

/**
 * Log Entry is an log event in a log file.
 *
 * @author Mincong Huang
 * @since 1.0
 */
@AutoValue
public abstract class LogEntry { // NOSONAR: AutoValue

  public static Builder newBuilder() {
    return new AutoValue_LogEntry.Builder();
  }

  /** Column: "date" */
  public abstract ZonedDateTime dateTime();

  /** Column: "Service" */
  public abstract String service();

  /** Column: "Status" */
  public abstract String status();

  /** Column: "message" */
  public abstract String message();

  public abstract Builder toBuilder();

  @AutoValue.Builder
  public abstract static class Builder { // NOSONAR: AutoValue

    public abstract Builder dateTime(ZonedDateTime dateTime);

    public abstract Builder service(String service);

    public abstract Builder status(String status);

    public abstract Builder message(String message);

    public abstract LogEntry build();
  }
}
