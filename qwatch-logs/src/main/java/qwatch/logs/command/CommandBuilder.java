package qwatch.logs.command;

/**
 * Command builder.
 *
 * @param <T> the type of command
 * @author Mincong Huang
 * @since 1.0
 */
@FunctionalInterface
public interface CommandBuilder<T> {
  T build();
}
