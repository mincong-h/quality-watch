package qwatch.logs.model;

import com.google.auto.value.AutoValue;
import java.util.regex.Pattern;

/**
 * @author Mincong Huang
 * @since 1.0
 */
@AutoValue
public abstract class LogPattern { // NOSONAR: AutoValue

  public static LogPattern.Builder newBuilder() {
    return new AutoValue_LogPattern.Builder();
  }

  public abstract int id();

  public abstract Pattern pattern();

  public abstract String longMsg();

  public abstract String shortMsg();

  public boolean matches(String s) {
    return pattern().matcher(s).matches();
  }

  @AutoValue.Builder
  public abstract static class Builder { // NOSONAR: AutoValue
    public abstract Builder id(int id);

    public abstract Builder pattern(Pattern pattern);

    public abstract Builder longMsg(String longMsg);

    public abstract Builder shortMsg(String shortMsg);

    public abstract LogPattern build();
  }
}
