package qwatch.jenkins.model;

import com.google.auto.value.AutoValue;
import java.time.LocalTime;

/**
 * @author Mincong Huang
 * @since 1.0
 */
@AutoValue
public abstract class RawLog {

  public static RawLog parseTrusted(String line) {
    var t = LocalTime.parse(line.substring(0, 8));
    // ignore the chat at 8th position:
    // space between local time and message
    var s = line.substring(9);
    return of(t, s);
  }

  public static RawLog of(LocalTime localTime, String msg) {
    return new AutoValue_RawLog.Builder().localTime(localTime).message(msg).build();
  }

  public abstract String message();

  public abstract LocalTime localTime();

  public abstract Builder toBuilder();

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder message(String message);

    public abstract Builder localTime(LocalTime localTime);

    public abstract RawLog build();
  }
}
