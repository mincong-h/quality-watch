package qwatch.logs.command;

import io.vavr.collection.Set;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.model.LogEntry;
import qwatch.logs.util.JsonImportUtil;
import qwatch.logs.util.SummaryExtractor;

/**
 * Stats Command.
 *
 * @author Mincong Huang
 * @since 1.0
 */
public class StatsCommand implements Command<Void> {

  private static final Logger logger = LoggerFactory.getLogger(StatsCommand.class);
  public static final String NAME = "stats";

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder implements CommandBuilder<StatsCommand> {

    private int topN;
    private Path logDir;

    /**
     * Sets the top N exceptions to display in the statistics.
     *
     * <p>Top N means the most frequently occurred exceptions.
     *
     * @param topN top N results to display
     * @return this
     */
    public Builder topN(int topN) {
      this.topN = topN;
      return this;
    }

    /**
     * Sets the log directory.
     *
     * @param logDir log directory
     * @return this
     */
    public Builder logDir(Path logDir) {
      this.logDir = logDir;
      return this;
    }

    @Override
    public StatsCommand build() {
      return new StatsCommand(this);
    }
  }

  private final int topN;
  private final Path logDir;

  private StatsCommand(Builder builder) {
    this.topN = builder.topN;
    this.logDir = builder.logDir;
  }

  @Override
  public Void execute() {
    Set<LogEntry> entries = JsonImportUtil.importLogEntries(logDir);
    logger.info("{} entries extracted.", entries.size());

    String summary = new SummaryExtractor(entries).top(topN);
    logger.info("{}", summary);
    return null;
  }
}
