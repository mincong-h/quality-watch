package qwatch.jenkins.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.auto.value.AutoValue;

/**
 * Model class for test-cases created by Maven Surefire Plugin and Maven Failsafe Plugin.
 *
 * @author Mincong Huang
 * @since 1.0
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_TestCase.Builder.class)
public abstract class TestCase {

  public static Builder newBuilder() {
    return new AutoValue_TestCase.Builder();
  }

  @JacksonXmlProperty(isAttribute = true, localName = "name")
  public abstract String name();

  @JacksonXmlProperty(isAttribute = true, localName = "classname")
  public abstract String className();

  @JacksonXmlProperty(isAttribute = true, localName = "time")
  public abstract double time();

  /**
   * Creates an enriched test case instance with job name and execution id.
   *
   * @param jobName the Jenkins job name
   * @param jobExecutionId the Jenkins job execution id
   * @return an enriched test case
   */
  public EnrichedTestCase enrichWith(String jobName, int jobExecutionId) {
    return EnrichedTestCase.newBuilder(this)
        .jobName(jobName)
        .jobExecutionId(jobExecutionId)
        .build();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    @JacksonXmlProperty(isAttribute = true, localName = "name")
    public abstract Builder name(String name);

    @JacksonXmlProperty(isAttribute = true, localName = "classname")
    public abstract Builder className(String className);

    @JacksonXmlProperty(isAttribute = true, localName = "time")
    public abstract Builder time(double time);

    public abstract TestCase build();
  }
}
