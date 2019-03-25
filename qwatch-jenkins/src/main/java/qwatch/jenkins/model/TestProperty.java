package qwatch.jenkins.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.auto.value.AutoValue;

/**
 * Model class for test-property created by Maven Surefire Plugin and Maven Failsafe Plugin.
 *
 * @author Mincong Huang
 * @since 1.0
 */
@AutoValue
@JacksonXmlRootElement(localName = "property")
@JsonDeserialize(builder = AutoValue_TestProperty.Builder.class)
public abstract class TestProperty {

  public static TestProperty of(String name, String value) {
    return new AutoValue_TestProperty.Builder().name(name).value(value).build();
  }

  @JacksonXmlProperty(isAttribute = true, localName = "name")
  public abstract String name();

  @JacksonXmlProperty(isAttribute = true, localName = "value")
  public abstract String value();

  @AutoValue.Builder
  public abstract static class Builder {

    @JacksonXmlProperty(isAttribute = true, localName = "name")
    public abstract Builder name(String name);

    @JacksonXmlProperty(isAttribute = true, localName = "value")
    public abstract Builder value(String value);

    public abstract TestProperty build();
  }
}
