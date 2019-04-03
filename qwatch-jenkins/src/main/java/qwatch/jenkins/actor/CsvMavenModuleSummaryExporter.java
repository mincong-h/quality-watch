package qwatch.jenkins.actor;

import io.vavr.collection.List;
import io.vavr.collection.SortedSet;
import io.vavr.control.Either;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import qwatch.jenkins.model.maven.MavenModuleSummary;

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

  public Either<String, Void> export(SortedSet<MavenModuleSummary> testCases) {
    var lines = List.of("\"jobName\",\"jobId\",\"module\",\"duration\"");
    lines = lines.appendAll(testCases.map(this::toRow));
    try {
      Files.write(dataDir.resolve("maven.csv"), lines, CREATE, TRUNCATE_EXISTING);
    } catch (IOException e) {
      return Either.left(e.getMessage());
    }
    return Either.right(null);
  }

  String toRow(MavenModuleSummary t) {
    var jobName = t.jobName().replace("\"", "\"\"");
    var jobId = t.jobExecutionId();
    var module = t.moduleName().replace("\"", "\"\"");
    var duration = String.format("%.3f", t.duration().getSeconds());
    return "\"" + jobName + "\",\"" + jobId + "\",\"" + module + "\",\"" + duration + "\"";
  }
}
