package qwatch.logs.io;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import qwatch.logs.model.LogEntry;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class JsonImporterTest {
  @Rule public TemporaryFolder tempDir = new TemporaryFolder();

  private Path json;

  @Before
  public void setUp() throws Exception {
    json = tempDir.newFile("log.2019-02-14.json").toPath();
    var content =
        "[{\n"
            + "  \"date\" : \"2019-02-14T12:44:20.962Z\",\n"
            + "  \"host\" : \"myHost\",\n"
            + "  \"service\" : \"myService\",\n"
            + "  \"status\" : \"error\",\n"
            + "  \"message\" : \"Project myProject not found.\"\n"
            + "}, {\n"
            + "  \"date\" : \"2019-02-14T12:44:20.962Z\",\n"
            + "  \"host\" : \"myHost\",\n"
            + "  \"service\" : \"myService\",\n"
            + "  \"status\" : \"error\",\n"
            + "  \"message\" : \"Project myProject not found.\"\n"
            + "}]";
    Files.write(json, content.getBytes(UTF_8));
  }

  @Test
  public void importLogEntriesFromFile_noDuplicates() {
    // Given an existing JSON file with duplicates
    // When importing the content
    var logEntries = JsonImporter.importLogEntriesFromFile(json).get();

    // Then the content is imported without duplicates
    var d = LocalDateTime.of(2019, 2, 14, 12, 44, 20, 962_000_000).atZone(ZoneId.of("UTC"));
    var expectedEntry =
        LogEntry.newBuilder()
            .dateTime(d)
            .host("myHost")
            .message("Project myProject not found.")
            .service("myService")
            .status("error")
            .build();
    assertThat(logEntries).hasSize(1).containsExactly(expectedEntry);
  }

  @Test
  public void importLogEntriesFromFile_notJson() {
    var nonexistent = tempDir.getRoot().toPath().resolve("nonexistent");
    assertThat(JsonImporter.importLogEntriesFromFile(nonexistent).isFailure()).isTrue();
  }

  @Test
  public void importLogEntries_noDuplicates() {
    var logEntries = JsonImporter.importLogEntries(tempDir.getRoot().toPath()).get();
    assertThat(logEntries).hasSize(1);
  }

  @Test
  public void listLogPaths() {
    var logPaths = JsonImporter.listLogPaths(tempDir.getRoot().toPath()).get();
    assertThat(logPaths).containsExactly(json);
  }
}
