package qwatch.logs.command;

import io.vavr.collection.List;
import io.vavr.control.Either;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qwatch.logs.io.JsonImporter;
import qwatch.logs.model.LogEntry;
import qwatch.logs.model.LogSummary;
import qwatch.logs.util.SummaryExtractor;

/**
 * Stats Command.
 *
 * @author Mincong Huang
 * @since 1.0
 */
public class StatsCommand implements Command<List<LogSummary>> {

  private static final Logger logger = LoggerFactory.getLogger(StatsCommand.class);
  public static final String NAME = "stats";

  static final String OPT_LONG_SINCE = "since";
  static final String OPT_LONG_TOP = "top";

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Either<IllegalArgumentException, Builder> parse(String... args) {
    var options = newOptions();
    var parser = new DefaultParser();
    CommandLine cmd;
    try {
      cmd = parser.parse(options, args);
    } catch (ParseException e) {
      return Either.left(
          new IllegalArgumentException("Failed to parse arguments: " + Arrays.toString(args), e));
    }
    var builder = new Builder();

    if (cmd.hasOption(OPT_LONG_SINCE)) {
      var v = cmd.getOptionValue(OPT_LONG_SINCE);
      try {
        var d = LocalDate.parse(v);
        builder.sinceDate(d);
      } catch (DateTimeParseException e) {
        return Either.left(new IllegalArgumentException("Invalid date value: " + v, e));
      }
    }

    if (cmd.hasOption(OPT_LONG_TOP)) {
      var v = cmd.getOptionValue(OPT_LONG_TOP);
      try {
        int topN = Integer.parseInt(v);
        builder.topN(topN);
      } catch (NumberFormatException e) {
        return Either.left(new IllegalArgumentException("Invalid int value: " + v, e));
      }
    }
    return Either.right(builder);
  }

  public static class Builder implements CommandBuilder<StatsCommand> {

    private int topN;
    private LocalDate sinceDate = LocalDate.now().minusDays(14);
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

    /**
     * Sets the since date from which in the statistics is started.
     *
     * <p>By default, the since day is today - 14 days.
     *
     * @param sinceDate since which date the stats should be calculated
     * @return this
     */
    public Builder sinceDate(LocalDate sinceDate) {
      this.sinceDate = sinceDate;
      return this;
    }

    @Override
    public StatsCommand build() {
      return new StatsCommand(this);
    }
  }

  private final int topN;
  private final LocalDate startDate;
  private final Path logDir;

  private StatsCommand(Builder builder) {
    this.topN = builder.topN;
    this.startDate = builder.sinceDate;
    this.logDir = builder.logDir;
  }

  @Override
  public List<LogSummary> execute() {
    // Import log entries
    var tryImport = JsonImporter.importLogEntries(logDir);
    if (tryImport.isFailure()) {
      logger.error("Failed to import JSON files", tryImport.getCause());
      return List.empty();
    }
    var entries = tryImport.get();
    if (entries.nonEmpty()) {
      var dates = entries.map(LogEntry::dateTime).map(ZonedDateTime::toLocalDate).toSortedSet();
      var end = dates.last(); // inclusive
      entries = entries.filter(e -> !e.dateTime().toLocalDate().isBefore(startDate));
      var size = String.format("%,d", entries.size());
      logger.info("{} entries extracted ({} to {}).", size, startDate, end);
    } else {
      logger.info("0 entries extracted.");
    }

    // Summary
    var summaries = new SummaryExtractor(entries).top(topN);
    var detail =
        summaries
            .map(s -> String.format("- %,6d: %s", s.count(), s.description()))
            .collect(Collectors.joining("\n"));
    logger.info("Top {} errors:\n{}", summaries.size(), detail);
    return summaries;
  }

  static Options newOptions() {
    var options = new Options();
    var optSince =
        Option.builder()
            .longOpt(OPT_LONG_SINCE)
            .hasArg()
            .argName("DATE")
            .desc("Since-date in ISO date format (yyyy-MM-dd).")
            .required(false)
            .build();
    var optTop =
        Option.builder()
            .longOpt(OPT_LONG_TOP)
            .hasArg()
            .argName("N")
            .desc("Top N results to display, defaults to all.")
            .required(false)
            .build();
    options.addOption(optSince);
    options.addOption(optTop);
    return options;
  }
}
