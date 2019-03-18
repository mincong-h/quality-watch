package qwatch.jenkins.actor;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class TestSummarizerTest {

  @Test
  public void prefix4() {
    assertThat(TestSummarizer.prefix4("p1.p2.p3.p4.p5")).isEqualTo("p1.p2.p3.p4");
  }
}
