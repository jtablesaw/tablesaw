package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.StringColumn;

/**
 * A partial implementation of aggregate functions to summarize over a StringColumn and return a
 * String
 */
public abstract class StringAggregateFunction extends AggregateFunction<StringColumn, String> {

  /**
   * Constructs an {@code StringFunction} with the given name. The name may be used to name a column
   * in the output when this function is used by {@link Summarizer}
   */
  public StringAggregateFunction(String name) {
    super(name);
  }

  public abstract String summarize(StringColumn column);

  /** {@inheritDoc} */
  @Override
  public boolean isCompatibleColumn(ColumnType type) {
    return type.equals(ColumnType.STRING);
  }

  /** {@inheritDoc} */
  @Override
  public ColumnType returnType() {
    return ColumnType.STRING;
  }
}
