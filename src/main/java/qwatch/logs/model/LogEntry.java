package qwatch.logs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import java.time.ZonedDateTime;

/**
 * Log Entry is an log event in a log file.
 *
 * @author Mincong Huang
 * @since 1.0
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_LogEntry.Builder.class)
public abstract class LogEntry { // NOSONAR: AutoValue

  public static Builder newBuilder() {
    return new AutoValue_LogEntry.Builder();
  }

  /** Column: "date" */
  @JsonProperty("date")
  public abstract ZonedDateTime dateTime();

  /** Column: "Service" */
  @JsonProperty("service")
  public abstract String service();

  /** Column: "Status" */
  @JsonProperty("status")
  public abstract String status();

  /** Column: "message" */
  @JsonProperty("message")
  public abstract String message();

  public abstract Builder toBuilder();

  @AutoValue.Builder
  public abstract static class Builder { // NOSONAR: AutoValue
    @JsonProperty("date")
    public abstract Builder dateTime(ZonedDateTime dateTime);

    @JsonProperty("service")
    public abstract Builder service(String service);

    @JsonProperty("status")
    public abstract Builder status(String status);

    @JsonProperty("message")
    public abstract Builder message(String message);

    public abstract LogEntry build();
  }
}
