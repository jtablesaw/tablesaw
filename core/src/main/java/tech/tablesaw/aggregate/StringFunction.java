package tech.tablesaw.aggregate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.StringColumn;

/**
 * A partial implementation of aggregate functions to summarize over a StringColumn TODO: Consider
 * renaming to StringAggregateFunction for consistency
 */
public abstract class StringFunction extends AggregateFunction<StringColumn, String> {

  /**
   * Constructs an {@code StringFunction} with the given name. The name may be used to name a column
   * in the output when this function is used by {@link Summarizer}
   */
  public StringFunction(String name) {
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
