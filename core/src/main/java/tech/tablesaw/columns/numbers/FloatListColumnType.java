package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.FloatListColumn;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.ReadOptions;

/** Tablesaw type metadata for a column containing lists of floats. */
public final class FloatListColumnType extends AbstractColumnType {
  private static FloatListColumnType INSTANCE;

  private FloatListColumnType() {
    super(-1, "FLOAT_LIST", "Float List");
  }

  public static FloatListColumnType instance() {
    if (INSTANCE == null) {
      INSTANCE = new FloatListColumnType();
    }
    return INSTANCE;
  }

  @Override
  public Column<?> create(String name) {
    return FloatListColumn.create(name);
  }

  @Override
  public AbstractColumnParser<?> customParser(ReadOptions options) {
    return new FloatListColumn.Parser(this);
  }
}
