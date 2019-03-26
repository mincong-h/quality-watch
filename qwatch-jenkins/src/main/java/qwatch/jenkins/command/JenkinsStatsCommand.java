package qwatch.jenkins.command;

import java.nio.file.Path;
import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.jenkins.actor.TestSuiteImporter;
import qwatch.jenkins.actor.TestSummarizer;
import qwatch.jenkins.model.TestSuite;

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

  private final Path buildDir;

  private JenkinsStatsCommand(Builder builder) {
    this.buildDir = builder.buildDir;
  }

  /** Executes the statistics command. */
  public void execute() {
    logger.info("buildDir: {}", buildDir);
    var eitherImport = TestSuiteImporter.importSuites(buildDir);
    if (eitherImport.isLeft()) {
      return;
    }
    var suites = eitherImport.get();
    for (var s : suites.toSortedSet(Comparator.comparing(TestSuite::name))) {
      if (logger.isInfoEnabled()) {
        var str =
            String.format(
                "✅ %2d, 💥 %2d, ❄️ %2d - %s (%.2f s)",
                s.testCount(), s.failureCount(), s.skippedCount(), s.name(), s.time());
        logger.info(str);
      }
    }
    var pkgSummary = TestSummarizer.createSummaryPerPackage(suites);
    logger.info("Summary:\n{}", pkgSummary);
  }
}
