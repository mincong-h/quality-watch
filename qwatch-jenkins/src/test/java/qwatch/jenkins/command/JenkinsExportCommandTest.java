package qwatch.jenkins.command;

import java.time.LocalTime;
import org.assertj.core.internal.bytebuddy.asm.Advice.Local;
import org.junit.Test;
import qwatch.jenkins.model.EnrichedTestCase;
import qwatch.jenkins.model.maven.MavenPluginExecSummary;

import static org.assertj.core.api.Assertions.assertThat;
import static qwatch.jenkins.command.JenkinsExportCommand.ENRICHED_TEST_CASE_COMPARATOR;
import static qwatch.jenkins.command.JenkinsExportCommand.MAVEN_PLUGIN_EXEC_SUMMARY_COMPARATOR;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class JenkinsExportCommandTest {

  @Test
  public void enrichedTestCaseComparator() {
    var testCase =
        EnrichedTestCase.newBuilder()
            .jobName("job1")
            .jobExecutionId(1)
            .module("module1")
            .className("class1")
            .name("test1")
            .time(1)
            .build();
    var tcJ1E1M1C1T1D1 = testCase;
    var tcJ1E1M1C1T1D2 = testCase.toBuilder().time(2).build();
    var tcJ1E1M1C1T2D1 = testCase.toBuilder().name("test2").build();
    var tcJ1E1M1C2T1D1 = testCase.toBuilder().className("class2").build();
    var tcJ1E1M2C1T1D1 = testCase.toBuilder().module("module2").build();
    var tcJ1E2M1C1T1D1 = testCase.toBuilder().jobExecutionId(2).build();
    var tcJ2E1M1C1T1D1 = testCase.toBuilder().jobName("job2").build();

    assertThat(ENRICHED_TEST_CASE_COMPARATOR.compare(tcJ1E1M1C1T1D1, tcJ1E1M1C1T1D2)).isNegative();
    assertThat(ENRICHED_TEST_CASE_COMPARATOR.compare(tcJ1E1M1C1T1D1, tcJ1E1M1C1T2D1)).isNegative();
    assertThat(ENRICHED_TEST_CASE_COMPARATOR.compare(tcJ1E1M1C1T1D1, tcJ1E1M1C2T1D1)).isNegative();
    assertThat(ENRICHED_TEST_CASE_COMPARATOR.compare(tcJ1E1M1C1T1D1, tcJ1E1M2C1T1D1)).isNegative();
    assertThat(ENRICHED_TEST_CASE_COMPARATOR.compare(tcJ1E1M1C1T1D1, tcJ1E2M1C1T1D1)).isNegative();
    assertThat(ENRICHED_TEST_CASE_COMPARATOR.compare(tcJ1E1M1C1T1D1, tcJ2E1M1C1T1D1)).isNegative();
  }

  @Test
  public void mavenPluginExecSummaryComparator() {
    var summary =
        MavenPluginExecSummary.newBuilder()
            .jobName("job1")
            .jobExecId(1)
            .moduleId("module1")
            .moduleName("Module 1")
            .pluginName("plugin1")
            .pluginVersion("1.0.0")
            .pluginGoal("goal1")
            .pluginExecId("exec1")
            .startTime(LocalTime.of(0, 0, 0))
            .endTime(LocalTime.of(0, 1, 0))
            .build();
    var sJ1E1M1P1D1 = summary;
    var sJ1E1M1P2D1 = summary.toBuilder().pluginName("plugin2").build();
    var sJ1E1M2P1D1 = summary.toBuilder().moduleId("module2").moduleName("Module 2").build();
    var sJ1E2M1P1D1 = summary.toBuilder().jobExecId(2).build();
    var sJ2E1M1P1D1 = summary.toBuilder().jobName("job2").build();


    assertThat(MAVEN_PLUGIN_EXEC_SUMMARY_COMPARATOR.compare(sJ1E1M1P1D1, sJ1E1M1P2D1)).isNegative();
    assertThat(MAVEN_PLUGIN_EXEC_SUMMARY_COMPARATOR.compare(sJ1E1M1P1D1, sJ1E1M2P1D1)).isNegative();
    assertThat(MAVEN_PLUGIN_EXEC_SUMMARY_COMPARATOR.compare(sJ1E1M1P1D1, sJ1E2M1P1D1)).isNegative();
    assertThat(MAVEN_PLUGIN_EXEC_SUMMARY_COMPARATOR.compare(sJ1E1M1P1D1, sJ2E1M1P1D1)).isNegative();
  }
}
