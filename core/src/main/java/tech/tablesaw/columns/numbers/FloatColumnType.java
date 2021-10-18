package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.ReadOptions;

/** The {@link ColumnType} for {@link FloatColumn} */
public class FloatColumnType extends AbstractColumnType {

  public static final int BYTE_SIZE = 4;

  /** Returns the default parser for {@link FloatColumn} */
  public static final FloatParser DEFAULT_PARSER = new FloatParser(ColumnType.FLOAT);

  private static FloatColumnType INSTANCE;

  private FloatColumnType(int byteSize, String name, String printerFriendlyName) {
    super(byteSize, name, printerFriendlyName);
  }

  /** Returns the singleton instance of FloatColumnType */
  public static FloatColumnType instance() {
    if (INSTANCE == null) {
      INSTANCE = new FloatColumnType(BYTE_SIZE, "FLOAT", "float");
    }
    return INSTANCE;
  }

  /** {@inheritDoc} */
  @Override
  public FloatColumn create(String name) {
    return FloatColumn.create(name);
  }

  /** {@inheritDoc} */
  @Override
  public FloatParser customParser(ReadOptions options) {
    return new FloatParser(this, options);
  }

  /** Returns true if the given value is the missing value indicator for this column type */
  public static boolean valueIsMissing(float value) {
    return Float.isNaN(value);
  }

  /** Returns the missing value indicator for this column type */
  public static float missingValueIndicator() {
    return Float.NaN;
  }
}
