package tech.tablesaw.columns.temporal;

import java.time.temporal.Temporal;
import tech.tablesaw.columns.Column;

/**
 * An interface for columns of temporal values backed by longs, e.g. DateTimeColumn and
 * InstantColumn
 *
 * @param <T>
 */
public interface TemporalColumn<T extends Temporal> extends Column<T> {

  /**
   * Returns a value of the Object type stored in the column (e.g. a LocalDateTime) at the given row
   */
  T get(int r);

  /** Returns the internal (long-encoded) value stored in the column at the given row */
  long getLongInternal(int r);

  /** Returns this column with the argument appended at the bottom */
  TemporalColumn<T> appendInternal(long value);
}
