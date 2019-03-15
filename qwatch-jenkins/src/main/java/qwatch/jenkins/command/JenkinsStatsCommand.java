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
import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.jenkins.model.SurefireTestSuite;
import qwatch.jenkins.util.ObjectMapperFactory;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class JenkinsStatsCommand {

  public static final String NAME = "jenkins-stats";
  private static final Logger logger = LoggerFactory.getLogger(JenkinsStatsCommand.class);

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private Path buildDir;

    /**
     * Sets the directory path of the Jenkins build execution.
     *
     * @param buildDir the directory path of the Jenkins build execution
     * @return this
     */
    public Builder buildDir(Path buildDir) {
      this.buildDir = buildDir;
      return this;
    }

    public JenkinsStatsCommand build() {
      return new JenkinsStatsCommand(this);
    }
  }

  private final XmlMapper mapper = ObjectMapperFactory.newXmlMapper();
  private final Path buildDir;

  private JenkinsStatsCommand(Builder builder) {
    this.buildDir = builder.buildDir;
  }

  /** Executes the statistics command. */
  public void execute() {
    Set<SurefireTestSuite> suites = HashSet.empty();
    logger.info("buildDir: {}", buildDir);
    java.util.Set<Path> paths = new java.util.HashSet<>();
    try {
      Files.walkFileTree(
          buildDir,
          new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
              var fileName = path.getFileName().toString();
              var dirName = path.getParent().getFileName().toString();
              if (!attrs.isDirectory()
                  && dirName.equals("surefire-reports")
                  && fileName.startsWith("TEST-")
                  && fileName.endsWith(".xml")) {
                paths.add(path);
              }
              return FileVisitResult.CONTINUE;
            }
          });
      for (var xml : paths) {
        var s = mapper.readValue(xml.toFile(), SurefireTestSuite.class);
        suites = suites.add(s);
      }
    } catch (IOException e) {
      logger.error("Failed to list files from " + buildDir, e);
    }
    for (var s : suites.toSortedSet(Comparator.comparing(SurefireTestSuite::name))) {
      if (logger.isInfoEnabled()) {
        logger.info(
            "{} (✅: {}, 💥: {}, ❄️: {})",
            s.name(),
            s.testCount(),
            s.failureCount(),
            s.skippedCount());
      }
    }
  }
}
