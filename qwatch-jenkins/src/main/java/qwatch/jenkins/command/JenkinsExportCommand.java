package qwatch.jenkins.command;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.jenkins.actor.CsvTestCaseExporter;
import qwatch.jenkins.model.EnrichedTestCase;
import qwatch.jenkins.model.TestSuite;
import qwatch.jenkins.util.ObjectMapperFactory;

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

  private final XmlMapper mapper = ObjectMapperFactory.newXmlMapper();
  private final Path artifactDir;
  private final Path exportDir;

  private JenkinsExportCommand(Builder builder) {
    this.artifactDir = builder.artifactDir;
    this.exportDir = builder.exportDir;
  }

  public void execute() {
    Set<TestSuite> suites = HashSet.empty();
    logger.info("artifactDir: {}", artifactDir);
    logger.info("exportDir: {}", exportDir);
    var xmlPaths = new java.util.HashSet<Path>();
    try {
      Files.walkFileTree(
          artifactDir,
          new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
              var fileName = path.getFileName().toString();
              var dirName = path.getParent().getFileName().toString();
              if (!attrs.isDirectory()
                  && (dirName.equals("surefire-reports") || dirName.equals("failsafe-reports"))
                  && fileName.startsWith("TEST-")
                  && fileName.endsWith(".xml")) {
                xmlPaths.add(path);
              }
              return FileVisitResult.CONTINUE;
            }
          });
      for (var xml : xmlPaths) {
        try {
          var s = mapper.readValue(xml.toFile(), TestSuite.class);
          suites = suites.add(s);
        } catch (IOException e2) {
          logger.error("Failed to parse file " + xml, e2);
        }
      }
    } catch (IOException e) {
      logger.error("Failed to list files from " + artifactDir, e);
    }
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
