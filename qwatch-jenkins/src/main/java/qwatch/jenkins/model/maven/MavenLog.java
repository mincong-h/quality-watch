package qwatch.jenkins.model.maven;

import com.google.auto.value.AutoValue;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import java.time.LocalTime;
import java.util.function.Function;
import qwatch.jenkins.model.RawLog;

import static qwatch.jenkins.model.maven.MavenLog.Level.ERROR;
import static qwatch.jenkins.model.maven.MavenLog.Level.INFO;
import static qwatch.jenkins.model.maven.MavenLog.Level.WARN;

/**
 * @author Mincong Huang
 * @since 1.0
 */
@AutoValue
public abstract class MavenLog {
  public enum Level {
    INFO("INFO"),
    ERROR("ERROR"),
    WARN("WARNING");

    private static Map<String, Level> map =
        HashSet.of(Level.values()).toMap(Level::representation, Function.identity());

    public static Option<Level> getOptLevel(String representation) {
      return map.get(representation);
    }

    private final String repr;
    private final String marker;

    Level(String representation) {
      this.repr = representation;
      this.marker = '[' + representation + ']';
    }

    public String representation() {
      return repr;
    }

    public String marker() {
      return marker;
    }
  }

  /* ---------- Factory Methods ---------- */

  public static MavenLog parseTrusted(RawLog rawLog) {
    var msg = rawLog.message();
    if (msg.startsWith(INFO.marker)) {
      return info(msg.substring(INFO.marker.length())).localTime(rawLog.localTime()).build();
    }
    if (msg.startsWith(WARN.marker)) {
      return warn(msg.substring(WARN.marker.length())).localTime(rawLog.localTime()).build();
    }
    if (msg.startsWith(ERROR.marker)) {
      return warn(msg.substring(ERROR.marker.length())).localTime(rawLog.localTime()).build();
    }
    throw new IllegalStateException("This should never happen. Unable to parse line: " + rawLog);
  }

  public static Builder info(String msg) {
    return of(INFO, msg);
  }

  public static Builder warn(String msg) {
    return of(Level.WARN, msg);
  }

  public static Builder error(String msg) {
    return of(Level.ERROR, msg);
  }

  public static Builder of(Level level, String msg) {
    return new AutoValue_MavenLog.Builder().level(level).message(msg);
  }

  /* ---------- Getter Methods ---------- */

  public abstract Level level();

  public abstract String message();

  public abstract LocalTime localTime();

  /* ---------- Transform Methods ---------- */

  public MavenLog extendMsg(String extraMsg) {
    return toBuilder().message(message() + '\n' + extraMsg).build();
  }

  public abstract Builder toBuilder();

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder level(Level level);

    public Builder level(String level) {
      return Level.getOptLevel(level)
          .map(this::level)
          .getOrElseThrow(() -> new IllegalArgumentException("Unknown level: " + level));
    }

    public abstract Builder message(String message);

    public abstract Builder localTime(LocalTime localTime);

    public abstract MavenLog build();
  }
}
