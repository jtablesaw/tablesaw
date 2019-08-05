package tech.tablesaw.aggregate;

import java.time.LocalDateTime;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateTimeColumn;

/** A partial implementation of aggregate functions to summarize over a dateTime column */
public abstract class DateTimeAggregateFunction
    extends AggregateFunction<DateTimeColumn, LocalDateTime> {

  public DateTimeAggregateFunction(String name) {
    super(name);
  }

  public abstract LocalDateTime summarize(DateTimeColumn column);

  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    return type.equals(ColumnType.LOCAL_DATE_TIME);
  }

  @Override
  public ColumnType returnType() {
    return ColumnType.LOCAL_DATE_TIME;
  }
}
