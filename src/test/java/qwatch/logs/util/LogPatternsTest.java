package qwatch.logs.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static qwatch.logs.util.LogPatterns.CANNOT_VERIFY_EA;
import static qwatch.logs.util.LogPatterns.ERR_CREATING_MANAGED_CONNECTION;
import static qwatch.logs.util.LogPatterns.FAILED_TO_CLONE_REPO;
import static qwatch.logs.util.LogPatterns.FAILED_TO_PARSE_REGISTRY;
import static qwatch.logs.util.LogPatterns.FEATURE_VALIDATION_FAILED;
import static qwatch.logs.util.LogPatterns.INCORRECT_VERSION_FILE;
import static qwatch.logs.util.LogPatterns.INTERNAL_ERR_RECEIVE_PACK;
import static qwatch.logs.util.LogPatterns.INTERNAL_ERR_UPLOAD_PACK;
import static qwatch.logs.util.LogPatterns.INVALID_REF_NAME;
import static qwatch.logs.util.LogPatterns.IO_EXCEPTION_ON_REQ_URL;
import static qwatch.logs.util.LogPatterns.JGIT_PACK_FILE;
import static qwatch.logs.util.LogPatterns.LOGIN_SERVICE_NOT_FOUND;
import static qwatch.logs.util.LogPatterns.NO_SUCH_PROJECT;
import static qwatch.logs.util.LogPatterns.PROJECT_NOT_FOUND;
import static qwatch.logs.util.LogPatterns.RESPONSE_COMMITTED;
import static qwatch.logs.util.LogPatterns.SERVICE_TICKET_MISMATCHED;
import static qwatch.logs.util.LogPatterns.SSO_AUTH_FAILED;
import static qwatch.logs.util.LogPatterns.STUDIO_PROJECT_NOT_FOUND_FOR_CONNECT;
import static qwatch.logs.util.LogPatterns.UNHANDLED_ERROR;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class LogPatternsTest {

  @Test
  public void patterns() {
    String s;
    assertThat(PROJECT_NOT_FOUND.matches("Project foo not found.")).isTrue();
    assertThat(PROJECT_NOT_FOUND.matches("Project foo-bar not found.")).isTrue();

    s =
        "java.lang.IllegalStateException: Cannot call sendError() after the response has been committed";
    assertThat(RESPONSE_COMMITTED.matches(s)).isTrue();

    s = "Could not verify if early access is enabled for project xxx: no associated studio project";
    assertThat(CANNOT_VERIFY_EA.matches(s)).isTrue();

    s = "Failed to parse registry from {...}";
    assertThat(FAILED_TO_PARSE_REGISTRY.matches(s)).isTrue();

    s =
        "javax.servlet.ServletException: java.lang.IllegalStateException: org.eclipse.jgit.api.errors.JGitInternalException: Invalid ref name: HEAD";
    assertThat(INVALID_REF_NAME.matches(s)).isTrue();

    s = "java.io.IOException: On requestURL: http://localhost:8186/nuxeo/git/foo.git/info/refs";
    assertThat(IO_EXCEPTION_ON_REQ_URL.matches(s)).isTrue();

    s = "No such project foo";
    assertThat(NO_SUCH_PROJECT.matches(s)).isTrue();

    s = "The version file should be created for the branch feature/foo of the project pp";
    assertThat(INCORRECT_VERSION_FILE.matches(s)).isTrue();

    s =
        "Authentication has failed. Credentials may be incorrect or CAS cannot find authentication handler that supports [foo] of type [UsernamePasswordCredential].";
    assertThat(SSO_AUTH_FAILED.matches(s)).isTrue();

    s = "ERROR: Exception caught while accessing pack file xxx";
    assertThat(JGIT_PACK_FILE.matches(s)).isTrue();

    s =
        "remote=xxx, xxx,principal=RemoteConnectInstance,uri=xxx,session=xxx.test1,thread=xxx,info=Unhandled error was caught by the Filter";
    assertThat(UNHANDLED_ERROR.matches(s)).isTrue();

    s = "Validation failed for feature 'Domain'";
    assertThat(FEATURE_VALIDATION_FAILED.matches(s)).isTrue();

    s = "Internal error during receive-pack to /xxx/xxx/foo.git";
    assertThat(INTERNAL_ERR_RECEIVE_PACK.matches(s)).isTrue();

    s = "Internal error during upload-pack from /xxx/xxx/foo.git";
    assertThat(INTERNAL_ERR_UPLOAD_PACK.matches(s)).isTrue();

    s =
        "Error occurred creating ManagedConnection for handle: nullManagedConnectionInfo: org.apache.geronimo.connector.outbound.ManagedConnectionInfo@xxx. mc: null]";
    assertThat(ERR_CREATING_MANAGED_CONNECTION.matches(s)).isTrue();

    s = "Failed to clone remote repository for project: xxx-sandbox";
    assertThat(FAILED_TO_CLONE_REPO.matches(s)).isTrue();

    s = "Service ticket [foo] with service [bar] does not match supplied service [xxx]";
    assertThat(SERVICE_TICKET_MISMATCHED.matches(s)).isTrue();

    s = "Service @login not found for object: /path/to/package of type pkg";
    assertThat(LOGIN_SERVICE_NOT_FOUND.matches(s)).isTrue();

    s = "Couldn't find StudioProject for ConnectProject with id foo";
    assertThat(STUDIO_PROJECT_NOT_FOUND_FOR_CONNECT.matches(s)).isTrue();
  }

  @Test
  public void head() {
    assertThat(LogPatterns.head("1\n2")).isEqualTo("1");
    assertThat(LogPatterns.head("1\r2")).isEqualTo("1");
    assertThat(LogPatterns.head("1\r\n2")).isEqualTo("1");
  }
}
