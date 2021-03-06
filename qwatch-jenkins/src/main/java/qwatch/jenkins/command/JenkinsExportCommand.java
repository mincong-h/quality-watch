package qwatch.jenkins.command;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.collection.TreeSet;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.jenkins.actor.CsvMavenModuleSummaryExporter;
import qwatch.jenkins.actor.CsvTestCaseExporter;
import qwatch.jenkins.actor.JenkinsLogReader;
import qwatch.jenkins.actor.MavenModuleSummaryReducer;
import qwatch.jenkins.actor.TestSuiteImporter;
import qwatch.jenkins.model.EnrichedTestCase;
import qwatch.jenkins.model.maven.MavenLog;
import qwatch.jenkins.model.maven.MavenPluginExecSummary;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class JenkinsExportCommand {

  public static final String NAME = "jenkins-export";
  private static final Logger logger = LoggerFactory.getLogger(JenkinsExportCommand.class);

  static final Comparator<EnrichedTestCase> ENRICHED_TEST_CASE_COMPARATOR =
      Comparator.comparing(EnrichedTestCase::jobName)
          .thenComparing(EnrichedTestCase::jobExecutionId)
          .thenComparing(EnrichedTestCase::module)
          .thenComparing(EnrichedTestCase::className)
          .thenComparing(EnrichedTestCase::name)
          .thenComparing(EnrichedTestCase::time);

  static final Comparator<MavenPluginExecSummary> MAVEN_PLUGIN_EXEC_SUMMARY_COMPARATOR =
      Comparator.comparing(MavenPluginExecSummary::jobName)
          .thenComparing(MavenPluginExecSummary::jobExecId)
          .thenComparing(MavenPluginExecSummary::moduleId)
          .thenComparing(MavenPluginExecSummary::pluginName)
          .thenComparing(MavenPluginExecSummary::pluginVersion)
          .thenComparing(MavenPluginExecSummary::pluginGoal)
          .thenComparing(MavenPluginExecSummary::pluginExecId)
          .thenComparing(MavenPluginExecSummary::startTime);

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private Path artifactDir;
    private Path exportDir;

    /**
     * Sets the directory path of Jenkins build artifacts.
     *
     * @param artifactDir the directory path of the Jenkins build artifacts
     * @return this
     */
    public Builder buildDir(Path artifactDir) {
      this.artifactDir = artifactDir;
      return this;
    }

    /**
     * Sets the directory path for exportation.
     *
     * @param exportDir the directory path of exportation
     * @return this
     */
    public Builder exportDir(Path exportDir) {
      this.exportDir = exportDir;
      return this;
    }

    public JenkinsExportCommand build() {
      return new JenkinsExportCommand(this);
    }
  }

  private final Path artifactDir;
  private final Path exportDir;

  private JenkinsExportCommand(Builder builder) {
    this.artifactDir = builder.artifactDir;
    this.exportDir = builder.exportDir;
  }

  public void execute() {
    logger.info("artifactDir: {}", artifactDir);
    logger.info("exportDir: {}", exportDir);

    // Collect
    var suites = new java.util.LinkedList<EnrichedTestCase>();
    var logs = new HashMap<String, List<MavenLog>>();
    try (var execDirs = Files.newDirectoryStream(artifactDir, "nos-*")) {
      for (var dir : execDirs) {
        logger.info("Processing {}", dir);
        var dirName = dir.getFileName().toString();
        // test cases
        TestSuiteImporter.importTestCases(dir)
            .peekLeft(logger::error)
            .peek(s -> suites.addAll(s.toJavaList()));
        // logs
        JenkinsLogReader.read(dir.resolve("jenkins.log"))
            .peekLeft(logger::error)
            .peek(s -> logs.put(dirName, s._2));
      }
    } catch (IOException e) {
      logger.error("Failed to list files in directory " + artifactDir, e);
      return;
    }

    // Reduce
    Set<MavenPluginExecSummary> mavenSummaries = HashSet.empty();
    for (var jobLogs : logs.entrySet()) {
      var jobName = jobLogs.getKey().split("\\.")[0];
      var jobId = Integer.parseInt(jobLogs.getKey().split("\\.")[1]);
      var summary = MavenModuleSummaryReducer.reduce(jobName, jobId, jobLogs.getValue());
      mavenSummaries = mavenSummaries.addAll(summary);

      var logSize = String.format("%,d", jobLogs.getValue().size());
      var modSize = String.format("%,d", summary.size());
      logger.info("Job {}.{}: reduced {} logs to {} modules", jobName, jobId, logSize, modSize);
    }

    // Export
    var testExporter = new CsvTestCaseExporter(exportDir);
    var sortedTestCases = List.ofAll(suites).sorted(ENRICHED_TEST_CASE_COMPARATOR);
    var testExport = testExporter.export(sortedTestCases);
    if (testExport.isRight()) {
      var lines = String.format("%,d", sortedTestCases.size());
      logger.info("Test exportation succeed. Exported {} lines.", lines);
    } else {
      logger.error("Test exportation failed. Cause: {}", testExport.getLeft());
    }

    var summaryExporter = new CsvMavenModuleSummaryExporter(exportDir);

    var sortedSummaries = TreeSet.of(MAVEN_PLUGIN_EXEC_SUMMARY_COMPARATOR).addAll(mavenSummaries);
    var summaryExport = summaryExporter.export(sortedSummaries);
    if (summaryExport.isRight()) {
      var lines = String.format("%,d", sortedSummaries.size());
      logger.info("Summary exportation succeed. Exported {} lines.", lines);
    } else {
      logger.error("Summary exportation failed. Cause: {}", summaryExport.getLeft());
    }
  }
}
