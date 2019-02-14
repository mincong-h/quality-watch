package qwatch.logs.util;

import io.vavr.collection.TreeSet;
import java.nio.file.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import qwatch.logs.model.LogEntry;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class JsonExportUtilTest {

  @Rule public TemporaryFolder tempDir = new TemporaryFolder();

  @Test
  public void export() throws Exception {
    Path destination = tempDir.newFile().toPath();
    JsonExportUtil.export(destination, TreeSet.empty(LogEntry.BY_DATE));
    assertThat(destination).hasContent("[ ]");
  }
}
