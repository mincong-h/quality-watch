package qwatch.jenkins.model;

import java.io.StringWriter;
import org.junit.Before;
import org.junit.Test;
import qwatch.jenkins.util.ObjectMapperFactory;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class SurefireTestSuiteTest {

  private String xml;

  @Before
  public void setUp() {
    // language=XML
    xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<testsuite\n"
            + "  version=\"3.0\"\n"
            + "  name=\"com.nuxeo.studio.api.BranchServiceTest\"\n"
            + "  time=\"231.307\"\n"
            + "  tests=\"30\"\n"
            + "  errors=\"0\"\n"
            + "  skipped=\"0\"\n"
            + "  failures=\"0\">\n"
            + "\n"
            + "  <properties>\n"
            + "    <property\n"
            + "      name=\"sun.io.unicode.encoding\"\n"
            + "      value=\"UnicodeLittle\"/>\n"
            + "    <property\n"
            + "      name=\"java.class.version\"\n"
            + "      value=\"52.0\"/>\n"
            + "  </properties>\n"
            + "  <testcase\n"
            + "    name=\"getHistoryMultiplePages\"\n"
            + "    classname=\"com.nuxeo.studio.api.BranchServiceTest\"\n"
            + "    time=\"1.121\"/>\n"
            + "  <testcase\n"
            + "    name=\"getHistoryProjectNonExisting\"\n"
            + "    classname=\"com.nuxeo.studio.api.BranchServiceTest\"\n"
            + "    time=\"0.036\"/>\n"
            + "</testsuite>";
  }

  @Test
  public void deserialization() throws Exception {
    var mapper = ObjectMapperFactory.newXmlMapper();
    var suite = mapper.readValue(xml.getBytes(UTF_8), SurefireTestSuite.class);

    var c1 =
        SurefireTestCase.newBuilder()
            .name("getHistoryMultiplePages")
            .className("com.nuxeo.studio.api.BranchServiceTest")
            .time(1.121)
            .build();
    var c2 =
        SurefireTestCase.newBuilder()
            .name("getHistoryProjectNonExisting")
            .className("com.nuxeo.studio.api.BranchServiceTest")
            .time(0.036)
            .build();
    assertThat(suite.testCases()).containsExactly(c1, c2);
  }

  @Test
  public void serialization() throws Exception {
    var mapper = ObjectMapperFactory.newXmlMapper();
    var tc = SurefireTestCase.newBuilder().name("n").className("c").time(0.1).build();
    var suite = SurefireTestSuite.newBuilder().testCases(tc).build();
    try (var writer = new StringWriter()) {
      mapper.writeValue(writer, suite);
      // language=XML
      var s = "<testsuite><testcase name=\"n\" classname=\"c\" time=\"0.1\"/></testsuite>";
      assertThat(writer.toString()).isEqualTo(s);
    }
  }
}
