package qwatch.jenkins.actor;

import io.vavr.collection.SortedSet;
import io.vavr.control.Either;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import qwatch.jenkins.model.EnrichedTestCase;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class TestCaseExporter {

  private final Path dataDir;

  TestCaseExporter(Path dataDir) {
    this.dataDir = dataDir;
  }

  Either<String, Void> export(SortedSet<EnrichedTestCase> testCases) {
    var lines = List.of("\"jobName\",\"jobId\",\"class\",\"name\",\"time\"");
    lines.addAll(testCases.map(this::toRow).toJavaList());
    try {
      Files.write(dataDir.resolve("executions.csv"), lines, WRITE, TRUNCATE_EXISTING);
    } catch (IOException e) {
      return Either.left(e.getMessage());
    }
    return Either.right(null);
  }

  String toRow(EnrichedTestCase t) {
    var jobName = t.jobName().replace("\"", "\"\"");
    var jobId = t.jobExecutionId();
    var className = t.className().replace("\"", "\"\"");
    var name = t.name().replace("\"", "\"\"");
    var time = String.format("%.3f", t.time());
    return "\"" + jobName + "\",\"" + jobId + "\",\"" + className + "\",\"" + name + "\",\"" + time
        + "\"";
  }
}
