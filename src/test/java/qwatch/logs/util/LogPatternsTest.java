package qwatch.logs.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static qwatch.logs.util.LogPatterns.P_CANNOT_VERIFY_EA;
import static qwatch.logs.util.LogPatterns.P_FAILED_TO_PARSE_REGISTRY;
import static qwatch.logs.util.LogPatterns.P_INCORRECT_VERSION_FILE;
import static qwatch.logs.util.LogPatterns.P_INVALID_REF_NAME;
import static qwatch.logs.util.LogPatterns.P_IO_EXCEPTION_ON_REQ_URL;
import static qwatch.logs.util.LogPatterns.P_JGIT_PACK_FILE;
import static qwatch.logs.util.LogPatterns.P_NO_SUCH_PROJECT;
import static qwatch.logs.util.LogPatterns.P_PROJECT_NOT_FOUND;
import static qwatch.logs.util.LogPatterns.P_RESPONSE_COMMITTED;
import static qwatch.logs.util.LogPatterns.P_SSO_AUTH_FAILED;
import static qwatch.logs.util.LogPatterns.P_UNHANDLED_ERROR;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class LogPatternsTest {

  @Test
  public void pattern_projectNotFound() {
    assertThat(P_PROJECT_NOT_FOUND.matcher("Project foo not found.").matches()).isTrue();
    assertThat(P_PROJECT_NOT_FOUND.matcher("Project foo-bar not found.").matches()).isTrue();
  }

  @Test
  public void pattern_responseCommitted() {
    String s =
        "java.lang.IllegalStateException: Cannot call sendError() after the response has been committed";
    assertThat(P_RESPONSE_COMMITTED.matcher(s).matches()).isTrue();
  }

  @Test
  public void patter_cannotVerifyEarlyAccess() {
    String s =
        "Could not verify if early access is enabled for project xxx: no associated studio project";
    assertThat(P_CANNOT_VERIFY_EA.matcher(s).matches()).isTrue();
  }

  @Test
  public void pattern_failedToParseRegistry() {
    String s = "Failed to parse registry from {...}";
    assertThat(P_FAILED_TO_PARSE_REGISTRY.matcher(s).matches()).isTrue();
  }

  @Test
  public void pattern_invalidRefName() {
    String s =
        "javax.servlet.ServletException: java.lang.IllegalStateException: org.eclipse.jgit.api.errors.JGitInternalException: Invalid ref name: HEAD";
    assertThat(P_INVALID_REF_NAME.matcher(s).matches()).isTrue();
  }

  @Test
  public void pattern_ioExceptionOnRequestUrl() {
    String s =
        "java.io.IOException: On requestURL: http://localhost:8186/nuxeo/git/foo.git/info/refs";
    assertThat(P_IO_EXCEPTION_ON_REQ_URL.matcher(s).matches()).isTrue();
  }

  @Test
  public void pattern_noSuchProject() {
    String s = "No such project foo";
    assertThat(P_NO_SUCH_PROJECT.matcher(s).matches()).isTrue();
  }

  @Test
  public void pattern_incorrectVersionFile() {
    String s = "The version file should be created for the branch feature/foo of the project pp";
    assertThat(P_INCORRECT_VERSION_FILE.matcher(s).matches()).isTrue();
  }

  @Test
  public void pattern_ssoAuthFailed() {
    String s =
        "Authentication has failed. Credentials may be incorrect or CAS cannot find authentication handler that supports [foo] of type [UsernamePasswordCredential].";
    assertThat(P_SSO_AUTH_FAILED.matcher(s).matches()).isTrue();
  }

  @Test
  public void pattern_jgitPackFile() {
    String s = "ERROR: Exception caught while accessing pack file xxx";
    assertThat(P_JGIT_PACK_FILE.matcher(s).matches()).isTrue();
  }

  @Test
  public void pattern_unhandledError() {
    String s =
        "remote=xxx, xxx,principal=RemoteConnectInstance,uri=xxx,session=xxx.test1,thread=xxx,info=Unhandled error was caught by the Filter";
    assertThat(P_UNHANDLED_ERROR.matcher(s).matches()).isTrue();
  }
}
