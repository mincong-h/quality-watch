package qwatch.logs.util;

import io.vavr.collection.Set;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
public class JsonImportUtilTest {
  @Rule public TemporaryFolder tempDir = new TemporaryFolder();

  @Test
  public void importLogEntriesFromFile_noDuplicates() throws Exception {
    // Given an existing JSON file with duplicates
    String content =
        "[{\n"
            + "  \"date\" : \"2019-02-14T12:44:20.962Z\",\n"
            + "  \"host\" : \"nos-prod-nuxp-15-10-3-8-228-euw\",\n"
            + "  \"service\" : \"nos-15\",\n"
            + "  \"status\" : \"error\",\n"
            + "  \"message\" : \"Project experimental2017 not found.\"\n"
            + "}, {\n"
            + "  \"date\" : \"2019-02-14T12:44:20.962Z\",\n"
            + "  \"host\" : \"nos-prod-nuxp-15-10-3-8-228-euw\",\n"
            + "  \"service\" : \"nos-15\",\n"
            + "  \"status\" : \"error\",\n"
            + "  \"message\" : \"Project experimental2017 not found.\"\n"
            + "}]";
    Path json = tempDir.newFile().toPath();
    Files.write(json, content.getBytes(UTF_8));

    // When importing the content
    Set<LogEntry> logEntries = JsonImportUtil.importLogEntriesFromFile(json);

    // Then the content is imported without duplicates
    ZonedDateTime d =
        LocalDateTime.of(2019, 2, 14, 12, 44, 20, 962_000_000).atZone(ZoneId.of("UTC"));
    LogEntry expectedEntry =
        LogEntry.newBuilder()
            .dateTime(d)
            .host("nos-prod-nuxp-15-10-3-8-228-euw")
            .message("Project experimental2017 not found.")
            .service("nos-15")
            .status("error")
            .build();
    assertThat(logEntries).hasSize(1).containsExactly(expectedEntry);
  }

  @Test
  public void importLogEntriesFromFile_notJson() {
    Path nonexistent = tempDir.getRoot().toPath().resolve("nonexistent");
    Set<LogEntry> importedSet = JsonImportUtil.importLogEntriesFromFile(nonexistent);
    assertThat(importedSet).isEmpty();
  }
}
