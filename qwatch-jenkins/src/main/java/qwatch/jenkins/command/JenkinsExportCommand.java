package qwatch.jenkins.command;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.collection.TreeSet;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import qwatch.jenkins.model.maven.MavenModuleSummary;

import static java.util.Comparator.comparing;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class JenkinsExportCommand {

  public static final String NAME = "jenkins-export";
  private static final Logger logger = LoggerFactory.getLogger(JenkinsExportCommand.class);

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
    var suites = new java.util.HashSet<EnrichedTestCase>();
    var logs = new HashMap<String, List<MavenLog>>();
    try (var execDirs = Files.newDirectoryStream(artifactDir, "nos-*")) {
      for (var dir : execDirs) {
        logger.info("Processing {}", dir);
        var dirName = dir.getFileName().toString();
        // test cases
        TestSuiteImporter.importTestCases(dir)
            .peekLeft(logger::error)
            .peek(s -> suites.addAll(s.toJavaSet()));
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
    Set<MavenModuleSummary> mavenSummaries = HashSet.empty();
    for (var jobLogs : logs.entrySet()) {
      var jobName = jobLogs.getKey().split("\\.")[0];
      var jobId = Integer.parseInt(jobLogs.getKey().split("\\.")[1]);
      logger.info("Reducing {} logs from job {}.{}", jobLogs.getValue().size(), jobName, jobId);
      mavenSummaries =
          mavenSummaries.addAll(
              MavenModuleSummaryReducer.reduce(jobName, jobId, jobLogs.getValue()));
    }

    // Export
    var testExporter = new CsvTestCaseExporter(exportDir);
    var sortedTestCases =
        TreeSet.of(
                comparing(EnrichedTestCase::jobName)
                    .thenComparing(EnrichedTestCase::jobExecutionId)
                    .thenComparing(EnrichedTestCase::className)
                    .thenComparing(EnrichedTestCase::name))
            .addAll(suites);
    var testExport = testExporter.export(sortedTestCases);
    if (testExport.isRight()) {
      logger.info("Test exportation succeed. Exported {} lines.", sortedTestCases.size());
    } else {
      logger.error("Test exportation failed. Cause: {}", testExport.getLeft());
    }

    var summaryExporter = new CsvMavenModuleSummaryExporter(exportDir);
    var sortedSummaries =
        TreeSet.of(
                comparing(MavenModuleSummary::jobName)
                    .thenComparing(MavenModuleSummary::jobExecutionId)
                    .thenComparing(MavenModuleSummary::startTime))
            .addAll(mavenSummaries);
    var summaryExport = summaryExporter.export(sortedSummaries);
    if (summaryExport.isRight()) {
      logger.info("Summary exportation succeed. Exported {} lines.", sortedSummaries.size());
    } else {
      logger.error("Summary exportation failed. Cause: {}", summaryExport.getLeft());
    }
  }
}
