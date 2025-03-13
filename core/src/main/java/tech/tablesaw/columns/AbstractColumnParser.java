package tech.tablesaw.columns;

import java.util.List;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.io.TypeUtils;

/**
 * A parser for turning strings into objects that can be inserted into a column
 *
 * <p>It serves two purposes, to determine if a string can be parsed into the desired object type,
 * and to actually parse the string.
 *
 * <p>Implementations may take additional parameters such as a locale or DateTimeFormatter.
 *
 * @param <T> The Class of object to be inserted: String for StringColumn, LocalDate for DateColumn,
 *     etc.
 */
public abstract class AbstractColumnParser<T> {

  private final ColumnType columnType;

  protected List<String> missingValueStrings = TypeUtils.MISSING_INDICATORS;

  public AbstractColumnParser(ColumnType columnType) {
    this.columnType = columnType;
  }

  public abstract boolean canParse(String s);

  public abstract T parse(String s);

  public ColumnType columnType() {
    return columnType;
  }

  public boolean isMissing(String s) {
    if (s == null) {
      return true;
    }
    return s.isEmpty() || missingValueStrings.contains(s);
  }

  public byte parseByte(String s) {
    throw new UnsupportedOperationException(
        this.getClass().getSimpleName() + " doesn't support parsing to booleans");
  }

  public int parseInt(String s) {
    throw new UnsupportedOperationException(
        this.getClass().getSimpleName() + " doesn't support parsing to ints");
  }

  public short parseShort(String s) {
    throw new UnsupportedOperationException(
        this.getClass().getSimpleName() + " doesn't support parsing to shorts");
  }

  public long parseLong(String s) {
    throw new UnsupportedOperationException(
        this.getClass().getSimpleName() + " doesn't support parsing to longs");
  }

  public double parseDouble(String s) {
    throw new UnsupportedOperationException(
        this.getClass().getSimpleName() + " doesn't support parsing to doubles");
  }

  public float parseFloat(String s) {
    throw new UnsupportedOperationException(
        this.getClass().getSimpleName() + " doesn't support parsing to floats");
  }

  protected static String remove(final String str, final char remove) {
    if (str == null || str.indexOf(remove) == -1) {
      return str;
    }
    final char[] chars = str.toCharArray();
    int pos = 0;
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] != remove) {
        chars[pos++] = chars[i];
      }
    }
    return new String(chars, 0, pos);
  }

  public void setMissingValueStrings(List<String> missingValueStrings) {
    this.missingValueStrings = missingValueStrings;
  }
}
