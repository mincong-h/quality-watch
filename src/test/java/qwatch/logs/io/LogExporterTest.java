package qwatch.logs.io;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeSet;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
public class LogExporterTest {
  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  private final ZoneId Z = ZoneId.of("Z");
  private LogExporter exporter;

  @Before
  public void setUp() {
    exporter = new LogExporter(tempFolder.getRoot().toPath());
  }

  @Test
  public void export() {
    // Given 4 log entries, 2 for day 1 and 2 for day 2
    LogEntry d1a =
        LogEntry.newBuilder()
            .dateTime(LocalDateTime.of(2019, 1, 1, 0, 0, 0).atZone(Z))
            .host("aHost")
            .message("aMessage")
            .service("aService")
            .status("error")
            .build();
    LogEntry d1b =
        d1a.toBuilder().dateTime(LocalDateTime.of(2019, 1, 1, 1, 0, 0).atZone(Z)).build();
    SortedSet<LogEntry> entriesD1 = TreeSet.of(LogEntry.BY_DATE, d1a, d1b);

    LogEntry d2a =
        LogEntry.newBuilder()
            .dateTime(LocalDateTime.of(2019, 1, 2, 0, 0, 0).atZone(Z))
            .host("aHost")
            .message("aMessage")
            .service("aService")
            .status("error")
            .build();
    LogEntry d2b =
        d2a.toBuilder().dateTime(LocalDateTime.of(2019, 1, 2, 1, 0, 0).atZone(Z)).build();
    SortedSet<LogEntry> entriesD2 = TreeSet.of(LogEntry.BY_DATE, d2a, d2b);

    // When exporting them to filesystem
    Map<LocalDate, SortedSet<LogEntry>> entriesByDay =
        HashMap.of(LocalDate.of(2019, 1, 1), entriesD1, LocalDate.of(2019, 1, 2), entriesD2);
    exporter.export(entriesByDay);

    // Then the export is successful
    Path pathD1 = tempFolder.getRoot().toPath().resolve("log.2019-01-01.json");
    Path pathD2 = tempFolder.getRoot().toPath().resolve("log.2019-01-02.json");
    assertThat(pathD1).exists();
    assertThat(pathD2).exists();
  }
}
