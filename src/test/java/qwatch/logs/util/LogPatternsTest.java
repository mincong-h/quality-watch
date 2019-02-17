package qwatch.logs.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static qwatch.logs.util.LogPatterns.P_CANNOT_VERIFY_EA;
import static qwatch.logs.util.LogPatterns.P_ERR_CREATING_MANAGED_CONNECTION;
import static qwatch.logs.util.LogPatterns.P_FAILED_TO_CLONE_REPO;
import static qwatch.logs.util.LogPatterns.P_FAILED_TO_PARSE_REGISTRY;
import static qwatch.logs.util.LogPatterns.P_FEATURE_VALIDATION_FAILED;
import static qwatch.logs.util.LogPatterns.P_INCORRECT_VERSION_FILE;
import static qwatch.logs.util.LogPatterns.P_INTERNAL_ERR_RECEIVE_PACK;
import static qwatch.logs.util.LogPatterns.P_INTERNAL_ERR_UPLOAD_PACK;
import static qwatch.logs.util.LogPatterns.P_INVALID_REF_NAME;
import static qwatch.logs.util.LogPatterns.P_IO_EXCEPTION_ON_REQ_URL;
import static qwatch.logs.util.LogPatterns.P_JGIT_PACK_FILE;
import static qwatch.logs.util.LogPatterns.P_MKP_LOGIN_SERVICE_NOT_FOUND_FOR_PKG;
import static qwatch.logs.util.LogPatterns.P_NO_SUCH_PROJECT;
import static qwatch.logs.util.LogPatterns.P_PROJECT_NOT_FOUND;
import static qwatch.logs.util.LogPatterns.P_RESPONSE_COMMITTED;
import static qwatch.logs.util.LogPatterns.P_SERVICE_TICKET_MISMATCHED;
import static qwatch.logs.util.LogPatterns.P_SSO_AUTH_FAILED;
import static qwatch.logs.util.LogPatterns.P_UNHANDLED_ERROR;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class LogPatternsTest {

  @Test
  public void patterns() {
    String s;
    assertThat(P_PROJECT_NOT_FOUND.matcher("Project foo not found.").matches()).isTrue();
    assertThat(P_PROJECT_NOT_FOUND.matcher("Project foo-bar not found.").matches()).isTrue();

    s =
        "java.lang.IllegalStateException: Cannot call sendError() after the response has been committed";
    assertThat(P_RESPONSE_COMMITTED.matcher(s).matches()).isTrue();

    s = "Could not verify if early access is enabled for project xxx: no associated studio project";
    assertThat(P_CANNOT_VERIFY_EA.matcher(s).matches()).isTrue();

    s = "Failed to parse registry from {...}";
    assertThat(P_FAILED_TO_PARSE_REGISTRY.matcher(s).matches()).isTrue();

    s =
        "javax.servlet.ServletException: java.lang.IllegalStateException: org.eclipse.jgit.api.errors.JGitInternalException: Invalid ref name: HEAD";
    assertThat(P_INVALID_REF_NAME.matcher(s).matches()).isTrue();

    s = "java.io.IOException: On requestURL: http://localhost:8186/nuxeo/git/foo.git/info/refs";
    assertThat(P_IO_EXCEPTION_ON_REQ_URL.matcher(s).matches()).isTrue();

    s = "No such project foo";
    assertThat(P_NO_SUCH_PROJECT.matcher(s).matches()).isTrue();

    s = "The version file should be created for the branch feature/foo of the project pp";
    assertThat(P_INCORRECT_VERSION_FILE.matcher(s).matches()).isTrue();

    s =
        "Authentication has failed. Credentials may be incorrect or CAS cannot find authentication handler that supports [foo] of type [UsernamePasswordCredential].";
    assertThat(P_SSO_AUTH_FAILED.matcher(s).matches()).isTrue();

    s = "ERROR: Exception caught while accessing pack file xxx";
    assertThat(P_JGIT_PACK_FILE.matcher(s).matches()).isTrue();

    s =
        "remote=xxx, xxx,principal=RemoteConnectInstance,uri=xxx,session=xxx.test1,thread=xxx,info=Unhandled error was caught by the Filter";
    assertThat(P_UNHANDLED_ERROR.matcher(s).matches()).isTrue();

    s = "Validation failed for feature 'Domain'";
    assertThat(P_FEATURE_VALIDATION_FAILED.matcher(s).matches()).isTrue();

    s = "Internal error during receive-pack to /xxx/xxx/foo.git";
    assertThat(P_INTERNAL_ERR_RECEIVE_PACK.matcher(s).matches()).isTrue();

    s = "Internal error during upload-pack from /xxx/xxx/foo.git";
    assertThat(P_INTERNAL_ERR_UPLOAD_PACK.matcher(s).matches()).isTrue();

    s =
        "Error occurred creating ManagedConnection for handle: nullManagedConnectionInfo: org.apache.geronimo.connector.outbound.ManagedConnectionInfo@xxx. mc: null]";
    assertThat(P_ERR_CREATING_MANAGED_CONNECTION.matcher(s).matches()).isTrue();

    s = "Failed to clone remote repository for project: xxx-sandbox";
    assertThat(P_FAILED_TO_CLONE_REPO.matcher(s).matches()).isTrue();

    s = "Service ticket [foo] with service [bar] does not match supplied service [xxx]";
    assertThat(P_SERVICE_TICKET_MISMATCHED.matcher(s).matches()).isTrue();

    s = "Service @login not found for object: /path/to/package of type pkg";
    assertThat(P_MKP_LOGIN_SERVICE_NOT_FOUND_FOR_PKG.matcher(s).matches()).isTrue();
  }
}
