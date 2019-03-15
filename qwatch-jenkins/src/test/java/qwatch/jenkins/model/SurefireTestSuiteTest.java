package qwatch.jenkins.model;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
@SuppressWarnings("squid:S1192") // repeated string literals
public class SurefireTestSuiteTest {

  private final XmlMapper mapper = ObjectMapperFactory.newXmlMapper();

  private String xml;

  @Before
  public void setUp() {
    // language=XML
    xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<testsuite\n"
            + "  version=\"3.0\"\n"
            + "  name=\"pkg.BranchServiceTest\"\n"
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
            + "    classname=\"pkg.BranchServiceTest\"\n"
            + "    time=\"1.121\"/>\n"
            + "  <testcase\n"
            + "    name=\"getHistoryProjectNonExisting\"\n"
            + "    classname=\"pkg.BranchServiceTest\"\n"
            + "    time=\"0.036\"/>\n"
            + "</testsuite>";
  }

  @Test
  public void deserialization() throws Exception {
    var actualSuite = mapper.readValue(xml.getBytes(UTF_8), SurefireTestSuite.class);

    var p1 = SurefireProperty.of("sun.io.unicode.encoding", "UnicodeLittle");
    var p2 = SurefireProperty.of("java.class.version", "52.0");
    var c1 =
        SurefireTestCase.newBuilder()
            .name("getHistoryMultiplePages")
            .className("pkg.BranchServiceTest")
            .time(1.121)
            .build();
    var c2 =
        SurefireTestCase.newBuilder()
            .name("getHistoryProjectNonExisting")
            .className("pkg.BranchServiceTest")
            .time(0.036)
            .build();
    var expectedSuite =
        SurefireTestSuite.newBuilder()
            .name("pkg.BranchServiceTest")
            .time(231.307)
            .testCount(30)
            .errorCount(0)
            .skippedCount(0)
            .failureCount(0)
            .properties(p1, p2)
            .testCases(c1, c2)
            .build();
    assertThat(actualSuite).isEqualTo(expectedSuite);
  }

  @Test
  public void serialization() throws Exception {
    var prop = SurefireProperty.of("key", "val");
    var tc = SurefireTestCase.newBuilder().name("n").className("c").time(0.1).build();
    var suite =
        SurefireTestSuite.newBuilder()
            .name("MyTest")
            .time(2.0)
            .testCount(2)
            .errorCount(0)
            .skippedCount(0)
            .failureCount(0)
            .properties(prop)
            .testCases(tc)
            .build();
    try (var writer = new StringWriter()) {
      mapper.writeValue(writer, suite);
      // language=XML
      var s =
          "<testsuite name=\"MyTest\" time=\"2.0\" tests=\"2\" errors=\"0\" skipped=\"0\" failures=\"0\"><properties><properties name=\"key\" value=\"val\"/></properties><testcase name=\"n\" classname=\"c\" time=\"0.1\"/></testsuite>";
      assertThat(writer.toString()).isEqualTo(s);
    }
  }
}
