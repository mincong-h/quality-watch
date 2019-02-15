package qwatch.logs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import java.time.ZonedDateTime;
import java.util.Comparator;
import qwatch.logs.util.LogPatterns;

/**
 * Log Entry is an log event in a log file.
 *
 * @author Mincong Huang
 * @since 1.0
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_LogEntry.Builder.class)
public abstract class LogEntry { // NOSONAR: AutoValue

  public static final Comparator<LogEntry> BY_DATE =
      Comparator.comparing(LogEntry::dateTime)
          .thenComparing(LogEntry::host)
          .thenComparing(LogEntry::message)
          .thenComparing(LogEntry::service)
          .thenComparing(LogEntry::status);

  public static Builder newBuilder() {
    return new AutoValue_LogEntry.Builder();
  }

  /** Column: "date" */
  @JsonProperty("date")
  public abstract ZonedDateTime dateTime();

  /** Column: "Host" */
  @JsonProperty("host")
  public abstract String host();

  /** Column: "Service" */
  @JsonProperty("service")
  public abstract String service();

  /** Column: "Status" */
  @JsonProperty("status")
  public abstract String status();

  /** Column: "message" */
  @JsonProperty("message")
  public abstract String message();

  /**
   * Gets summary of the full message.
   *
   * @return a single line summary
   */
  public abstract String summary();

  @AutoValue.Builder
  public abstract static class Builder { // NOSONAR: AutoValue
    @JsonProperty("date")
    public abstract Builder dateTime(ZonedDateTime dateTime);

    @JsonProperty("host")
    public abstract Builder host(String host);

    @JsonProperty("service")
    public abstract Builder service(String service);

    @JsonProperty("status")
    public abstract Builder status(String status);

    @JsonProperty("message")
    public abstract Builder message(String message);

    public abstract Builder summary(String summary);

    abstract String message();

    abstract LogEntry autoBuild();

    public LogEntry build() {
      String summary = LogPatterns.createSummary(message());
      summary(summary);
      return autoBuild();
    }
  }
}
