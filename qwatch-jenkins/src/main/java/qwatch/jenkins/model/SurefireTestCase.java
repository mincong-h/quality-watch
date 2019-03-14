package qwatch.jenkins.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.auto.value.AutoValue;

/**
 * @author Mincong Huang
 * @since 1.0
 */
@AutoValue
//@JacksonXmlRootElement(localName = "testcase")
@JsonDeserialize(builder = AutoValue_SurefireTestCase.Builder.class)
public abstract class SurefireTestCase {

  public static Builder newBuilder() {
    return new AutoValue_SurefireTestCase.Builder();
  }

  @JacksonXmlProperty(isAttribute = true, localName = "name")
  public abstract String name();

  @JacksonXmlProperty(isAttribute = true, localName = "classname")
  public abstract String className();

  @JacksonXmlProperty(isAttribute = true, localName = "time")
  public abstract double time();

  @AutoValue.Builder
  public abstract static class Builder {

    @JacksonXmlProperty(isAttribute = true, localName = "name")
    public abstract Builder name(String name);

    @JacksonXmlProperty(isAttribute = true, localName = "classname")
    public abstract Builder className(String className);

    @JacksonXmlProperty(isAttribute = true, localName = "time")
    public abstract Builder time(double time);

    public abstract SurefireTestCase build();
  }
}
