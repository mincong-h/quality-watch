package qwatch.jenkins.actor;

import io.vavr.collection.List;
import io.vavr.collection.SortedSet;
import io.vavr.control.Either;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import qwatch.jenkins.model.maven.MavenPluginExecSummary;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class CsvMavenModuleSummaryExporter {

  private final Path dataDir;

  public CsvMavenModuleSummaryExporter(Path dataDir) {
    this.dataDir = dataDir;
  }

  public Either<String, Void> export(SortedSet<MavenPluginExecSummary> summaries) {
    var lines =
        List.of(
            "\"jobName\",\"jobId\",\"module\",\"pluginName\",\"pluginVersion\",\"pluginGoal\",\"pluginExecId\",\"duration\"");
    lines = lines.appendAll(summaries.map(this::toRow));
    try {
      Files.write(dataDir.resolve("maven.csv"), lines, CREATE, TRUNCATE_EXISTING);
    } catch (IOException e) {
      return Either.left(e.getMessage());
    }
    return Either.right(null);
  }

  private String toRow(MavenPluginExecSummary t) {
    var jobName = t.jobName().replace("\"", "\"\"");
    var jobId = t.jobExecId();
    var module = t.moduleId().replace("\"", "\"\"");
    var pluginName = t.pluginName().replace("\"", "\"\"");
    var pluginVersion = t.pluginVersion().replace("\"", "\"\"");
    var pluginGoal = t.pluginGoal().replace("\"", "\"\"");
    var pluginExecId = t.pluginExecId().replace("\"", "\"\"");
    var duration = t.duration().getSeconds();
    return "\""
        + jobName
        + "\",\""
        + jobId
        + "\",\""
        + module
        + "\",\""
        + pluginName
        + "\",\""
        + pluginVersion
        + "\",\""
        + pluginGoal
        + "\",\""
        + pluginExecId
        + "\",\""
        + duration
        + "\"";
  }
}
