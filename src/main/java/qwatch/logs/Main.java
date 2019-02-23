package qwatch.logs;

import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.command.CollectCommand;
import qwatch.logs.command.StatsCommand;

public class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String... args) {
    if (args.length < 1) {
      logger.warn("qwatch <command>");
      logger.warn("qwatch collect");
      logger.warn("qwatch stats <topN>");
      System.exit(-1);
    }
    String command = args[0];
    if (CollectCommand.NAME.equals(command)) {
      CollectCommand.newBuilder() //
          .logDir(Paths.get("/Users/mincong/Downloads"))
          .build()
          .execute();
    } else if (StatsCommand.NAME.equals(command)) {
      int n = args.length > 1 ? Integer.parseInt(args[1]) : 200;
      StatsCommand.newBuilder() //
          .logDir(Paths.get("/Users/mincong/datadog"))
          .topN(n)
          .days(14)
          .build()
          .execute();
    } else {
      logger.warn("Unknown command: {}", command);
    }
  }
}
