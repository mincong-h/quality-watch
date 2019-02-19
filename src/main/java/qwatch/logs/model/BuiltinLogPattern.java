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
      return Pattern.compile("^(.*)JGitInternalException: Invalid ref name(.*)$");
    }

    @Override
    public String longMsg() {
      return "JGitInternalException: Invalid ref name: ${ref}";
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
      return Pattern.compile("^Failed to initialize project (.*)$");
    }

    @Override
    public String longMsg() {
      return "Failed to initialize project ${projectId}";
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
  };

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
    throw new UnsupportedOperationException("Should be implemented by enum element");
  }

  @Override
  public String longMsg() {
    throw new UnsupportedOperationException("Should be implemented by enum element");
  }

  @Override
  public String shortMsg() {
    throw new UnsupportedOperationException("Should be implemented by enum element");
  }
}
