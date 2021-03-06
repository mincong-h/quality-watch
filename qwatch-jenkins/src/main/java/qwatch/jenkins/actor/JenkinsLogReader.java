package qwatch.jenkins.actor;

import io.vavr.Tuple;
import io.vavr.Tuple3;
import io.vavr.collection.List;
import io.vavr.control.Either;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.jenkins.model.RawLog;
import qwatch.jenkins.model.maven.MavenLog;
import qwatch.jenkins.model.maven.MavenLog.Level;

import static java.util.stream.Collectors.joining;

/**
 * Jenkins log reader reads console log generated by Jenkins.
 *
 * @author Mincong Huang
 * @since 1.0
 */
public class JenkinsLogReader {

  private static final Logger logger = LoggerFactory.getLogger(JenkinsLogReader.class);

  private static final String MVN_START_PREFIX = "Executing Maven:";
  private static final String MVN_END_PREFIX = "[INFO] Final Memory:";

  /**
   * Reads Jenkins logs
   *
   * @param logFile the filepath of Jenkins logs
   * @return either a failure message, or a tuple of logs before / during / after Maven processing
   */
  public static Either<String, Tuple3<List<RawLog>, List<MavenLog>, List<RawLog>>> read(
      Path logFile) {
    java.util.List<String> lines;
    try {
      lines = Files.readAllLines(logFile);
    } catch (IOException e) {
      return Either.left("Failed to read log file: " + logFile.toAbsolutePath());
    }

    // Preparation
    var rawLogs = new ArrayList<RawLog>(lines.size());
    for (var line : lines) {
      if (hasTimePrefix(line)) {
        rawLogs.add(RawLog.parseTrusted(line));
      } else {
        logger.warn("Failed to understand line: {}", line);
      }
    }

    // Before Maven logs: skip
    var it = rawLogs.iterator();
    var isMaven = false;
    List<RawLog> beforeLogs = List.of();
    while (it.hasNext() && !isMaven) {
      var log = it.next();
      if (log.message().startsWith(MVN_START_PREFIX)) {
        isMaven = true;
      } else {
        beforeLogs = beforeLogs.append(log);
      }
    }

    // During Maven logs: process
    var logs = new java.util.LinkedList<MavenLog>();
    while (it.hasNext() && isMaven) {
      var rawLog = it.next();
      if (hasLevel(rawLog.message())) {
        if (rawLog.message().startsWith(MVN_END_PREFIX)) {
          isMaven = false;
          logs.add(MavenLog.parseTrusted(rawLog));
          if (it.hasNext()) {
            logs.add(MavenLog.parseTrusted(it.next()));
          }
        } else {
          logs.add(MavenLog.parseTrusted(rawLog));
        }
      } else {
        var last = logs.pollLast();
        logs.add(last.extendMsg(rawLog.message()));
      }
    }
    List<MavenLog> mavenLogs = List.ofAll(logs);

    List<RawLog> afterLogs = List.of();
    while (it.hasNext()) {
      afterLogs = afterLogs.append(it.next());
    }
    var mavenSummary =
        List.ofAll(logs)
            .groupBy(MavenLog::level)
            .map(t -> String.format("%s: %,d", t._1.representation(), t._2.size()))
            .collect(joining(", "));
    logger.info("Before: {} lines", beforeLogs.size());
    logger.info("Maven:  {}", mavenSummary);
    logger.info("After:  {} lines", afterLogs.size());
    return Either.right(Tuple.of(beforeLogs, mavenLogs, afterLogs));
  }

  private static boolean hasLevel(String line) {
    for (var level : Level.values()) {
      if (line.startsWith(level.marker())) {
        return true;
      }
    }
    return false;
  }

  private static boolean hasTimePrefix(String line) {
    if (line.length() >= 8) {
      var h = Character.isDigit(line.charAt(0)) && Character.isDigit(line.charAt(1));
      var m = Character.isDigit(line.charAt(3)) && Character.isDigit(line.charAt(4));
      var s = Character.isDigit(line.charAt(6)) && Character.isDigit(line.charAt(7));
      var p = line.charAt(2) == ':' && line.charAt(5) == ':';
      return h && m && s && p;
    } else {
      return false;
    }
  }

  public static void main(String[] args) {
    JenkinsLogReader.read(
        Paths.get("/Users/mincong/jenkins/jenkins-artifacts/nos-master.277/jenkins.log"));
  }
}
