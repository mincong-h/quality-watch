package qwatch.jenkins.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

/**
 * Enriched test case in a normal test case {@link TestCase} enriched with more information coming
 * from the test suite and build.
 *
 * @author Mincong Huang
 * @since 1.0
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_EnrichedTestCase.Builder.class)
public abstract class EnrichedTestCase {

  public static EnrichedTestCase.Builder newBuilder() {
    return new AutoValue_EnrichedTestCase.Builder();
  }

  public static EnrichedTestCase.Builder newBuilder(TestCase testCase) {
    return newBuilder().name(testCase.name()).className(testCase.className()).time(testCase.time());
  }

  /* ---------- From TestCase ---------- */

  public abstract String name();

  public abstract String className();

  public abstract double time();

  /* ---------- Enriched ---------- */

  public abstract String jobName();

  public abstract int jobExecutionId();

  public abstract String module();

  public abstract Builder toBuilder();

  @AutoValue.Builder
  public abstract static class Builder { // NOSONAR: AutoValue

    public abstract Builder name(String name);

    public abstract Builder className(String className);

    public abstract Builder time(double time);

    public abstract Builder jobName(String jobName);

    public abstract Builder jobExecutionId(int jobExecutionId);

    public abstract Builder module(String module);

    public abstract EnrichedTestCase build();
  }
}
