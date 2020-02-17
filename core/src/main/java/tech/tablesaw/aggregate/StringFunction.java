package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.StringColumn;

/** A partial implementation of aggregate functions to summarize over a date column */
public abstract class StringFunction extends AggregateFunction<StringColumn, String> {

  public StringFunction(String name) {
    super(name);
  }

  public abstract String summarize(StringColumn column);

  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    return type.equals(ColumnType.STRING);
  }

  @Override
  public ColumnType returnType() {
    return ColumnType.STRING;
  }
}
