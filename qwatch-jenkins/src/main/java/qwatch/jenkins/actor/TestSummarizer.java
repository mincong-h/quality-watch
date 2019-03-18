package qwatch.jenkins.actor;

import io.vavr.collection.Set;
import qwatch.jenkins.model.TestSuite;

/**
 * Test Summarizer summarizes the test execution reports into a brief text.
 *
 * @author Mincong Huang
 * @since 1.0
 */
public class TestSummarizer {

  private static final String sep = System.lineSeparator();

  /**
   * Creates a summary for test suites, grouped by package.
   *
   * @param testSuites all test suites
   * @return summary text
   */
  public static String createSummaryPerPackage(Set<TestSuite> testSuites) {
    var l1 = "Success | Failures | Skipped | Errors | Time    | Package" + sep;
    var l2 = "------- | -------- | ------- | ------ | ------- | -------" + sep;
    var sb = new StringBuilder(l1).append(l2);
    for (var pkg : testSuites.groupBy(s -> prefix4(s.name())).toSortedSet()) {
      var failureCount = pkg._2.map(TestSuite::failureCount).sum();
      var skippedCount = pkg._2.map(TestSuite::skippedCount).sum();
      var errorCount = pkg._2.map(TestSuite::errorCount).sum();
      var successCount = pkg._2.map(TestSuite::testCount).sum();
      var time = pkg._2.map(TestSuite::time).sum();
      var line =
          String.format(
              "%7d | %8d | %7d | %6d | %7.1f | %s",
              successCount, skippedCount, errorCount, successCount, time, pkg._1);
      sb.append(line).append(sep);
    }
    return sb.toString();
  }

  static String prefix4(String pkgName) {
    int pos = -1;
    int i = 0;
    do {
      pos = pkgName.indexOf('.', pos + 1);
      i++;
    } while (i < 4 && pos != -1);
    return pos >= 0 ? pkgName.substring(0, pos) : pkgName;
  }

  private TestSummarizer() {
    // Utility class, do not instantiate
  }
}
