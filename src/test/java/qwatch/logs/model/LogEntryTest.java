package qwatch.logs.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class LogEntryTest {

  @Test
  public void summary() {
    LogEntry e1 =
        LogEntry.newBuilder()
            .host("myHost")
            .message("a\nb")
            .status("error")
            .service("myService")
            .dateTime(LocalDateTime.of(2019, 1, 2, 3, 4, 5).atZone(ZoneId.of("Z")))
            .build();
    LogEntry e2 = e1.toBuilder().message("Project foo not found.").build();

    assertThat(e1.summary()).isEqualTo("[   ] a");
    assertThat(e2.summary()).isEqualTo("[P01] Project ${id} not found");
  }
}
