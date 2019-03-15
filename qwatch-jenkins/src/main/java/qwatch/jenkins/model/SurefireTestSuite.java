package qwatch.jenkins.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.auto.value.AutoValue;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Mincong Huang
 * @since 1.0
 */
@AutoValue
@JacksonXmlRootElement(localName = "testsuite")
@JsonDeserialize(builder = AutoValue_SurefireTestSuite.Builder.class)
public abstract class SurefireTestSuite {

  public static Builder newBuilder() {
    return new AutoValue_SurefireTestSuite.Builder();
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

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "testcase")
  public abstract List<SurefireTestCase> testCases();

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

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "testcase")
    public abstract Builder testCases(Set<SurefireTestCase> testCases);

    public Builder testCases(SurefireTestCase... testCases) {
      return testCases(new HashSet<>(Arrays.asList(testCases)));
    }

    public abstract SurefireTestSuite build();
  }
}
