package qwatch.jenkins.model.maven;

import com.google.auto.value.AutoValue;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import java.util.function.Function;

/**
 * @author Mincong Huang
 * @since 1.0
 */
@AutoValue
public abstract class RawLog {
  public enum Level {
    INFO("INFO"),
    ERROR("ERROR"),
    WARN("WARNING");

    private static Map<String, Level> map =
        HashSet.of(Level.values()).toMap(Level::representation, Function.identity());

    public static Option<Level> getOptLevel(String representation) {
      return map.get(representation);
    }

    private final String representation;

    Level(String representation) {
      this.representation = representation;
    }

    public String representation() {
      return representation;
    }
  }

  /* ---------- Factory Methods ---------- */

  public static RawLog info(String msg) {
    return of(Level.INFO, msg);
  }

  public static RawLog warn(String msg) {
    return of(Level.WARN, msg);
  }

  public static RawLog error(String msg) {
    return of(Level.ERROR, msg);
  }

  public static RawLog of(Level level, String msg) {
    return new AutoValue_RawLog.Builder().level(level).message(msg).build();
  }

  /* ---------- Getter Methods ---------- */

  public abstract Level level();

  public abstract String message();

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder level(Level level);

    public Builder level(String level) {
      return Level.getOptLevel(level)
          .map(this::level)
          .getOrElseThrow(() -> new IllegalArgumentException("Unknown level: " + level));
    }

    public abstract Builder message(String message);

    public abstract RawLog build();
  }
}
