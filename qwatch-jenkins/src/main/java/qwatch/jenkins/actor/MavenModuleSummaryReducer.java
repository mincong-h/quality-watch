package qwatch.jenkins.actor;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import qwatch.jenkins.model.maven.MavenLog;
import qwatch.jenkins.model.maven.MavenModuleSummary;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class MavenModuleSummaryReducer {

  private static final String SEP_LINE =
      "------------------------------------------------------------------------";
  private static final String REACTOR_BUILD_ORDER_LINE = "Reactor Build Order:";
  private static final String REACTOR_SUMMARY_LINE = "Reactor Summary:";
  private static final String MODULE_HEADER_PREFIX = "Building ";

  public static Set<MavenModuleSummary> reduce(String jobName, int jobId, List<MavenLog> logs) {
    var it = logs.iterator();

    // Pre-reactor
    var preReactor = true;
    while (it.hasNext() && preReactor) {
      var log = it.next();
      if (REACTOR_BUILD_ORDER_LINE.equals(log.message())) {
        preReactor = false;
      } else {
        // TODO Collect logs
      }
    }

    // Reactor
    var reactor = true;
    while (it.hasNext() && reactor) {
      var log = it.next();
      if (SEP_LINE.equals(log.message())) {
        reactor = false;
      } else {
        // TODO Collect logs
      }
    }

    // Modules
    var hasModules = true;
    var isHeader = true;
    final var summaries = new java.util.LinkedList<MavenModuleSummary>();
    MavenModuleSummary summary = null;
    while (it.hasNext() && hasModules) {
      var log = it.next();
      if (REACTOR_SUMMARY_LINE.equals(log.message())) {
        hasModules = false;
      } else if (SEP_LINE.equals(log.message())) {
        // next module
        summaries.add(summary);
        isHeader = true;
      } else if (isHeader) {
        var moduleName = log.message().substring(MODULE_HEADER_PREFIX.length());
        summary =
            MavenModuleSummary.newBuilder()
                .jobName(jobName)
                .jobExecutionId(jobId)
                .startTime(log.localTime())
                .endTime(log.localTime())
                .moduleName(moduleName)
                .build();
        isHeader = false;
        it.next(); // skip next line (separator)
      } else {
        summary = summary.toBuilder().endTime(log.localTime()).build();
      }
    }
    return HashSet.ofAll(summaries);
  }

  private MavenModuleSummaryReducer() {
    // Utility class, do not instantiate
  }
}
