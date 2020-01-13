package tech.tablesaw.columns.strings;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.TextColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.ReadOptions;

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
