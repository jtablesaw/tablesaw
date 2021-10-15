package tech.tablesaw.columns.strings;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.TextColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.ReadOptions;

/**
 * A ColumnType for columns that holds String values.
 *
 * <p>It is optimized for situations where the values in the column rarely if ever repeat, so the
 * dictionary encoding performed by StringColumn would be detrimental to memory usage.
 *
 * <p>See also: {@link tech.tablesaw.api.StringColumn}
 */
public class TextColumnType extends AbstractColumnType {

  public static final int BYTE_SIZE = 4;
  public static final StringParser DEFAULT_PARSER = new StringParser(ColumnType.STRING);

  private static TextColumnType INSTANCE;

  private TextColumnType(int byteSize, String name, String printerFriendlyName) {
    super(byteSize, name, printerFriendlyName);
  }

  public static TextColumnType instance() {
    if (INSTANCE == null) {
      INSTANCE = new TextColumnType(BYTE_SIZE, "TEXT", "Text");
    }
    return INSTANCE;
  }

  public static boolean valueIsMissing(String string) {
    return missingValueIndicator().equals(string);
  }

  @Override
  public TextColumn create(String name) {
    return TextColumn.create(name);
  }

  @Override
  public StringParser customParser(ReadOptions options) {
    return new StringParser(this, options);
  }

  public static String missingValueIndicator() {
    return "";
  }
}
