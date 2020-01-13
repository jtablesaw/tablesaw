package tech.tablesaw.columns.strings;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.ReadOptions;

public class StringColumnType extends AbstractColumnType {

  public static final int BYTE_SIZE = 4;
  public static final StringParser DEFAULT_PARSER = new StringParser(ColumnType.STRING);

  private static StringColumnType INSTANCE;

  private StringColumnType(int byteSize, String name, String printerFriendlyName) {
    super(byteSize, name, printerFriendlyName);
  }

  public static StringColumnType instance() {
    if (INSTANCE == null) {
      INSTANCE = new StringColumnType(BYTE_SIZE, "STRING", "String");
    }
    return INSTANCE;
  }

  public static boolean valueIsMissing(String string) {
    return missingValueIndicator().equals(string);
  }

  @Override
  public StringColumn create(String name) {
    return StringColumn.create(name);
  }

  @Override
  public StringParser customParser(ReadOptions options) {
    return new StringParser(this, options);
  }

  public static String missingValueIndicator() {
    return "";
  }
}
