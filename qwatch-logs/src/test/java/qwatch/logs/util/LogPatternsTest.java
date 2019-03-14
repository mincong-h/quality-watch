package qwatch.logs.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class LogPatternsTest {

  @Test
  public void head() {
    assertThat(LogPatterns.head("1\n2")).isEqualTo("1");
    assertThat(LogPatterns.head("1\r2")).isEqualTo("1");
    assertThat(LogPatterns.head("1\r\n2")).isEqualTo("1");
  }
}
