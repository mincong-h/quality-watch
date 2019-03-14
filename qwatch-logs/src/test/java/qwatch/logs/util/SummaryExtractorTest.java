package qwatch.logs.util;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.Before;
import org.junit.Test;
import qwatch.logs.model.LogEntry;
import qwatch.logs.model.LogSummary;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class SummaryExtractorTest {

  private Set<LogEntry> entrySet;

  @Before
  public void setUp() {
    var a1 =
        LogEntry.newBuilder()
            .host("myHost")
            .message("a")
            .status("error")
            .service("myService")
            .dateTime(LocalDateTime.of(2019, 1, 2, 3, 4, 5).atZone(ZoneId.of("Z")))
            .build();
    var a2 =
        a1.toBuilder()
            .dateTime(LocalDateTime.of(2019, 2, 2, 3, 4, 5).atZone(ZoneId.of("Z")))
            .build();
    var b1 =
        LogEntry.newBuilder()
            .host("myHost")
            .message("b")
            .status("error")
            .service("myService")
            .dateTime(LocalDateTime.of(2019, 1, 2, 3, 4, 5).atZone(ZoneId.of("Z")))
            .build();
    entrySet = HashSet.of(a1, a2, b1);
  }

  @Test
  public void top() {
    var extractor = new SummaryExtractor(entrySet);
    var summaries = extractor.top(2);
    assertThat(summaries).containsExactly(LogSummary.of(2, "[   ] a"), LogSummary.of(1, "[   ] b"));
  }
}
