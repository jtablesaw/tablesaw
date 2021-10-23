package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.ReadOptions;

/** The {@link ColumnType} for {@link IntColumn} */
public class IntColumnType extends AbstractColumnType {

  /** The default parser for IntColumn */
  public static final IntParser DEFAULT_PARSER = new IntParser(ColumnType.INTEGER);

  private static final int BYTE_SIZE = 4;

  private static IntColumnType INSTANCE;

  private IntColumnType(int byteSize, String name, String printerFriendlyName) {
    super(byteSize, name, printerFriendlyName);
  }

  /** Returns the singleton instance of IntColumnType */
  public static IntColumnType instance() {
    if (INSTANCE == null) {
      INSTANCE = new IntColumnType(BYTE_SIZE, "INTEGER", "Integer");
    }
    return INSTANCE;
  }

  /** {@inheritDoc} */
  @Override
  public IntColumn create(String name) {
    return IntColumn.create(name);
  }

  /** {@inheritDoc} */
  @Override
  public IntParser customParser(ReadOptions options) {
    return new IntParser(this, options);
  }

  /** Returns true if the given value is the missing value indicator for this column type */
  public static boolean valueIsMissing(int value) {
    return value == missingValueIndicator();
  }

  /** Returns the missing value indicator for this column type NOTE: */
  public static int missingValueIndicator() {
    return Integer.MIN_VALUE;
  }
}
