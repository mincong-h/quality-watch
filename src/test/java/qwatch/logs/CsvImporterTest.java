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

  private static final ZoneId Z = ZoneId.of("Z");

  @Rule public TemporaryFolder tempDir = new TemporaryFolder();

  private Path logPath;

  @Before
  public void setUp() throws Exception {
    logPath = tempDir.newFile().toPath();
    List<String> linesF1 = new ArrayList<>();
    linesF1.add("date,Service,Status,message");
    linesF1.add("2019-02-11T12:13:57.916Z,nos-15,error,Project foo not found.");
    linesF1.add("2019-02-11T12:13:57.917Z,nos-15,error,\"First line");
    linesF1.add("another line\"");
    Files.write(logPath, linesF1);
  }

  @Test
  public void importLogEntries() {
    List<LogEntry> entries = CsvImporter.importLogEntries(logPath).get().toJavaList();
    LogEntry expectedEntry1 =
        LogEntry.newBuilder()
            .dateTime(LocalDateTime.of(2019, 2, 11, 12, 13, 57, 916_000_000).atZone(Z))
            .service("nos-15")
            .status("error")
            .message("Project foo not found.")
            .build();
    LogEntry expectedEntry2 =
        LogEntry.newBuilder()
            .dateTime(LocalDateTime.of(2019, 2, 11, 12, 13, 57, 917_000_000).atZone(Z))
            .service("nos-15")
            .status("error")
            .message("First line\nanother line")
            .build();
    assertThat(entries).containsExactly(expectedEntry1, expectedEntry2);
  }

  @Test
  public void internalParseCsv() {
    List<String[]> rows = CsvImporter.internalParseCsv("A,B,C\na,b,c", 3).get().toJavaList();
    assertThat(rows)
        .hasSize(2)
        .containsExactly(new String[] {"A", "B", "C"}, new String[] {"a", "b", "c"});

    List<String[]> rows2 = CsvImporter.internalParseCsv("A,B,C\na,b,\"c1\"\"c2\"", 3).get().toJavaList();
    assertThat(rows2)
        .hasSize(2)
        .containsExactly(new String[] {"A", "B", "C"}, new String[] {"a", "b", "c1\"c2"});

    List<String[]> rows3 = CsvImporter.internalParseCsv("a,b,\"c1\nc2\"", 3).get().toJavaList();
    assertThat(rows3).hasSize(1).containsExactly(new String[] {"a", "b", "c1\nc2"});
  }
}
