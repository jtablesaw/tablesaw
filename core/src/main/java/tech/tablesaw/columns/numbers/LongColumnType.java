package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.ReadOptions;

/** The {@link ColumnType} for {@link LongColumn} */
public class LongColumnType extends AbstractColumnType {

  /** The default parser for LongColumn */
  public static final LongParser DEFAULT_PARSER = new LongParser(ColumnType.LONG);

  private static final int BYTE_SIZE = 8;

  private static LongColumnType INSTANCE;

  private LongColumnType(int byteSize, String name, String printerFriendlyName) {
    super(byteSize, name, printerFriendlyName);
  }

  /** Returns the singleton instance of LongColumnType */
  public static LongColumnType instance() {
    if (INSTANCE == null) {
      INSTANCE = new LongColumnType(BYTE_SIZE, "LONG", "Long");
    }
    return INSTANCE;
  }

  /** {@inheritDoc} */
  @Override
  public LongColumn create(String name) {
    return LongColumn.create(name);
  }

  /** Returns the default parser used to convert strings to long values */
  public LongParser defaultParser() {
    return DEFAULT_PARSER;
  }

  /** {@inheritDoc} */
  @Override
  public LongParser customParser(ReadOptions options) {
    return new LongParser(this, options);
  }

  /** Returns true if the given value is the missing value indicator for this column type */
  public static boolean valueIsMissing(long value) {
    return value == missingValueIndicator();
  }

  /** Returns the missing value indicator for this column type NOTE: */
  public static long missingValueIndicator() {
    return Long.MIN_VALUE;
  }
}
