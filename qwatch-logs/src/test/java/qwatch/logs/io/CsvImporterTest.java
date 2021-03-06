package qwatch.logs.io;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
    var linesF1 = new ArrayList<String>();
    linesF1.add("date,Host,Service,Status,message");
    linesF1.add("2019-02-11T12:13:57.916Z,foo,myService,error,Project foo not found.");
    linesF1.add("2019-02-11T12:13:57.917Z,foo,myService,error,\"First line");
    linesF1.add("another line\"");
    Files.write(logPath, linesF1);
  }

  @Test
  public void importLogEntries() {
    // Given a CSV file where 2 log entries are available
    // When importing it
    var entries = CsvImporter.importLogEntriesFromFile(logPath).get().toJavaList();

    // Then the import is successful
    // and zone id is 'UTC', same as JSON object mapper
    // (we need to keep deserialization same everywhere)
    var expectedEntry1 =
        LogEntry.newBuilder()
            .dateTime(LocalDateTime.of(2019, 2, 11, 12, 13, 57, 916_000_000).atZone(UTC))
            .host("foo")
            .service("myService")
            .status("error")
            .message("Project foo not found.")
            .build();
    var expectedEntry2 =
        LogEntry.newBuilder()
            .dateTime(LocalDateTime.of(2019, 2, 11, 12, 13, 57, 917_000_000).atZone(UTC))
            .host("foo")
            .service("myService")
            .status("error")
            .message("First line\nanother line")
            .build();
    assertThat(entries).containsExactly(expectedEntry1, expectedEntry2);
  }

  @Test
  public void internalParseCsv() {
    var rows = CsvImporter.internalParseCsv("A,B,C\na,b,c").get().toJavaList();
    assertThat(rows)
        .hasSize(2)
        .containsExactly(new String[] {"A", "B", "C"}, new String[] {"a", "b", "c"});

    var rows2 =
        CsvImporter.internalParseCsv("A,B,C\na,b,\"c1\"\"c2\"").get().toJavaList();
    assertThat(rows2)
        .hasSize(2)
        .containsExactly(new String[] {"A", "B", "C"}, new String[] {"a", "b", "c1\"c2"});

    var rows3 = CsvImporter.internalParseCsv("a,b,\"c1\nc2\"").get().toJavaList();
    assertThat(rows3).hasSize(1).containsExactly(new String[] {"a", "b", "c1\nc2"});
  }
}
