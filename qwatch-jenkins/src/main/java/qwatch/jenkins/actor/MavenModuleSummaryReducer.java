package qwatch.jenkins.actor;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.jenkins.model.maven.MavenLog;
import qwatch.jenkins.model.maven.MavenModuleSummary;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class MavenModuleSummaryReducer {

  private static final Logger logger = LoggerFactory.getLogger(MavenModuleSummary.class);
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
    var modules = true;
    var moduleHeader = true;
    var moduleBody = false;
    final var summaries = new java.util.LinkedList<MavenModuleSummary>();
    MavenModuleSummary summary = null;
    while (it.hasNext() && modules) {
      var log = it.next();
      if (moduleHeader) {
        var moduleName = log.message().replace(MODULE_HEADER_PREFIX, "");
        summary =
            MavenModuleSummary.newBuilder()
                .jobName(jobName)
                .jobExecutionId(jobId)
                .startTime(log.localTime())
                .endTime(log.localTime())
                .moduleName(moduleName)
                .build();
        moduleHeader = false;
        it.next(); // skip separator
        moduleBody = true;
        continue;
      }
      if (SEP_LINE.equals(log.message())) {
        moduleBody = false;
        // next module
        summaries.add(summary);
        moduleHeader = true;
      }
      if (REACTOR_SUMMARY_LINE.equals(log.message())) {
        modules = false;
        continue;
      }
      if (moduleBody) {
        summary = summary.toBuilder().endTime(log.localTime()).build();
      }
    }
    logger.info("{} lines collected", summaries.size());
    return HashSet.ofAll(summaries);
  }
}
