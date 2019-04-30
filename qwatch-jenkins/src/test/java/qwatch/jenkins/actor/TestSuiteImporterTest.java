package qwatch.jenkins.actor;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class TestSuiteImporterTest {

  @Rule public TemporaryFolder tempDir = new TemporaryFolder();

  @Test
  public void findReports() throws Exception {
    tempDir.newFolder("target");
    tempDir.newFolder("target", "surefire-reports");
    tempDir.newFolder("target", "failsafe-reports");
    tempDir.newFolder("target", "whatever-reports");
    tempDir.newFolder("surefire-reports");
    tempDir.newFolder("failsafe-reports");
    var p1 = tempDir.newFile("target/surefire-reports/TEST-1.xml").toPath();
    var p2 = tempDir.newFile("target/surefire-reports/TEST-2.xml").toPath();
    var p3 = tempDir.newFile("target/failsafe-reports/TEST-3.xml").toPath();
    var p4 = tempDir.newFile("target/failsafe-reports/TEST-4.xml").toPath();
    var p5 = tempDir.newFile("target/whatever-reports/TEST-5.xml").toPath();
    var p6 = tempDir.newFile("target/whatever-reports/TEST-6.xml").toPath();
    var p7 = tempDir.newFile("surefire-reports/TEST-7.xml").toPath();
    var p8 = tempDir.newFile("surefire-reports/TEST-8.xml").toPath();
    var p9 = tempDir.newFile("failsafe-reports/TEST-9.xml").toPath();
    var p10 = tempDir.newFile("failsafe-reports/TEST-10.xml").toPath();

    var paths = TestSuiteImporter.findReports(tempDir.getRoot().toPath()).get();

    assertThat(paths)
        .containsExactlyInAnyOrder(p1, p2, p3, p4)
        .doesNotContain(p5, p6, p7, p8, p9, p10);
  }
}
