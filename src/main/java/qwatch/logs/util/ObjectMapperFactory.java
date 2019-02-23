package qwatch.logs.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class ObjectMapperFactory {

  public static ObjectMapper newObjectMapper() {
    var mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module());
    mapper.registerModule(new ParameterNamesModule());
    mapper.registerModule(new JavaTimeModule());

    // ISO-8601 datetime
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.setDateFormat(new StdDateFormat());

    // Pretty JSON
    mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    return mapper;
  }

  private ObjectMapperFactory() {
    // Utility class, do not instantiate
  }
}
