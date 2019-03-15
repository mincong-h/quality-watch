package qwatch;

import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.jenkins.command.JenkinsStatsCommand;
import qwatch.logs.command.CollectCommand;
import qwatch.logs.command.StatsCommand;

/**
 * @author Mincong Huang
 * @since 1.0
 */
public class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String... args) {
    if (args.length < 1) {
      logger.warn("qwatch <command>");
      logger.warn("qwatch collect");
      logger.warn("qwatch stats <topN>");
      System.exit(-1);
    }
    var command = args[0];
    if (CollectCommand.NAME.equals(command)) {
      logger.info("Received command '{}'", command);
      CollectCommand.newBuilder() //
          .logDir(Paths.get("/Users/mincong/Downloads"))
          .build()
          .execute();
    } else if (StatsCommand.NAME.equals(command)) {
      logger.info("Received command '{}'", command);
      int n = args.length > 1 ? Integer.parseInt(args[1]) : 200;
      StatsCommand.newBuilder() //
          .logDir(Paths.get("/Users/mincong/datadog"))
          .topN(n)
          .days(14)
          .build()
          .execute();
    } else if (JenkinsStatsCommand.NAME.equals(command)) {
      logger.info("Received command '{}'", command);
      JenkinsStatsCommand.newBuilder() //
          .buildDir(Paths.get("/Users/mincong/jenkins/artifacts/nos-ondemand-mhuang.113"))
          .build()
          .execute();
    } else {
      logger.warn("Unknown command: {}", command);
    }
  }
}
