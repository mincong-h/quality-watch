package qwatch.logs.command;

/**
 * Command pattern.
 *
 * @param <T> the return type of the result
 * @author Mincong Huang
 * @since 1.0
 */
@FunctionalInterface
public interface Command<T> {
  T execute();
}
