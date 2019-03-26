package qwatch.jenkins.command;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.jenkins.actor.CsvTestCaseExporter;
import qwatch.jenkins.actor.TestSuiteImporter;
import qwatch.jenkins.model.EnrichedTestCase;
import qwatch.jenkins.model.TestSuite;

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
    var eitherImport = TestSuiteImporter.importSuites(artifactDir);
    if (eitherImport.isLeft()) {
      return;
    }
    var suites = eitherImport.get();
    // Transform TestSuite to EnrichedTestCase
    var filename = artifactDir.getFileName().toString();
    logger.info("filename {}", filename);
    var tokens = filename.split("\\.");
    var jobName = tokens[0];
    var jobId = Integer.parseInt(tokens[1]);
    Set<EnrichedTestCase> enrichedSet = HashSet.empty();
    for (var s : suites) {
      enrichedSet = enrichedSet.addAll(toEnrichedTestCase(s, jobName, jobId));
    }
    CsvTestCaseExporter exporter = new CsvTestCaseExporter(exportDir);
    var sorted =
        enrichedSet.toSortedSet(
            comparing(EnrichedTestCase::jobName)
                .thenComparing(EnrichedTestCase::jobExecutionId)
                .thenComparing(EnrichedTestCase::className)
                .thenComparing(EnrichedTestCase::name));
    var result = exporter.export(sorted);
    if (result.isRight()) {
      logger.info("Exportation succeed. Exported {} lines.", enrichedSet.size());
    } else {
      logger.error("Exportation failed. Cause: {}", result.getLeft());
    }
  }

  Set<EnrichedTestCase> toEnrichedTestCase(TestSuite s, String jobName, int jobId) {
    return HashSet.ofAll(s.testCases()).map(t -> t.enrichWith(jobName, jobId));
  }
}
