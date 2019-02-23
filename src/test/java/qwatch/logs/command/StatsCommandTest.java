package qwatch.logs.command;

import io.vavr.collection.List;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import qwatch.logs.model.LogSummary;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class StatsCommandTest {

  @Rule public TemporaryFolder tempDir = new TemporaryFolder();

  private Path tempRoot;

  @Before
  public void setUp() throws Exception {
    Path csv1 = tempDir.newFile("log.2019-01-01.json").toPath();
    Path csv2 = tempDir.newFile("log.2019-01-02.json").toPath();
    Path csv3 = tempDir.newFile("log.2019-01-03.json").toPath();
    List<String> lines =
        List.of("  \"host\" : \"myHost\",")
            .append("  \"service\" : \"myService\",")
            .append("  \"status\" : \"error\",")
            .append("  \"message\" : \"Foo\"")
            .append("} ]");
    Files.write(
        csv1, List.of("[ {", "  \"date\" : \"2019-01-01T00:00:00.000Z\",").appendAll(lines));
    Files.write(
        csv2, List.of("[ {", "  \"date\" : \"2019-01-02T00:00:00.000Z\",").appendAll(lines));
    Files.write(
        csv3, List.of("[ {", "  \"date\" : \"2019-01-03T00:00:00.000Z\",").appendAll(lines));
    tempRoot = tempDir.getRoot().toPath();
  }

  @Test
  public void execute() {
    List<LogSummary> summaries =
        StatsCommand.newBuilder().logDir(tempRoot).days(2).topN(1).build().execute();
    assertThat(summaries).containsExactly(LogSummary.of(2, "[   ] Foo"));
  }
}
