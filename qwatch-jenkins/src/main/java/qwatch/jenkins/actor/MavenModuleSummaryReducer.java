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

  public static Set<MavenModuleSummary> reduce(String jobName, int jobId, List<MavenLog> logs) {
    // TODO
    return HashSet.of();
  }
}
