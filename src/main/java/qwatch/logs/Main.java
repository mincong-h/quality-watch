package qwatch.logs;

import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.command.CollectCommand;

public class Main {

  //  private static final int N = 10;
  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String... args) {
    if (args.length < 1) {
      logger.warn("qwatch <command>");
      logger.warn("qwatch collect");
      System.exit(-1);
    }
    String command = args[0];
    if (CollectCommand.NAME.equals(command)) {
      CollectCommand.newBuilder() //
          .logDir(Paths.get("/Users/mincong/Downloads"))
          .build()
          .execute();
    } else {
      logger.warn("Unknown command: {}", command);
    }
    //    Either<String, List<LogEntry>> either = CsvImporter.importLogEntries(Paths.get(path));
    //    if (either.isLeft()) {
    //      logger.error("{}", either.getLeft());
    //      return;
    //    }
    //    List<LogEntry> entries = either.get();
    //    logger.info("{} entries extracted.", entries.size());
    //    logger.info("Top {} errors:\n{}", N, new SummaryExtractor(entries).top(N));
  }
}
