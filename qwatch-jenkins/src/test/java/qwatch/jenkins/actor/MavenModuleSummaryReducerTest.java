package qwatch.jenkins.actor;

import io.vavr.collection.List;
import java.time.LocalTime;
import org.junit.Test;
import qwatch.jenkins.model.maven.MavenLog;
import qwatch.jenkins.model.maven.MavenPluginExecSummary;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class MavenModuleSummaryReducerTest {

  @Test
  public void pluginDeclarationPattern() {
    var p = MavenModuleSummaryReducer.PLUGIN_DECLARATION_PATTERN;
    var m = p.matcher("--- maven-clean-plugin:2.5:clean (default-clean) @ module1 ---");
    assertThat(m.matches()).isTrue();
    assertThat(m.group(1)).isEqualTo("maven-clean-plugin");
    assertThat(m.group(2)).isEqualTo("2.5");
    assertThat(m.group(3)).isEqualTo("clean");
    assertThat(m.group(4)).isEqualTo("default-clean");
    assertThat(m.group(5)).isEqualTo("module1");
  }

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
      log(3, ""),
      log(3, "------------------------------------------------------------------------"),
      log(4, "Building Module 1 1.0.0-SNAPSHOT"),
      log(4, "------------------------------------------------------------------------"),
      log(4, "Downloading: http://example.com/plugin-2.0.6.pom"),
      log(5, "Downloaded: http://example.com/plugin-2.0.6.pom (0 B at 0.0 KB/sec)"),
      log(6, "--- maven-clean-plugin:2.5:clean (default-clean) @ module1 ---"),
      log(7, ""),
      log(8, "--- maven-install-plugin:2.4:install (default-install) @ module1 ---"),
      log(9, ""),
      log(9, "------------------------------------------------------------------------"),
      log(9, "Building Module 2 1.0.0-SNAPSHOT"),
      log(9, "------------------------------------------------------------------------"),
      log(9, ""),
      log(9, "--- maven-clean-plugin:2.5:clean (default-clean) @ module2 ---"),
      log(9, ""),
      log(12, "------------------------------------------------------------------------"),
      log(13, "Reactor Summary:"),
      log(13, ""),
      log(13, "Module 1 ........................................... SUCCESS [05:11 min]"),
      log(13, "Module 2 ........................................... SUCCESS [  3.909 s]"),
      log(13, "------------------------------------------------------------------------"),
      log(13, "BUILD SUCCESS"),
      log(13, "------------------------------------------------------------------------"),
      log(13, "Total time: 02:11 h"),
      log(13, "Finished at: 2019-03-25T17:06:52+00:00"),
      log(13, "Final Memory: 3710M/6185M"),
      log(13, "------------------------------------------------------------------------"),
    };
    var summaries = MavenModuleSummaryReducer.reduce("myJob", 123, List.of(logs));
    var m1p1 =
        MavenPluginExecSummary.newBuilder()
            .jobName("myJob")
            .jobExecId(123)
            .moduleId("module1")
            .moduleName("Module 1 1.0.0-SNAPSHOT")
            .startTime(LocalTime.of(16, 55, 4))
            .endTime(LocalTime.of(16, 55, 5))
            .pluginName("__download__")
            .pluginVersion("1.0.0")
            .pluginGoal("download")
            .pluginExecId("download")
            .build();
    var m1p2 =
        MavenPluginExecSummary.newBuilder()
            .jobName("myJob")
            .jobExecId(123)
            .moduleId("module1")
            .moduleName("Module 1 1.0.0-SNAPSHOT")
            .startTime(LocalTime.of(16, 55, 6))
            .endTime(LocalTime.of(16, 55, 7))
            .pluginName("maven-clean-plugin")
            .pluginGoal("clean")
            .pluginVersion("2.5")
            .pluginExecId("default-clean")
            .build();
    var m1p3 =
        MavenPluginExecSummary.newBuilder()
            .jobName("myJob")
            .jobExecId(123)
            .moduleId("module1")
            .moduleName("Module 1 1.0.0-SNAPSHOT")
            .startTime(LocalTime.of(16, 55, 8))
            .endTime(LocalTime.of(16, 55, 9))
            .pluginName("maven-install-plugin")
            .pluginVersion("2.4")
            .pluginGoal("install")
            .pluginExecId("default-install")
            .build();
    var m2p1 =
        MavenPluginExecSummary.newBuilder()
            .jobName("myJob")
            .jobExecId(123)
            .moduleId("module2")
            .moduleName("Module 2 1.0.0-SNAPSHOT")
            .startTime(LocalTime.of(16, 55, 9))
            .endTime(LocalTime.of(16, 55, 9))
            .pluginName("__download__")
            .pluginVersion("1.0.0")
            .pluginGoal("download")
            .pluginExecId("download")
            .build();
    var m2p2 =
        MavenPluginExecSummary.newBuilder()
            .jobName("myJob")
            .jobExecId(123)
            .moduleId("module2")
            .moduleName("Module 2 1.0.0-SNAPSHOT")
            .startTime(LocalTime.of(16, 55, 9))
            .endTime(LocalTime.of(16, 55, 9))
            .pluginGoal("clean")
            .pluginVersion("2.5")
            .pluginName("maven-clean-plugin")
            .pluginExecId("default-clean")
            .build();
    assertThat(summaries).containsExactlyInAnyOrder(m1p1, m1p2, m1p3, m2p1, m2p2);
  }

  private static MavenLog log(int second, String message) {
    return MavenLog.info(message).localTime(LocalTime.of(16, 55, second)).build();
  }
}
