package tech.tablesaw.columns.strings;

import tech.tablesaw.api.StringListColumn;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.ReadOptions;

/** Tablesaw type metadata for a column containing lists of strings. */
public final class StringListColumnType extends AbstractColumnType {
  private static StringListColumnType INSTANCE;

  private StringListColumnType() {
    super(-1, "STRING_LIST", "String List");
  }

  public static StringListColumnType instance() {
    if (INSTANCE == null) {
      INSTANCE = new StringListColumnType();
    }
    return INSTANCE;
  }

  @Override
  public Column<?> create(String name) {
    return StringListColumn.create(name);
  }

  @Override
  public AbstractColumnParser<?> customParser(ReadOptions options) {
    return new StringListColumn.Parser(this);
  }
}
