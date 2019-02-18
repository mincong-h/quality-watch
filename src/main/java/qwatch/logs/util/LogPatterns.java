package qwatch.logs.util;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import java.util.regex.Pattern;
import qwatch.logs.model.LogPattern;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class LogPatterns {

  static final LogPattern PROJECT_NOT_FOUND =
      LogPattern.newBuilder()
          .id(1)
          .shortMsg("Project ${id} not found")
          .longMsg("Project ${id} not found")
          .pattern(Pattern.compile("^Project (.*) not found.$"))
          .build();

  static final LogPattern RESPONSE_COMMITTED =
      LogPattern.newBuilder()
          .id(2)
          .shortMsg("Response has been committed")
          .longMsg("Cannot call sendError() after the response has been committed")
          .pattern(
              Pattern.compile(
                  "^(.*)Cannot call sendError\\(\\) after the response has been committed$"))
          .build();

  static final LogPattern CANNOT_VERIFY_EA =
      LogPattern.newBuilder()
          .id(3)
          .shortMsg("Cannot verify early access")
          .longMsg("Could not verify if early access is enabled for project ${id}")
          .pattern(
              Pattern.compile("^Could not verify if early access is enabled for project (.*)$"))
          .build();

  static final LogPattern FAILED_TO_PARSE_REGISTRY =
      LogPattern.newBuilder()
          .id(4)
          .shortMsg("Failed to parse registry")
          .longMsg("Failed to parse registry from ${json}")
          .pattern(Pattern.compile("^Failed to parse registry from (.*)$"))
          .build();

  static final LogPattern IO_EXCEPTION_ON_REQ_URL =
      LogPattern.newBuilder()
          .id(5)
          .shortMsg("IOException on requestURL")
          .longMsg("java.io.IOException: On requestURL: ${url}")
          .pattern(Pattern.compile("^java.io.IOException: On requestURL:(.*)$"))
          .build();

  static final LogPattern NO_SUCH_PROJECT =
      LogPattern.newBuilder()
          .id(6)
          .shortMsg("No such project")
          .longMsg("No such project ${id}")
          .pattern(Pattern.compile("^No such project (.*)$"))
          .build();

  static final LogPattern INCORRECT_VERSION_FILE =
      LogPattern.newBuilder()
          .id(7)
          .shortMsg("Incorrect version file")
          .longMsg("The version file should be created for the branch ${name}")
          .pattern(Pattern.compile("^The version file should be created for the branch (.*)$"))
          .build();

  static final LogPattern SSO_AUTH_FAILED =
      LogPattern.newBuilder()
          .id(8)
          .shortMsg("Authentication failed")
          .longMsg("Authentication has failed. Credentials may be incorrect")
          .pattern(Pattern.compile("^Authentication has failed. Credentials may be incorrect (.*)"))
          .build();

  static final LogPattern UNHANDLED_ERROR =
      LogPattern.newBuilder()
          .id(9)
          .shortMsg("Unhandled error")
          .longMsg("Unhandled error was caught by the Filter")
          .pattern(Pattern.compile("^(.*)Unhandled error was caught by the Filter$"))
          .build();

  static final LogPattern FEATURE_VALIDATION_FAILED =
      LogPattern.newBuilder()
          .id(10)
          .shortMsg("Validation failed for feature")
          .longMsg("Validation failed for feature ${id}")
          .pattern(Pattern.compile("^Validation failed for feature '(.*)'$"))
          .build();

  static final LogPattern ERR_CREATING_MANAGED_CONNECTION =
      LogPattern.newBuilder()
          .id(11)
          .shortMsg("Error occurred creating ManagedConnection")
          .longMsg("Error occurred creating ManagedConnection for handle: ${info}")
          .pattern(Pattern.compile("^Error occurred creating ManagedConnection for handle: (.*)$"))
          .build();

  static final LogPattern SERVICE_TICKET_MISMATCHED =
      LogPattern.newBuilder()
          .id(12)
          .shortMsg("Service ticket mismatched")
          .longMsg(
              "Service ticket ${ticket} with service ${service} does not match supplied service ${suppliedService}")
          .pattern(
              Pattern.compile(
                  "^Service ticket \\[(.*)] with service \\[(.*)] does not match supplied service \\[(.*)]$"))
          .build();

  static final LogPattern STUDIO_PROJECT_NOT_FOUND_FOR_CONNECT =
      LogPattern.newBuilder()
          .id(13)
          .shortMsg("StudioProject for ConnectProject")
          .longMsg("Couldn't find StudioProject for ConnectProject with id ${projectId}")
          .pattern(Pattern.compile("^Couldn't find StudioProject for ConnectProject with id (.*)$"))
          .build();

  static final LogPattern INVALID_REF_NAME =
      LogPattern.newBuilder()
          .id(14)
          .shortMsg("Invalid ref name")
          .longMsg("JGitInternalException: Invalid ref name: ${ref}")
          .pattern(Pattern.compile("^(.*)JGitInternalException: Invalid ref name(.*)$"))
          .build();

  static final LogPattern INTERNAL_ERR_RECEIVE_PACK =
      LogPattern.newBuilder()
          .id(15)
          .shortMsg("Receive-pack error")
          .longMsg("Internal error during receive-pack to ${gitPath}")
          .pattern(Pattern.compile("^Internal error during receive-pack to (.*)$"))
          .build();

  static final LogPattern INTERNAL_ERR_UPLOAD_PACK =
      LogPattern.newBuilder()
          .id(16)
          .shortMsg("Upload-pack error")
          .longMsg("Internal error during upload-pack from ${gitPath}")
          .pattern(Pattern.compile("^Internal error during upload-pack from (.*)$"))
          .build();

  static final LogPattern JGIT_PACK_FILE =
      LogPattern.newBuilder()
          .id(17)
          .shortMsg("Cannot access pack file")
          .longMsg("Exception caught while accessing pack file ${pack}")
          .pattern(Pattern.compile("^(.*)Exception caught while accessing pack file (.*)$"))
          .build();

  static final LogPattern FAILED_TO_CLONE_REPO =
      LogPattern.newBuilder()
          .id(18)
          .shortMsg("Failed to clone repository")
          .longMsg("Failed to clone remote repository for project: ${id}")
          .pattern(Pattern.compile("^Failed to clone remote repository for project: (.*)$"))
          .build();

  static final LogPattern LOGIN_SERVICE_NOT_FOUND =
      LogPattern.newBuilder()
          .id(19)
          .shortMsg("Service @login not found")
          .longMsg("Service @login not found for object: ${pkgPath} of type pkg")
          .pattern(Pattern.compile("^Service @login not found for object: (.*) of type pkg$"))
          .build();

  static final LogPattern RESET_ON_HEAD_FAILED =
      LogPattern.newBuilder()
          .id(20)
          .shortMsg("ResetOnHead failed")
          .longMsg("Failed to resetOnHead: ${gitPath}")
          .pattern(Pattern.compile("^Failed to resetOnHead: (.*)$"))
          .build();

  static final LogPattern FAILED_INIT_PROJECT =
      LogPattern.newBuilder()
          .id(21)
          .shortMsg("Failed to initialize project")
          .longMsg("Failed to initialize project ${projectId}")
          .pattern(Pattern.compile("^Failed to initialize project (.*)$"))
          .build();

  private static final Set<LogPattern> PATTERNS =
      HashSet.of(PROJECT_NOT_FOUND)
          .add(RESPONSE_COMMITTED)
          .add(CANNOT_VERIFY_EA)
          .add(FAILED_TO_PARSE_REGISTRY)
          .add(IO_EXCEPTION_ON_REQ_URL)
          .add(NO_SUCH_PROJECT)
          .add(INCORRECT_VERSION_FILE)
          .add(SSO_AUTH_FAILED)
          .add(UNHANDLED_ERROR)
          .add(FEATURE_VALIDATION_FAILED)
          .add(ERR_CREATING_MANAGED_CONNECTION)
          .add(SERVICE_TICKET_MISMATCHED)
          .add(STUDIO_PROJECT_NOT_FOUND_FOR_CONNECT)
          .add(INVALID_REF_NAME)
          .add(INTERNAL_ERR_RECEIVE_PACK)
          .add(INTERNAL_ERR_UPLOAD_PACK)
          .add(JGIT_PACK_FILE)
          .add(FAILED_TO_CLONE_REPO)
          .add(LOGIN_SERVICE_NOT_FOUND)
          .add(RESET_ON_HEAD_FAILED)
          .add(FAILED_INIT_PROJECT);

  /**
   * Creates an abbreviation for a full message.
   *
   * @param fullMessage full message
   * @return abbreviation
   */
  public static String createSummary(String fullMessage) {
    String head = head(fullMessage);
    for (LogPattern pattern : PATTERNS) {
      if (pattern.matches(head)) {
        return pattern.longMsg();
      }
    }
    return head;
  }

  static String head(String message) {
    int r = message.indexOf('\r');
    if (r > 0) {
      return message.substring(0, r);
    }
    int n = message.indexOf('\n');
    if (n > 0) {
      return message.substring(0, n);
    }
    return message;
  }

  private LogPatterns() {
    // Utility class, do not instantiate
  }
}
