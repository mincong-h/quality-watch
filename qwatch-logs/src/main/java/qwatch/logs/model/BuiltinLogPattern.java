package qwatch.logs.model;

import java.util.regex.Pattern;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public enum BuiltinLogPattern implements LogPattern {
  PROJECT_NOT_FOUND(1) {

    @Override
    public String shortMsg() {
      return "Project ${id} not found";
    }

    @Override
    public String longMsg() {
      return "Project ${id} not found";
    }

    @Override
    public Pattern pattern() {
      return Pattern.compile("^Project (.*) not found.$");
    }
  },

  RESPONSE_COMMITTED(2) {

    @Override
    public String shortMsg() {
      return "Response has been committed";
    }

    @Override
    public String longMsg() {
      return "Cannot call sendError() after the response has been committed";
    }

    @Override
    public Pattern pattern() {
      return Pattern.compile(
          "^(.*)Cannot call sendError\\(\\) after the response has been committed$");
    }
  },

  CANNOT_VERIFY_EA(3) {

    @Override
    public String shortMsg() {
      return "Cannot verify early access";
    }

    @Override
    public String longMsg() {
      return "Could not verify if early access is enabled for project ${id}";
    }

    @Override
    public Pattern pattern() {
      return Pattern.compile("^Could not verify if early access is enabled for project (.*)$");
    }
  },

  FAILED_TO_PARSE_REGISTRY(4) {
    @Override
    public String shortMsg() {
      return "Failed to parse registry";
    }

    @Override
    public String longMsg() {
      return "Failed to parse registry from ${json}";
    }

    @Override
    public Pattern pattern() {
      return Pattern.compile("^Failed to parse registry from (.*)$");
    }
  },

  IO_EXCEPTION_ON_REQ_URL(5) {
    @Override
    public String shortMsg() {
      return "IOException on requestURL";
    }

    @Override
    public String longMsg() {
      return "java.io.IOException: On requestURL: ${url}";
    }

    @Override
    public Pattern pattern() {
      return Pattern.compile("^java.io.IOException: On requestURL:(.*)$");
    }
  },

  NO_SUCH_PROJECT(6) {
    @Override
    public String shortMsg() {
      return "No such project";
    }

    @Override
    public String longMsg() {
      return "No such project ${id}";
    }

    @Override
    public Pattern pattern() {
      return Pattern.compile("^No such project (.*)$");
    }
  },

  INCORRECT_VERSION_FILE(7) {

    @Override
    public String shortMsg() {
      return "Incorrect version file";
    }

    @Override
    public String longMsg() {
      return "The version file should be created for the branch ${name}";
    }

    @Override
    public Pattern pattern() {
      return Pattern.compile("^The version file should be created for the branch (.*)$");
    }
  },

  SSO_AUTH_FAILED(8) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Authentication has failed. Credentials may be incorrect (.*)");
    }

    @Override
    public String longMsg() {
      return "Authentication has failed. Credentials may be incorrect";
    }

    @Override
    public String shortMsg() {
      return "Authentication failed";
    }
  },

  UNHANDLED_ERROR(9) {
    @Override
    public String shortMsg() {
      return "Unhandled error";
    }

    @Override
    public String longMsg() {
      return "Unhandled error was caught by the Filter";
    }

    @Override
    public Pattern pattern() {
      return Pattern.compile("^(.*)Unhandled error was caught by the Filter$");
    }
  },

  FEATURE_VALIDATION_FAILED(10) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Validation failed for feature '(.*)'$");
    }

    @Override
    public String longMsg() {
      return "Validation failed for feature ${id}";
    }

    @Override
    public String shortMsg() {
      return "Validation failed for feature";
    }
  },

  ERR_CREATING_MANAGED_CONNECTION(11) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Error occurred creating ManagedConnection for handle: (.*)$");
    }

    @Override
    public String longMsg() {
      return "Error occurred creating ManagedConnection for handle: ${info}";
    }

    @Override
    public String shortMsg() {
      return "Error occurred creating ManagedConnection";
    }
  },

  SERVICE_TICKET_MISMATCHED(12) {
    @Override
    public Pattern pattern() {
      return Pattern.compile(
          "^Service ticket \\[(.*)] with service \\[(.*)] does not match supplied service \\[(.*)]$");
    }

    @Override
    public String longMsg() {
      return "Service ticket ${ticket} with service ${service} does not match supplied service ${suppliedService}";
    }

    @Override
    public String shortMsg() {
      return "Service ticket mismatched";
    }
  },

  STUDIO_PROJECT_NOT_FOUND_FOR_CONNECT(13) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Couldn't find StudioProject for ConnectProject with id (.*)$");
    }

    @Override
    public String longMsg() {
      return "Couldn't find StudioProject for ConnectProject with id ${projectId}";
    }

    @Override
    public String shortMsg() {
      return "StudioProject for ConnectProject";
    }
  },

  INVALID_REF_NAME(14) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^(.*)Invalid ref name(.*)$");
    }

    @Override
    public String longMsg() {
      return "Invalid ref name: ${ref}";
    }

    @Override
    public String shortMsg() {
      return "Invalid ref name";
    }
  },

  INTERNAL_ERR_RECEIVE_PACK(15) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Internal error during receive-pack to (.*)$");
    }

    @Override
    public String longMsg() {
      return "Internal error during receive-pack to ${gitPath}";
    }

    @Override
    public String shortMsg() {
      return "Receive-pack error";
    }
  },

  INTERNAL_ERR_UPLOAD_PACK(16) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Internal error during upload-pack from (.*)$");
    }

    @Override
    public String longMsg() {
      return "Internal error during upload-pack from ${gitPath}";
    }

    @Override
    public String shortMsg() {
      return "Upload-pack error";
    }
  },

  JGIT_PACK_FILE(17) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^(.*)Exception caught while accessing pack file (.*)$");
    }

    @Override
    public String longMsg() {
      return "Exception caught while accessing pack file ${pack}";
    }

    @Override
    public String shortMsg() {
      return "Exception caught while accessing pack file ${pack}";
    }
  },

  FAILED_TO_CLONE_REPO(18) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Failed to clone remote repository for project: (.*)$");
    }

    @Override
    public String longMsg() {
      return "Failed to clone remote repository for project: ${id}";
    }

    @Override
    public String shortMsg() {
      return "Failed to clone repository";
    }
  },

  LOGIN_SERVICE_NOT_FOUND(19) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Service @login not found for object: (.*) of type pkg$");
    }

    @Override
    public String longMsg() {
      return "Service @login not found for object: ${pkgPath} of type pkg";
    }

    @Override
    public String shortMsg() {
      return "Service @login not found";
    }
  },

  RESET_ON_HEAD_FAILED(20) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Failed to resetOnHead: (.*)$");
    }

    @Override
    public String longMsg() {
      return "Failed to resetOnHead: ${gitPath}";
    }

    @Override
    public String shortMsg() {
      return "ResetOnHead failed";
    }
  },

  FAILED_INIT_PROJECT(21) {
    @Override
    public Pattern pattern() {
      // Sometime we have the project id, sometime we don't.
      return Pattern.compile("^Failed to initialize project(.*)$");
    }

    @Override
    public String longMsg() {
      return "Failed to initialize project";
    }

    @Override
    public String shortMsg() {
      return "Failed to initialize project";
    }
  },

  UNABLE_GET_REGISTRY(22) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Unable to get registries for (.*)$");
    }

    @Override
    public String longMsg() {
      return "Unable to get registries for package/platform ${id}";
    }

    @Override
    public String shortMsg() {
      return "Unable to get registries";
    }
  },

  CANNOT_FORWARD_TO_ERR_PAGE(23) {
    @Override
    public Pattern pattern() {
      return Pattern.compile(
          "Cannot forward to error page for request (.*) as the response has already been committed.(.*)");
    }

    @Override
    public String longMsg() {
      return "Cannot forward to error page for request ${path} as the response has already been committed.";
    }

    @Override
    public String shortMsg() {
      return "Cannot forward to error page for request";
    }
  },

  /**
   * Error executing FreeMarker template
   *
   * @see <a href="https://jira.nuxeo.com/browse/NXCONNECT-2087"></a>
   */
  ERROR_EXECUTING_FREEMARKER(24) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Error executing FreeMarker template$");
    }

    @Override
    public String longMsg() {
      return "Error executing FreeMarker template";
    }

    @Override
    public String shortMsg() {
      return "Error executing FreeMarker template";
    }
  },

  /**
   * REST request to JIRA: 400
   *
   * @see <a href="https://jira.nuxeo.com/browse/NXCONNECT-2090">Add more details when failed to
   *     perform REST request to Jira: 400</a>
   */
  ERROR_400_JIRA(25) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Bad status when performing REST request to Jira: 400$");
    }

    @Override
    public String longMsg() {
      return "Bad status when performing REST request to Jira: 400";
    }

    @Override
    public String shortMsg() {
      return "REST request to Jira: 400";
    }
  },

  /**
   * Error while fetching stats.
   *
   * <p>No instance found for CLID: ${clid}
   */
  ERROR_FETCHING_STATUS(26) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Error while fetching status$");
    }

    @Override
    public String longMsg() {
      return "Error while fetching status";
    }

    @Override
    public String shortMsg() {
      return "Error while fetching status";
    }
  },

  CANNOT_PULL_WIP_BRANCH(27) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^(.*): Could not pull WIP branch (.) because it has WIP commit$");
    }

    @Override
    public String longMsg() {
      return "Could not pull WIP branch ${ref} because it has WIP commit";
    }

    @Override
    public String shortMsg() {
      return "Could not pull WIP branch";
    }
  },

  WORKSPACE_STREAM_CLOSED(28) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^(.*): (.*): Stream closed\\.$");
    }

    @Override
    public String longMsg() {
      return "${gitPath}: Stream closed.";
    }

    @Override
    public String shortMsg() {
      return "${gitPath}: Stream closed.";
    }
  },

  UNCAUGHT_ERROR_ON_THREAD(29) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Uncaught error on thread (.*)$");
    }

    @Override
    public String longMsg() {
      return "Uncaught error on thread ${thread}";
    }

    @Override
    public String shortMsg() {
      return "Uncaught error on thread ${thread}";
    }
  },

  BRANCH_NOT_FOUND(30) {
    @Override
    public Pattern pattern() {
      return Pattern.compile(
          "^No studio current snapshot The branch (.*) was not found for the current project$");
    }

    @Override
    public String longMsg() {
      return "The branch ${branch} was not found for the current project";
    }

    @Override
    public String shortMsg() {
      return "The branch ${branch} was not found for the current project";
    }
  },

  UNLOCKING_LOCKFILE_FAILED(31) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Unlocking LockFile '(.*/gc\\.log\\.lock)' failed$");
    }

    @Override
    public String longMsg() {
      return "Unlocking LockFile ${path/to/gc.log.lock} failed";
    }

    @Override
    public String shortMsg() {
      return "Unlocking LockFile ${path/to/gc.log.lock} failed";
    }
  },

  KILLED_HANDLE_JDBC(32) {
    @Override
    public Pattern pattern() {
      return Pattern.compile(
          "^Killed handle: org.tranql.connector.jdbc.ConnectionHandle@(.*)ManagedConnectionInfo(.*)$");
    }

    @Override
    public String longMsg() {
      return "Killed handle: org.tranql.connector.jdbc.ConnectionHandle...";
    }

    @Override
    public String shortMsg() {
      return "Killed handle: org.tranql.connector.jdbc.ConnectionHandle...";
    }
  },
  ERR_COMMITTING_LOCAL_XA_RESOURCE(33) {
    @Override
    public Pattern pattern() {
      return Pattern.compile(
          "^Unexpected exception committing org\\.apache\\.geronimo\\.connector\\.outbound\\.LocalXAResource@(.*); continuing to commit other RMs$");
    }

    @Override
    public String longMsg() {
      return "Unexpected exception committing org.apache.geronimo.connector.outbound.LocalXAResource; continuing to commit other RMs";
    }

    @Override
    public String shortMsg() {
      return "Unexpected exception committing org.apache.geronimo.connector.outbound.LocalXAResource";
    }
  },
  FAILED_TO_CREATE_REPOSITORY(34) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Failed to create repository for request=GitRepositoryCreate(.*)$");
    }

    @Override
    public String longMsg() {
      return "Failed to create repository for request=GitRepositoryCreate{...}";
    }

    @Override
    public String shortMsg() {
      return "Failed to create repository";
    }
  },
  FAILED_TO_DELETE_REPOSITORY(35) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Failed to delete repository (.*)$");
    }

    @Override
    public String longMsg() {
      return "Failed to delete repository ${projectId}";
    }

    @Override
    public String shortMsg() {
      return "Failed to delete repository";
    }
  },
  UNABLE_TO_REPLACE_OWNER_ID(36) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Unable to replace the owner id by its name \\[(.*)]$");
    }

    @Override
    public String longMsg() {
      return "Unable to replace the owner id by its name ${detail}";
    }

    @Override
    public String shortMsg() {
      return "Unable to replace the owner id by its name";
    }
  },
  INCOMPATIBLE_REMOTE_SERVICE_EXCEPTION(37) {
    @Override
    public Pattern pattern() {
      return Pattern.compile(
          "^studioRpc: An IncompatibleRemoteServiceException was thrown while processing this call\\.$");
    }

    @Override
    public String longMsg() {
      return "studioRpc: An IncompatibleRemoteServiceException was thrown while processing this call.";
    }

    @Override
    public String shortMsg() {
      return "studioRpc: IncompatibleRemoteServiceException";
    }
  },
  ERROR_WHILE_FETCHING_DOWNLOAD(38) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Error while fetching download$");
    }

    @Override
    public String longMsg() {
      return "Error while fetching download";
    }

    @Override
    public String shortMsg() {
      return "Error while fetching download";
    }
  },
  CANNOT_FORWARD_TO_ERROR_PAGE(39) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Cannot forward to error page: response is already committed$");
    }

    @Override
    public String longMsg() {
      return "Cannot forward to error page: response is already committed";
    }

    @Override
    public String shortMsg() {
      return "Cannot forward to error page";
    }
  },
  REQUEST_PROCESSING_ERROR(40) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Request Processing Error$");
    }

    @Override
    public String longMsg() {
      return "Request Processing Error";
    }

    @Override
    public String shortMsg() {
      return "Request Processing Error";
    }
  },
  SEGMENT_EVENT_EXEC_FAILED(41) {
    @Override
    public Pattern pattern() {
      return Pattern.compile(
          "^Failed to execute async event null on listener segmentIOEventListener$");
    }

    @Override
    public String longMsg() {
      return "Failed to execute async event null on listener segmentIOEventListener";
    }

    @Override
    public String shortMsg() {
      return "Failed to execute async event on segmentIOEventListener";
    }
  },
  SEGMENT_EXCEPTION_DURING_WORK(42) {
    @Override
    public Pattern pattern() {
      return Pattern.compile(
          "^Exception during work: ListenerWork\\(Listener segmentIOEventListener.*$");
    }

    @Override
    public String longMsg() {
      return "Exception during work: ListenerWork(Listener segmentIOEventListener ...)";
    }

    @Override
    public String shortMsg() {
      return "Exception during work: segmentIOEventListener";
    }
  },
  REQUEST_ATTRIBUTE_RESPONSE_COMMITTED(43) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Request Attributes.*$", Pattern.MULTILINE);
    }

    @Override
    public String longMsg() {
      return "Request Attributes (response has been committed)";
    }

    @Override
    public String shortMsg() {
      return "Request Attributes (response has been committed)";
    }

    @Override
    public boolean matches(String s) {
      return pattern().matcher(s).find();
    }
  },
  PROJECT_REMOVAL_LISTENER_FAILED(44) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Exception during projectRemovalListener sync listener execution.*");
    }

    @Override
    public String longMsg() {
      return "Exception during projectRemovalListener sync listener execution";
    }

    @Override
    public String shortMsg() {
      return "Exception during projectRemovalListener sync listener execution";
    }
  },
  UNKNOWN_DB_CONNECTION(45) {
    @Override
    public Pattern pattern() {
      return Pattern.compile(
          "^java.lang.IllegalStateException: unknown connection org.nuxeo.ecm.core.storage.sql.ra.ConnectionImpl.*$");
    }

    @Override
    public String longMsg() {
      return "unknown connection org.nuxeo.ecm.core.storage.sql.ra.ConnectionImpl";
    }

    @Override
    public String shortMsg() {
      return "unknown connection org.nuxeo.ecm.core.storage.sql.ra.ConnectionImpl";
    }
  },
  KILL_HANDLE_DB_CONNECTION(46) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^Killed handle: org.nuxeo.ecm.core.storage.sql.ra.ConnectionImpl.*$");
    }

    @Override
    public String longMsg() {
      return "Killed handle: org.nuxeo.ecm.core.storage.sql.ra.ConnectionImpl";
    }

    @Override
    public String shortMsg() {
      return "Killed handle: org.nuxeo.ecm.core.storage.sql.ra.ConnectionImpl";
    }
  },
  UNABLE_TO_COMMIT_OR_ROLLBACK(47) {
    @Override
    public Pattern pattern() {
      return Pattern.compile("^.*Unable to commit/rollback.*$");
    }

    @Override
    public String longMsg() {
      return "Unable to commit/rollback";
    }

    @Override
    public String shortMsg() {
      return "Unable to commit/rollback";
    }
  };

  private static final String NOT_IMPLEMENTED = "Should be implemented by enum element";
  private final int id;

  BuiltinLogPattern(int id) {
    this.id = id;
  }

  @Override
  public int id() {
    return id;
  }

  @Override
  public Pattern pattern() {
    throw new UnsupportedOperationException(NOT_IMPLEMENTED);
  }

  @Override
  public String longMsg() {
    throw new UnsupportedOperationException(NOT_IMPLEMENTED);
  }

  @Override
  public String shortMsg() {
    throw new UnsupportedOperationException(NOT_IMPLEMENTED);
  }
}
