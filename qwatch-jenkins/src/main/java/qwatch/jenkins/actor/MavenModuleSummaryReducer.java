package qwatch.jenkins.actor;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import java.util.HashMap;
import java.util.regex.Pattern;
import qwatch.jenkins.model.maven.MavenLog;
import qwatch.jenkins.model.maven.MavenPluginExecSummary;

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

  /**
   * Regular expression pattern for plugin declaration.
   *
   * <ul>
   *   <li>Group 1: plugin ID
   *   <li>Group 2: plugin version
   *   <li>Group 3: plugin goal
   *   <li>Group 4: execution ID
   * </ul>
   */
  static final Pattern PLUGIN_DECLARATION_PATTERN =
      Pattern.compile("^--- ([\\w-]+):([\\w-.]+):([\\w-]+) \\(([\\w-]+)\\) @ ([\\w-]+) ---$");

  /**
   * Reduce Maven logs into a set of Maven summaries per module per plugin execution.
   *
   * <h2>Pre-Reactor</h2>
   *
   * <p>"Pre-reactor" section happens before the "reactor" section. Some dependencies are downloaded
   * in this section. No data is extracted from this section.
   *
   * <h2>Reactor</h2>
   *
   * <p>"Reactor" section shows the build order per module. No data is extracted from this section.
   *
   * <h2>Modules</h2>
   *
   * <p>"Modules" section is the core of the reducing process. The log of each module is modeled as
   * following:
   *
   * <pre>
   * Building ${module}
   * ------------------------------------------------------------------------
   * Downloading: ${link}
   * Downloaded: ${link}
   * [...]
   *
   * --- ${pluginName}:${pluginVersion}:${pluginGoal} (${pluginExecId}) @ ${moduleId} ---
   * [...]
   * </pre>
   *
   * For dependency download, it represents an important activity in the build, so it cannot be
   * ignored. For now, it's considered as a "Maven Download Plugin" ({@code __download__}) even
   * though it is not a real plugin. This is the best way I found for now. The complete
   * characteristic of this plugin is:
   *
   * <pre>
   * pluginName:     __download__
   * pluginVersion:  1.0.0
   * pluginGoal:     download
   * pluginExecId:   download
   * moduleId:       $moduleId
   * </pre>
   *
   * @param jobName Maven job name
   * @param jobId Maven job execution ID
   * @param logs the logs to reduce
   * @return summaries
   */
  public static Set<MavenPluginExecSummary> reduce(String jobName, int jobId, List<MavenLog> logs) {
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
    var isHeader = true;
    final var summaries = new java.util.LinkedList<MavenPluginExecSummary>();
    MavenPluginExecSummary summary = null;
    String moduleName = null;
    while (it.hasNext()) {
      var log = it.next();
      if (REACTOR_SUMMARY_LINE.equals(log.message())) {
        break;
      }
      var plugin = PLUGIN_DECLARATION_PATTERN.matcher(log.message());
      if (isHeader) {
        // prepare new module
        moduleName = log.message().substring(MODULE_HEADER_PREFIX.length());
        summary =
            MavenPluginExecSummary.newBuilder()
                .jobName(jobName)
                .jobExecId(jobId)
                .startTime(log.localTime())
                .endTime(log.localTime())
                .moduleName(moduleName)
                .moduleId("")
                .pluginName("__download__")
                .pluginVersion("1.0.0")
                .pluginGoal("download")
                .pluginExecId("download")
                .build();
        isHeader = false;
        it.next(); // skip next line (separator)
      } else if (SEP_LINE.equals(log.message())) {
        // next module
        summaries.add(summary);
        isHeader = true;
      } else if (plugin.matches()) {
        // next plugin
        summaries.add(summary);
        var pluginName = plugin.group(1);
        var pluginVersion = plugin.group(2);
        var pluginGoal = plugin.group(3);
        var pluginExecId = plugin.group(4);
        var moduleId = plugin.group(5);
        summary =
            MavenPluginExecSummary.newBuilder()
                .jobName(jobName)
                .jobExecId(jobId)
                .startTime(log.localTime())
                .endTime(log.localTime())
                .moduleId(moduleId)
                .moduleName(moduleName)
                .pluginName(pluginName)
                .pluginVersion(pluginVersion)
                .pluginGoal(pluginGoal)
                .pluginExecId(pluginExecId)
                .build();
      } else {
        // refresh end time
        summary = summary.toBuilder().endTime(log.localTime()).build();
      }
    }

    // Fill Module Name N/A
    final var map = new HashMap<String, String>();
    for (var s : summaries) {
      if (!s.moduleId().isEmpty()) {
        map.putIfAbsent(s.moduleName(), s.moduleId());
      }
    }
    return HashSet.ofAll(summaries)
        .map(s -> s.toBuilder().moduleId(map.getOrDefault(s.moduleName(), "")).build());
  }

  private MavenModuleSummaryReducer() {
    // Utility class, do not instantiate
  }
}
