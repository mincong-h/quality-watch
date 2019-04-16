package qwatch.jenkins.model.maven;

import com.google.auto.value.AutoValue;
import java.time.Duration;
import java.time.LocalTime;

/**
 * Maven plugin execution summary.
 *
 * @author Mincong Huang
 * @since 1.0
 */
@AutoValue
public abstract class MavenPluginExecSummary {

  public static Builder newBuilder() {
    return new AutoValue_MavenPluginExecSummary.Builder();
  }

  public abstract String jobName();

  public abstract int jobExecId();

  public abstract String pluginName();

  public abstract String pluginVersion();

  public abstract String pluginGoal();

  public abstract String pluginExecId();

  public abstract String moduleId();

  public abstract String moduleName();

  public abstract LocalTime startTime();

  public abstract LocalTime endTime();

  public abstract Duration duration();

  public abstract Builder toBuilder();

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder jobName(String jobName);

    public abstract Builder jobExecId(int jobExecId);

    public abstract Builder pluginName(String pluginName);

    public abstract Builder pluginVersion(String pluginVersion);

    public abstract Builder pluginGoal(String pluginGoal);

    public abstract Builder pluginExecId(String pluginExecId);

    public abstract Builder moduleId(String moduleId);

    public abstract Builder moduleName(String moduleName);

    public abstract Builder startTime(LocalTime startTime);

    public abstract Builder endTime(LocalTime endTime);

    abstract Builder duration(Duration duration);

    abstract LocalTime startTime();

    abstract LocalTime endTime();

    abstract MavenPluginExecSummary autoBuild();

    public MavenPluginExecSummary build() {
      return duration(Duration.between(startTime(), endTime())).autoBuild();
    }
  }
}
