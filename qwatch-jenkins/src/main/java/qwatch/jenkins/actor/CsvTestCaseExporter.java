package qwatch.jenkins.actor;

import io.vavr.collection.List;
import io.vavr.collection.SortedSet;
import io.vavr.control.Either;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import qwatch.jenkins.model.EnrichedTestCase;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class CsvTestCaseExporter {

  private final Path dataDir;

  public CsvTestCaseExporter(Path dataDir) {
    this.dataDir = dataDir;
  }

  public Either<String, Void> export(SortedSet<EnrichedTestCase> testCases) {
    var lines = List.of("\"jobName\",\"jobId\",\"module\",\"class\",\"name\",\"time\"");
    lines = lines.appendAll(testCases.map(this::toRow));
    try {
      Files.write(dataDir.resolve("tests.csv"), lines, CREATE, TRUNCATE_EXISTING);
    } catch (IOException e) {
      return Either.left(e.getMessage());
    }
    return Either.right(null);
  }

  String toRow(EnrichedTestCase t) {
    var jobName = t.jobName().replace("\"", "\"\"");
    var jobId = t.jobExecutionId();
    var module = t.module().replace("\"", "\"\"");
    var className = t.className().replace("\"", "\"\"");
    var name = t.name().replace("\"", "\"\"");
    var time = String.format("%.3f", t.time());
    return "\"" + jobName + "\",\"" + jobId + "\",\"" + module + "\",\"" + className + "\",\""
        + name + "\",\"" + time + "\"";
  }
}
