package qwatch.logs.util;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import java.util.regex.Pattern;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class LogPatterns {

  static final Pattern P_PROJECT_NOT_FOUND = Pattern.compile("^Project (.*) not found.$");
  static final Pattern P_RESPONSE_COMMITTED =
      Pattern.compile("^(.*)Cannot call sendError\\(\\) after the response has been committed$");
  static final Pattern P_CANNOT_VERIFY_EA =
      Pattern.compile("^Could not verify if early access is enabled for project (.*)$");
  static final Pattern P_FAILED_TO_PARSE_REGISTRY =
      Pattern.compile("^Failed to parse registry from (.*)$");
  static final Pattern P_IO_EXCEPTION_ON_REQ_URL =
      Pattern.compile("^java.io.IOException: On requestURL:(.*)$");
  static final Pattern P_NO_SUCH_PROJECT = Pattern.compile("^No such project (.*)$");
  static final Pattern P_INCORRECT_VERSION_FILE =
      Pattern.compile("^The version file should be created for the branch (.*)$");
  static final Pattern P_SSO_AUTH_FAILED =
      Pattern.compile("^Authentication has failed. Credentials may be incorrect (.*)");
  static final Pattern P_UNHANDLED_ERROR =
      Pattern.compile("^(.*)Unhandled error was caught by the Filter$");
  static final Pattern P_FEATURE_VALIDATION_FAILED =
      Pattern.compile("^Validation failed for feature '(.*)'$");
  static final Pattern P_ERR_CREATING_MANAGED_CONNECTION =
      Pattern.compile("^Error occurred creating ManagedConnection for handle: (.*)$");
  static final Pattern P_SERVICE_TICKET_MISMATCHED =
      Pattern.compile(
          "^Service ticket \\[(.*)] with service \\[(.*)] does not match supplied service \\[(.*)]$");

  /* ----- JGit ----- */
  static final Pattern P_INVALID_REF_NAME =
      Pattern.compile("^(.*)JGitInternalException: Invalid ref name(.*)$");
  static final Pattern P_INTERNAL_ERR_RECEIVE_PACK =
      Pattern.compile("^Internal error during receive-pack to (.*)$");
  static final Pattern P_INTERNAL_ERR_UPLOAD_PACK =
      Pattern.compile("^Internal error during upload-pack from (.*)$");
  static final Pattern P_JGIT_PACK_FILE =
      Pattern.compile("^(.*)Exception caught while accessing pack file (.*)$");
  static final Pattern P_FAILED_TO_CLONE_REPO =
      Pattern.compile("^Failed to clone remote repository for project: (.*)$");

  /* ----- Marketplace ----- */
  static final Pattern P_MKP_LOGIN_SERVICE_NOT_FOUND_FOR_PKG =
      Pattern.compile("^Service @login not found for object: (.*) of type pkg$");

  private static final List<Tuple2<String, Pattern>> PATTERNS;

  static {
    List<Tuple2<String, Pattern>> list = List.empty();
    String s;

    s = "Project ${id} not found";
    list = list.append(Tuple.of(s, P_PROJECT_NOT_FOUND));

    s = "Cannot call sendError() after the response has been committed";
    list = list.append(Tuple.of(s, P_RESPONSE_COMMITTED));

    s = "Could not verify if early access is enabled for project ${id}";
    list = list.append(Tuple.of(s, P_CANNOT_VERIFY_EA));

    s = "Failed to parse registry from ${json}";
    list = list.append(Tuple.of(s, P_FAILED_TO_PARSE_REGISTRY));

    s = "java.io.IOException: On requestURL: ${url}";
    list = list.append(Tuple.of(s, P_IO_EXCEPTION_ON_REQ_URL));

    s = "No such project ${id}";
    list = list.append(Tuple.of(s, P_NO_SUCH_PROJECT));

    s = "The version file should be created for the branch ${name}...";
    list = list.append(Tuple.of(s, P_INCORRECT_VERSION_FILE));

    s = "Authentication has failed. Credentials may be incorrect ...";
    list = list.append(Tuple.of(s, P_SSO_AUTH_FAILED));

    s = "Unhandled error was caught by the Filter";
    list = list.append(Tuple.of(s, P_UNHANDLED_ERROR));

    s = "Validation failed for feature ${id}";
    list = list.append(Tuple.of(s, P_FEATURE_VALIDATION_FAILED));

    s = "Error occurred creating ManagedConnection for handle: ${info}";
    list = list.append(Tuple.of(s, P_ERR_CREATING_MANAGED_CONNECTION));

    s =
        "Service ticket [${ticket}] with service [${service}] does not match supplied service [${suppliedService}]";
    list = list.append(Tuple.of(s, P_SERVICE_TICKET_MISMATCHED));

    // JGit
    s = "JGitInternalException: Invalid ref name: ${ref}";
    list = list.append(Tuple.of(s, P_INVALID_REF_NAME));
    s = "Exception caught while accessing pack file ${pack}";
    list = list.append(Tuple.of(s, P_JGIT_PACK_FILE));
    s = "Internal error during receive-pack to ${gitPath}";
    list = list.append(Tuple.of(s, P_INTERNAL_ERR_RECEIVE_PACK));
    s = "Internal error during upload-pack from ${gitPath}";
    list = list.append(Tuple.of(s, P_INTERNAL_ERR_UPLOAD_PACK));
    s = "Failed to clone remote repository for project: ${id}";
    list = list.append(Tuple.of(s, P_FAILED_TO_CLONE_REPO));

    // Marketplace
    s = "Service @login not found for object: ${pkgPath} of type pkg";
    list = list.append(Tuple.of(s, P_MKP_LOGIN_SERVICE_NOT_FOUND_FOR_PKG));

    PATTERNS = list;
  }

  /**
   * Creates an abbreviation for a full message.
   *
   * @param fullMessage full message
   * @return abbreviation
   */
  public static String createSummary(String fullMessage) {
    String head = head(fullMessage);
    for (Tuple2<String, Pattern> pattern : PATTERNS) {
      if (pattern._2.matcher(head).matches()) {
        return pattern._1;
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
