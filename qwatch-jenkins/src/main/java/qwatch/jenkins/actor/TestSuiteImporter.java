package qwatch.jenkins.actor;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.control.Either;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.jenkins.model.EnrichedTestCase;
import qwatch.jenkins.model.TestSuite;
import qwatch.jenkins.util.ObjectMapperFactory;

/**
 * Test-Suite Importer imports Surefire and Failsafe test-suites from a given directory.
 *
 * @author Mincong Huang
 * @since 1.0
 */
public class TestSuiteImporter {

  private static final Logger logger = LoggerFactory.getLogger(TestSuiteImporter.class);
  private static final XmlMapper mapper = ObjectMapperFactory.newXmlMapper();

  /**
   * Path matcher for test reports.
   *
   * <p>The Surefire Plugin generates reports in two different file formats:
   *
   * <ul>
   *   <li>Plain text files (*.txt)
   *   <li>XML files (*.xml)
   * </ul>
   *
   * By default, these files are generated in <code>${basedir}/target/surefire-reports/TEST-*.xml
   * </code>.
   *
   * <p>The Failsafe Plugin generates reports in two different file formats:
   *
   * <ul>
   *   <li>Plain text files (*.txt)
   *   <li>XML files (*.xml)
   * </ul>
   *
   * By default, these files are generated in <code>${basedir}/target/failsafe-reports/TEST-*.xml
   * </code>.
   *
   * @see <a href="https://maven.apache.org/surefire/maven-surefire-plugin/">Maven Surefire
   *     Plugin</a>
   * @see <a href="https://maven.apache.org/surefire/maven-failsafe-plugin/">Maven Failsafe
   *     Plugin</a>
   */
  private static final PathMatcher TEST_REPORT_MATCHER =
      FileSystems.getDefault()
          .getPathMatcher("glob:**/target/{surefire,failsafe}-reports/TEST-*.xml");

  /**
   * Import test suites from a given Jenkins build execution directory.
   *
   * @param executionDir Jenkins build execution directory
   * @return either failure or a set of test-suites
   */
  public static Either<String, List<EnrichedTestCase>> importTestCases(Path executionDir) {
    var parts = executionDir.getFileName().toString().split("\\.");
    var jobName = parts[0];
    var jobId = Integer.parseInt(parts[1]);

    List<EnrichedTestCase> testCases = List.empty();
    var tryFindReports = findReports(executionDir);
    if (tryFindReports.isLeft()) {
      return Either.left(tryFindReports.getLeft());
    }
    for (var xml : tryFindReports.get()) {
      try {
        var s = mapper.readValue(xml.toFile(), TestSuite.class);
        // Path: $module/target/$reportDir/$reportXml
        var mavenModule = xml.getParent().getParent().getParent().getFileName().toString();
        var cases =
            HashSet.ofAll(s.testCases()).map(t -> t.enrichWith(jobName, jobId, mavenModule));
        testCases = testCases.appendAll(cases);
      } catch (IOException e) {
        logger.error("Failed to parse file " + xml, e);
      }
    }
    return Either.right(testCases);
  }

  static Either<String, Set<Path>> findReports(Path executionDir) {
    var xmlPaths = new java.util.HashSet<Path>();
    try {
      Files.walkFileTree(
          executionDir,
          new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
              if (TEST_REPORT_MATCHER.matches(path)) {
                xmlPaths.add(path);
              }
              return FileVisitResult.CONTINUE;
            }
          });
      return Either.right(HashSet.ofAll(xmlPaths));
    } catch (IOException e) {
      var msg = "Failed to list files from " + executionDir;
      logger.error(msg, e);
      return Either.left(msg);
    }
  }

  private TestSuiteImporter() {
    // Utility class, do not instantiate
  }
}
