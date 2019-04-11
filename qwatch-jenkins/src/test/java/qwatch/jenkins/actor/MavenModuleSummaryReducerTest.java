package qwatch.jenkins.actor;

import io.vavr.collection.List;
import java.time.LocalTime;
import org.junit.Test;
import qwatch.jenkins.model.maven.MavenLog;
import qwatch.jenkins.model.maven.MavenModuleSummary;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class MavenModuleSummaryReducerTest {

  @Test
  public void reduce() {
    MavenLog[] logs = {
      log(0, "Scanning for projects..."),
      log(1, "Downloading: http://example.com/plugin-2.0.6.pom"),
      log(1, "Downloaded: http://example.com/plugin-2.0.6.pom (0 B at 0.0 KB/sec)"),
      log(1, "------------------------------------------------------------------------"),
      log(2, "Reactor Build Order:"),
      log(2, ""),
      log(2, "Module 1"),
      log(2, "Module 2"),
      log(2, "Module 3"),
      log(3, ""),
      log(3, "------------------------------------------------------------------------"),
      log(4, "Building Module 1 1.0.0-SNAPSHOT"),
      log(4, "------------------------------------------------------------------------"),
      log(4, "Downloading: http://example.com/plugin-2.0.6.pom"),
      log(5, "Downloaded: http://example.com/plugin-2.0.6.pom (0 B at 0.0 KB/sec)"),
      log(6, "--- maven-clean-plugin:2.5:clean (default-clean) @ parent ---"),
      log(6, ""),
      log(6, "------------------------------------------------------------------------"),
      log(7, "Building Module 2 1.0.0-SNAPSHOT"),
      log(7, "------------------------------------------------------------------------"),
      log(7, ""),
      log(8, "--- maven-clean-plugin:2.5:clean (default-clean) @ parent ---"),
      log(9, ""),
      log(9, "------------------------------------------------------------------------"),
      log(10, "Building Module 3 1.0.0-SNAPSHOT"),
      log(11, "------------------------------------------------------------------------"),
      log(11, ""),
      log(12, "--- maven-clean-plugin:2.5:clean (default-clean) @ parent ---"),
      log(12, ""),
      log(12, "------------------------------------------------------------------------"),
      log(13, "Reactor Summary:"),
      log(13, ""),
      log(13, "Module 1 ........................................... SUCCESS [05:11 min]"),
      log(13, "Module 2 ........................................... SUCCESS [  3.909 s]"),
      log(13, "Module 3 ........................................... SUCCESS [ 16.844 s]"),
      log(13, "------------------------------------------------------------------------"),
      log(13, "BUILD SUCCESS"),
      log(13, "------------------------------------------------------------------------"),
      log(13, "Total time: 02:11 h"),
      log(13, "Finished at: 2019-03-25T17:06:52+00:00"),
      log(13, "Final Memory: 3710M/6185M"),
      log(13, "------------------------------------------------------------------------"),
    };
    var summaries = MavenModuleSummaryReducer.reduce("myJob", 123, List.of(logs));
    var m1 =
        MavenModuleSummary.newBuilder()
            .jobName("myJob")
            .jobExecutionId(123)
            .moduleName("Module 1 1.0.0-SNAPSHOT")
            .startTime(LocalTime.of(16, 55, 4))
            .endTime(LocalTime.of(16, 55, 6))
            .build();
    var m2 =
        MavenModuleSummary.newBuilder()
            .jobName("myJob")
            .jobExecutionId(123)
            .moduleName("Module 2 1.0.0-SNAPSHOT")
            .startTime(LocalTime.of(16, 55, 7))
            .endTime(LocalTime.of(16, 55, 9))
            .build();
    var m3 =
        MavenModuleSummary.newBuilder()
            .jobName("myJob")
            .jobExecutionId(123)
            .moduleName("Module 3 1.0.0-SNAPSHOT")
            .startTime(LocalTime.of(16, 55, 10))
            .endTime(LocalTime.of(16, 55, 12))
            .build();
    assertThat(summaries.toJavaSet()).containsExactly(m1, m2, m3);
  }

  private static MavenLog log(int second, String message) {
    return MavenLog.info(message).localTime(LocalTime.of(16, 55, second)).build();
  }
}
