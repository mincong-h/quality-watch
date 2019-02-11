package qwatch.logs;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import qwatch.logs.model.LogEntry;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class CsvImporterTest {

  private static final ZoneId UTC = ZoneId.of("UTC");

  @Rule public TemporaryFolder tempDir = new TemporaryFolder();

  private Path logPath;

  @Before
  public void setUp() throws Exception {
    logPath = tempDir.newFile().toPath();
    List<String> linesF1 = new ArrayList<>();
    linesF1.add("date,Service,Status,message"); // header
    linesF1.add("2019-02-11T12:13:57.916Z,nos-15,error,Project foo not found."); // log1
    Files.write(logPath, linesF1);
  }

  @Test
  public void importLogEntries() {
    List<LogEntry> entries = CsvImporter.importLogEntries(logPath).get().toJavaList();
    LogEntry expectedEntry =
        LogEntry.newBuilder()
            .dateTime(LocalDateTime.of(2019, 2, 11, 12, 13, 57, 916).atZone(UTC))
            .service("nos-15")
            .status("error")
            .message("Project foo not found.")
            .build();
    assertThat(entries).containsExactly(expectedEntry);
  }
}
