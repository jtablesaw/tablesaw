package tech.tablesaw.columns.times;

import java.time.LocalTime;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.ReadOptions;

public class TimeColumnType extends AbstractColumnType {

  public static final int BYTE_SIZE = 4;

  public static final TimeParser DEFAULT_PARSER = new TimeParser(ColumnType.LOCAL_TIME);

  private static TimeColumnType INSTANCE;

  private TimeColumnType(int byteSize, String name, String printerFriendlyName) {
    super(byteSize, name, printerFriendlyName);
  }

  public static TimeColumnType instance() {
    if (INSTANCE == null) {
      INSTANCE = new TimeColumnType(BYTE_SIZE, "LOCAL_TIME", "Time");
    }
    return INSTANCE;
  }

  public static boolean valueIsMissing(int i) {
    return i == missingValueIndicator();
  }

  @Override
  public TimeColumn create(String name) {
    return TimeColumn.create(name);
  }

  @Override
  public AbstractColumnParser<LocalTime> customParser(ReadOptions options) {
    return new TimeParser(this, options);
  }

  public static int missingValueIndicator() {
    return Integer.MIN_VALUE;
  }
}
