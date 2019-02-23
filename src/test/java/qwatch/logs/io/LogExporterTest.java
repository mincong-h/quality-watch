package qwatch.logs.io;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeSet;
import io.vavr.control.Try;
import java.nio.file.Files;
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
  private final LogEntry d1e1 =
      LogEntry.newBuilder()
          .dateTime(LocalDateTime.of(2019, 1, 1, 0, 0, 0).atZone(Z))
          .host("aHost")
          .message("aMessage")
          .service("aService")
          .status("error")
          .build();
  private final LogEntry d1e2 =
      d1e1.toBuilder().dateTime(LocalDateTime.of(2019, 1, 1, 1, 0, 0).atZone(Z)).build();
  private final LogEntry d2e1 =
      d1e1.toBuilder().dateTime(LocalDateTime.of(2019, 1, 2, 0, 0, 0).atZone(Z)).build();
  private final LogEntry d2e2 =
      d1e1.toBuilder().dateTime(LocalDateTime.of(2019, 1, 2, 1, 0, 0).atZone(Z)).build();
  private LogExporter exporter;

  @Before
  public void setUp() {
    exporter = new LogExporter(tempFolder.getRoot().toPath());
  }

  @Test
  public void exportJson_ok() {
    // Given 4 log entries, 2 for day 1 and 2 for day 2
    SortedSet<LogEntry> entriesD1 = TreeSet.of(LogEntry.BY_DATE, d1e1, d1e2);
    SortedSet<LogEntry> entriesD2 = TreeSet.of(LogEntry.BY_DATE, d2e1, d2e2);
    Map<LocalDate, SortedSet<LogEntry>> entriesByDay =
        HashMap.of(LocalDate.of(2019, 1, 1), entriesD1, LocalDate.of(2019, 1, 2), entriesD2);

    // When exporting them to filesystem
    exporter.exportJson(entriesByDay);

    // Then the export is successful
    Path pathD1 = tempFolder.getRoot().toPath().resolve("log.2019-01-01.json");
    Path pathD2 = tempFolder.getRoot().toPath().resolve("log.2019-01-02.json");
    assertThat(pathD1).exists();
    assertThat(pathD2).exists();
  }

  @Test
  public void exportJson_pathAlreadyExists() throws Exception {
    // Given an existing path for 2019-01-01
    Path p = tempFolder.newFile("log.2019-01-01.json").toPath();
    Files.write(p, List.of("[ ]"));

    // When export log entries as JSON again
    SortedSet<LogEntry> entriesD1 = TreeSet.of(LogEntry.BY_DATE, d1e1, d1e2);
    Map<LocalDate, SortedSet<LogEntry>> entriesByDay =
        HashMap.of(LocalDate.of(2019, 1, 1), entriesD1);
    Try<Void> export = exporter.exportJson(entriesByDay);

    // Then the json file has been deleted and recreated again
    assertThat(export.isSuccess()).isTrue();
    assertThat(Files.readAllLines(p)).hasSize(13);
  }
}
