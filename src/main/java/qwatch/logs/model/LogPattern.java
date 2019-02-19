package qwatch.logs.model;

import java.util.regex.Pattern;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public interface LogPattern {

  int id();

  Pattern pattern();

  String longMsg();

  String shortMsg();

  default boolean matches(String s) {
    return pattern().matcher(s).matches();
  }
}
