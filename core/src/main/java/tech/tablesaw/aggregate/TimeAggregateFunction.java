package tech.tablesaw.aggregate;

import java.time.LocalTime;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.TimeColumn;

/** A partial implementation of aggregate functions to summarize over a time column */
public abstract class TimeAggregateFunction extends AggregateFunction<TimeColumn, LocalTime> {

  /**
   * Constructs a TimeAggregateFunction with the given name. The name is used as a column name in
   * the output
   */
  public TimeAggregateFunction(String name) {
    super(name);
  }

  /** Returns a LocalTime object that is the result of applying this function to the given Column */
  public abstract LocalTime summarize(TimeColumn column);

  /** {@inheritDoc} */
  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    return type.equals(ColumnType.LOCAL_TIME);
  }

  /** {@inheritDoc} */
  @Override
  public ColumnType returnType() {
    return ColumnType.LOCAL_TIME;
  }
}
