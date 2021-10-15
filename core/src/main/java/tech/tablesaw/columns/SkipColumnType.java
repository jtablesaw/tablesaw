package tech.tablesaw.columns;

import tech.tablesaw.io.ReadOptions;

/**
 * A special {@link tech.tablesaw.api.ColumnType} that can be used in a ColumnType[] for table
 * imports to instruct the system to skip (i.e. don't import) a column
 */
public class SkipColumnType extends AbstractColumnType {

  private static SkipColumnType INSTANCE;

  private SkipColumnType(int byteSize, String name, String printerFriendlyName) {
    super(byteSize, name, printerFriendlyName);
  }

  /** Returns the singleton instance of this class */
  public static SkipColumnType instance() {
    if (INSTANCE == null) {
      INSTANCE = new SkipColumnType(0, "SKIP", "Skipped");
    }
    return INSTANCE;
  }

  /** {@inheritDoc} */
  @Override
  public Column<Void> create(String name) {
    throw new UnsupportedOperationException(
        "Column type " + name() + " doesn't support column creation");
  }

  /** {@inheritDoc} */
  @Override
  public AbstractColumnParser<?> customParser(ReadOptions options) {
    throw new UnsupportedOperationException("Column type " + name() + " doesn't support parsing");
  }

  /** This method is not supported. It will always throw {@link UnsupportedOperationException} */
  public static Object missingValueIndicator() throws UnsupportedOperationException {
    throw new UnsupportedOperationException(
        "Column type " + SKIP + " doesn't support missing values");
  }
}
