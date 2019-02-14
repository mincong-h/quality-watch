package qwatch.logs.command;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class CollectCommandTest {

  @Test
  public void newBuilder() {
    assertThat(CollectCommand.newBuilder()).isInstanceOf(CommandBuilder.class);
  }
}
