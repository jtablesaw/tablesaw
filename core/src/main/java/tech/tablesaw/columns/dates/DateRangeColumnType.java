package tech.tablesaw.columns.dates;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateRangeColumn;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.temporal.DateRange;
import tech.tablesaw.io.ReadOptions;

public class DateRangeColumnType extends AbstractColumnType {

  public static final int BYTE_SIZE = 8;

  public static final DateRangeParser DEFAULT_PARSER = new DateRangeParser(ColumnType.DATE_RANGE);

  private static DateRangeColumnType INSTANCE;

  private DateRangeColumnType(int byteSize, String name, String printerFriendlyName) {
    super(byteSize, name, printerFriendlyName);
  }

  public static DateRangeColumnType instance() {
    if (INSTANCE == null) {
      INSTANCE = new DateRangeColumnType(BYTE_SIZE, "DATE_RANGE", "Date Range");
    }
    return INSTANCE;
  }

  @Override
  public DateRangeColumn create(String name) {
    return DateRangeColumn.create(name);
  }

  @Override
  public AbstractColumnParser<DateRange> customParser(ReadOptions options) {
    return new DateRangeParser(this, options);
  }

  public static int missingValueIndicator() {
    return Integer.MIN_VALUE;
  }

  public static boolean valueIsMissing(int from, int to) {
    return from == missingValueIndicator() && to == missingValueIndicator();
  }
}
