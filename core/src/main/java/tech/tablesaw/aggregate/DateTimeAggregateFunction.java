package tech.tablesaw.aggregate;

import java.time.LocalDateTime;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateTimeColumn;

/** A partial implementation of aggregate functions to summarize over a dateTime column */
public abstract class DateTimeAggregateFunction
    extends AggregateFunction<DateTimeColumn, LocalDateTime> {

  /**
   * Constructs an DateTimeAggregateFunction with the given name. The name is used as a column name
   * in the output
   */
  public DateTimeAggregateFunction(String name) {
    super(name);
  }

  /** Returns an LocalDateTime that is the result of applying this function to the given column */
  public abstract LocalDateTime summarize(DateTimeColumn column);

  /** {@inheritDoc} */
  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    return type.equals(ColumnType.LOCAL_DATE_TIME);
  }

  /** {@inheritDoc} */
  @Override
  public ColumnType returnType() {
    return ColumnType.LOCAL_DATE_TIME;
  }
}
