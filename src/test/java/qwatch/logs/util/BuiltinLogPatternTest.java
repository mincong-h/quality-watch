package qwatch.logs.util;

import io.vavr.collection.Stream;
import org.junit.Test;
import qwatch.logs.model.BuiltinLogPattern;
import qwatch.logs.model.LogPattern;

import static org.assertj.core.api.Assertions.assertThat;
import static qwatch.logs.model.BuiltinLogPattern.CANNOT_FORWARD_TO_ERR_PAGE;
import static qwatch.logs.model.BuiltinLogPattern.CANNOT_VERIFY_EA;
import static qwatch.logs.model.BuiltinLogPattern.ERROR_400_JIRA;
import static qwatch.logs.model.BuiltinLogPattern.ERROR_EXECUTING_FREEMARKER;
import static qwatch.logs.model.BuiltinLogPattern.ERR_CREATING_MANAGED_CONNECTION;
import static qwatch.logs.model.BuiltinLogPattern.FAILED_INIT_PROJECT;
import static qwatch.logs.model.BuiltinLogPattern.FAILED_TO_CLONE_REPO;
import static qwatch.logs.model.BuiltinLogPattern.FAILED_TO_PARSE_REGISTRY;
import static qwatch.logs.model.BuiltinLogPattern.FEATURE_VALIDATION_FAILED;
import static qwatch.logs.model.BuiltinLogPattern.INCORRECT_VERSION_FILE;
import static qwatch.logs.model.BuiltinLogPattern.INTERNAL_ERR_RECEIVE_PACK;
import static qwatch.logs.model.BuiltinLogPattern.INTERNAL_ERR_UPLOAD_PACK;
import static qwatch.logs.model.BuiltinLogPattern.INVALID_REF_NAME;
import static qwatch.logs.model.BuiltinLogPattern.IO_EXCEPTION_ON_REQ_URL;
import static qwatch.logs.model.BuiltinLogPattern.JGIT_PACK_FILE;
import static qwatch.logs.model.BuiltinLogPattern.LOGIN_SERVICE_NOT_FOUND;
import static qwatch.logs.model.BuiltinLogPattern.NO_SUCH_PROJECT;
import static qwatch.logs.model.BuiltinLogPattern.PROJECT_NOT_FOUND;
import static qwatch.logs.model.BuiltinLogPattern.RESET_ON_HEAD_FAILED;
import static qwatch.logs.model.BuiltinLogPattern.RESPONSE_COMMITTED;
import static qwatch.logs.model.BuiltinLogPattern.SERVICE_TICKET_MISMATCHED;
import static qwatch.logs.model.BuiltinLogPattern.SSO_AUTH_FAILED;
import static qwatch.logs.model.BuiltinLogPattern.STUDIO_PROJECT_NOT_FOUND_FOR_CONNECT;
import static qwatch.logs.model.BuiltinLogPattern.UNABLE_GET_REGISTRY;
import static qwatch.logs.model.BuiltinLogPattern.UNHANDLED_ERROR;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class BuiltinLogPatternTest {

  @Test
  public void longMsg_noDuplicates() {
    int expectedSize = BuiltinLogPattern.values().length;
    int actualSize = Stream.of(BuiltinLogPattern.values()).map(LogPattern::longMsg).toSet().size();
    assertThat(expectedSize).isEqualTo(actualSize);
  }

  @Test
  public void shortMsg_noDuplicates() {
    int expectedSize = BuiltinLogPattern.values().length;
    int actualSize = Stream.of(BuiltinLogPattern.values()).map(LogPattern::shortMsg).toSet().size();
    assertThat(expectedSize).isEqualTo(actualSize);
  }

  @Test
  public void values() {
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
    s =
        "javax.servlet.ServletException: java.lang.IllegalStateException: com.nuxeo.studio.core.api.exception.VersioningException: /path/to/foo.git: Invalid ref name: HEAD";
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
        "ERROR: Exception caught while accessing pack file /efs/studio_git_repos/vdutat-sandbox-710-nuxeo.git/objects/pack/pack-b0cd96843feb2f483322d84c600d7be4e1c95609.pack, the pack file might be corrupt, {1}. Caught {2} consecutive errors while trying to read this pack.";
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

    s = "Failed to resetOnHead: /path/to/project.git";
    assertThat(RESET_ON_HEAD_FAILED.matches(s)).isTrue();

    s = "Failed to initialize project foo";
    assertThat(FAILED_INIT_PROJECT.matches(s)).isTrue();

    s = "Failed to initialize project";
    assertThat(FAILED_INIT_PROJECT.matches(s)).isTrue();

    s = "Unable to get registries for package foo";
    assertThat(UNABLE_GET_REGISTRY.matches(s)).isTrue();

    s =
        "Cannot forward to error page for request [/login] as the response has already been committed.";
    assertThat(CANNOT_FORWARD_TO_ERR_PAGE.matches(s)).isTrue();

    s = "Error executing FreeMarker template";
    assertThat(ERROR_EXECUTING_FREEMARKER.matches(s)).isTrue();

    s = "Bad status when performing REST request to Jira: 400";
    assertThat(ERROR_400_JIRA.matches(s)).isTrue();
  }
}
