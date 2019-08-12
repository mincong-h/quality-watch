package qwatch;

import java.nio.file.Paths;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.jenkins.command.JenkinsExportCommand;
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
//      int n = args.length > 1 ? Integer.parseInt(args[1]) : 200;
      int n = 200;
      var parsed = StatsCommand.parse(Arrays.copyOfRange(args, 1, args.length));
      if (parsed.isLeft()) {
        logger.error("Failed to parse command '" + StatsCommand.NAME + "'", parsed.getLeft());
      } else {
        parsed
            .get() //
            .logDir(Paths.get("/Users/mincong/datadog"))
            .topN(n)
            .build()
            .execute();
      }
    } else if (JenkinsExportCommand.NAME.equals(command)) {
      logger.info("Received command '{}'", command);
      JenkinsExportCommand.newBuilder()
          .buildDir(Paths.get("/Users/mincong/jenkins/jenkins-artifacts"))
          .exportDir(Paths.get("/Users/mincong/jenkins/jenkins-database"))
          .build()
          .execute();
    } else {
      logger.warn("Unknown command: {}", command);
    }
  }
}
