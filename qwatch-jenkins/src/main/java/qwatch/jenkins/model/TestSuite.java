package qwatch.jenkins.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.auto.value.AutoValue;
import java.util.Optional;
import java.util.Set;

/**
 * @author Mincong Huang
 * @since 1.0
 */
@AutoValue
@JacksonXmlRootElement(localName = "testsuite")
@JsonDeserialize(builder = AutoValue_TestSuite.Builder.class)
public abstract class TestSuite {

  public static Builder newBuilder() {
    return new AutoValue_TestSuite.Builder();
  }

  /* ---------- Attributes ---------- */

  @JacksonXmlProperty(localName = "name", isAttribute = true)
  public abstract String name();

  @JacksonXmlProperty(localName = "time", isAttribute = true)
  public abstract double time();

  @JacksonXmlProperty(localName = "tests", isAttribute = true)
  public abstract int testCount();

  @JacksonXmlProperty(localName = "errors", isAttribute = true)
  public abstract int errorCount();

  @JacksonXmlProperty(localName = "skipped", isAttribute = true)
  public abstract int skippedCount();

  @JacksonXmlProperty(localName = "failures", isAttribute = true)
  public abstract int failureCount();

  /* ---------- Elements ---------- */

  @JacksonXmlElementWrapper
  @JacksonXmlProperty(localName = "properties")
  public abstract Set<TestProperty> properties();

  public Set<TestCase> testCases() {
    return optTestCases().orElse(Set.of());
  }

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "testcase")
  abstract Optional<Set<TestCase>> optTestCases();

  @AutoValue.Builder
  public abstract static class Builder {

    /* ---------- Attributes ---------- */

    @JacksonXmlProperty(localName = "name", isAttribute = true)
    public abstract Builder name(String name);

    @JacksonXmlProperty(localName = "time", isAttribute = true)
    public abstract Builder time(double time);

    @JacksonXmlProperty(localName = "tests", isAttribute = true)
    public abstract Builder testCount(int testCount);

    @JacksonXmlProperty(localName = "errors", isAttribute = true)
    public abstract Builder errorCount(int errorCount);

    @JacksonXmlProperty(localName = "skipped", isAttribute = true)
    public abstract Builder skippedCount(int skippedCount);

    @JacksonXmlProperty(localName = "failures", isAttribute = true)
    public abstract Builder failureCount(int failureCount);

    /* ---------- Elements ---------- */

    @JacksonXmlElementWrapper
    @JacksonXmlProperty(localName = "properties")
    public abstract Builder properties(Set<TestProperty> properties);

    public Builder properties(TestProperty... properties) {
      return properties(Set.of(properties));
    }

    public abstract Builder optTestCases(Optional<Set<TestCase>> testCases);

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "testcase")
    public Builder testCases(Set<TestCase> testCases) {
      return optTestCases(Optional.of(testCases));
    }

    public Builder testCases(TestCase... testCases) {
      return testCases(Set.of(testCases));
    }

    abstract Optional<Set<TestCase>> optTestCases();

    abstract TestSuite autoBuild();

    public TestSuite build() {
      testCases(optTestCases().orElse(Set.of()));
      return autoBuild();
    }
  }
}
