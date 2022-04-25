package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.ReadOptions;

/** The {@link ColumnType} for {@link DoubleColumn} */
public class DoubleColumnType extends AbstractColumnType {

  private static final int BYTE_SIZE = 8;

  /** Returns the default parser for DoubleColumn */
  public static final DoubleParser DEFAULT_PARSER = new DoubleParser(ColumnType.DOUBLE);

  private static DoubleColumnType INSTANCE = new DoubleColumnType(BYTE_SIZE, "DOUBLE", "Double");

  /** Returns the singleton instance of DoubleColumnType */
  public static DoubleColumnType instance() {
    if (INSTANCE == null) {
      INSTANCE = new DoubleColumnType(BYTE_SIZE, "DOUBLE", "Double");
    }
    return INSTANCE;
  }

  private DoubleColumnType(int byteSize, String name, String printerFriendlyName) {
    super(byteSize, name, printerFriendlyName);
  }

  /** {@inheritDoc} */
  @Override
  public DoubleColumn create(String name) {
    return DoubleColumn.create(name);
  }

  /** {@inheritDoc} */
  @Override
  public DoubleParser customParser(ReadOptions options) {
    return new DoubleParser(this, options);
  }

  /** Returns true if the given value is the missing value indicator for this column type */
  public static boolean valueIsMissing(double value) {
    return Double.isNaN(value);
  }

  /**
   * Returns the missing value indicator for this column type NOTE: Clients should use {@link
   * DoubleColumnType#valueIsMissing(double)} to test for missing value indicators
   */
  public static double missingValueIndicator() {
    return Double.NaN;
  }
}
