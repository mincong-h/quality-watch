package qwatch.jenkins.command;

import io.vavr.collection.TreeSet;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.jenkins.actor.CsvTestCaseExporter;
import qwatch.jenkins.actor.TestSuiteImporter;
import qwatch.jenkins.model.EnrichedTestCase;

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
    try (var execDirs = Files.newDirectoryStream(artifactDir, "nos-*")) {
      for (var dir : execDirs) {
        var result = TestSuiteImporter.importTestCases(dir);
        if (result.isLeft()) {
          logger.error(result.getLeft());
        } else {
          // Transform
          logger.info("Processing {}", dir);
          result.get().forEach(suites::add);
        }
      }
    } catch (IOException e) {
      logger.error("Failed to list files in directory " + artifactDir, e);
      return;
    }

    // Export
    CsvTestCaseExporter exporter = new CsvTestCaseExporter(exportDir);
    var sorted =
        TreeSet.of(
                comparing(EnrichedTestCase::jobName)
                    .thenComparing(EnrichedTestCase::jobExecutionId)
                    .thenComparing(EnrichedTestCase::className)
                    .thenComparing(EnrichedTestCase::name))
            .addAll(suites);
    var result = exporter.export(sorted);
    if (result.isRight()) {
      logger.info("Exportation succeed. Exported {} lines.", sorted.size());
    } else {
      logger.error("Exportation failed. Cause: {}", result.getLeft());
    }
  }
}
