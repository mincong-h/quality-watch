package qwatch.jenkins.actor;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import io.vavr.control.Either;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
   * Import test suites from a given Jenkins build execution directory.
   *
   * @param executionDir Jenkins build execution directory
   * @return either failure or a set of test-suites
   */
  public static Either<String, Set<TestSuite>> importSuites(Path executionDir) {
    var xmlPaths = new java.util.HashSet<Path>();
    try {
      Files.walkFileTree(
          executionDir,
          new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
              var f = path.getFileName().toString();
              var d = path.getParent().getFileName().toString();
              if (!attrs.isDirectory()
                  && (d.equals("surefire-reports") || d.equals("failsafe-reports"))
                  && f.startsWith("TEST-")
                  && f.endsWith(".xml")) {
                xmlPaths.add(path);
              }
              return FileVisitResult.CONTINUE;
            }
          });
    } catch (IOException e) {
      var msg = "Failed to list files from " + executionDir;
      logger.error(msg, e);
      return Either.left(msg);
    }
    Set<TestSuite> suites = HashSet.empty();
    for (var xml : xmlPaths) {
      try {
        var s = mapper.readValue(xml.toFile(), TestSuite.class);
        suites = suites.add(s);
      } catch (IOException e2) {
        logger.error("Failed to parse file " + xml, e2);
      }
    }
    return Either.right(suites);
  }

  private TestSuiteImporter() {
    // Utility class, do not instantiate
  }
}
