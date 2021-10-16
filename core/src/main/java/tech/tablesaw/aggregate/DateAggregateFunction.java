package tech.tablesaw.aggregate;

import java.time.LocalDate;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;

/** A partial implementation of aggregate functions to summarize over a date column */
public abstract class DateAggregateFunction extends AggregateFunction<DateColumn, LocalDate> {

  /**
   * Constructs a DateAggregateFunction with the given name. The name may be used to name a column
   * in the output when this function is used by {@link Summarizer}
   */
  public DateAggregateFunction(String name) {
    super(name);
  }

  /**
   * Returns an instance of LocalDate that is the result of applying this function to the given
   * column
   */
  public abstract LocalDate summarize(DateColumn column);

  /** {@inheritDoc} */
  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    return type.equals(ColumnType.LOCAL_DATE);
  }

  /** {@inheritDoc} */
  @Override
  public ColumnType returnType() {
    return ColumnType.LOCAL_DATE;
  }
}
