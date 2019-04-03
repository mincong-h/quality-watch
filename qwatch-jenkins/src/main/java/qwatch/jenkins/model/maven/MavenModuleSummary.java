package qwatch.jenkins.model.maven;

import com.google.auto.value.AutoValue;
import java.time.Duration;
import java.time.LocalTime;

/**
 * @author Mincong Huang
 * @since 1.0
 */
@AutoValue
public abstract class MavenModuleSummary {

  public static Builder newBuilder() {
    return new AutoValue_MavenModuleSummary.Builder();
  }

  public abstract String jobName();

  public abstract int jobExecutionId();

  public abstract String moduleName();

  public abstract LocalTime startTime();

  public abstract LocalTime endTime();

  public abstract Duration duration();

  public abstract Builder toBuilder();

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder jobName(String jobName);

    public abstract Builder jobExecutionId(int jobExecutionId);

    public abstract Builder moduleName(String moduleName);

    public abstract Builder startTime(LocalTime startTime);

    public abstract Builder endTime(LocalTime endTime);

    abstract Builder duration(Duration duration);

    abstract LocalTime startTime();

    abstract LocalTime endTime();

    abstract MavenModuleSummary autoBuild();

    public MavenModuleSummary build() {
      return duration(Duration.between(startTime(), endTime())).autoBuild();
    }
  }
}
