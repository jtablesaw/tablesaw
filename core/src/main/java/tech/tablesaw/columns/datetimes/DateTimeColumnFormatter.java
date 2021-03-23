package tech.tablesaw.columns.datetimes;

import com.google.common.base.Strings;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import javax.annotation.concurrent.Immutable;

@Immutable
public class DateTimeColumnFormatter {

  private final DateTimeFormatter format;
  private String missingValueString = "";

  public DateTimeColumnFormatter() {
    this.format = null;
  }

  public DateTimeColumnFormatter(DateTimeFormatter format) {
    this.format = format;
  }

  public DateTimeColumnFormatter(DateTimeFormatter format, String missingValueString) {
    this.format = format;
    this.missingValueString = missingValueString;
  }

  public String format(long epochSecond, int secondNanos) {
    if (epochSecond == DateTimeColumnType.missingValueIndicator()) {
      return missingValueString;
    }
    if (format == null) {
      return defaultFormat(epochSecond, secondNanos);
    }
    LocalDateTime time = LocalDateTime.ofEpochSecond(epochSecond, secondNanos, ZoneOffset.UTC);
    if (time == null) {
      return "";
    }
    return format.format(time);
  }

  public static String defaultFormat(long epochSecond, int secondNanos) {
    if (epochSecond == Long.MIN_VALUE) {
      return "";
    }
    LocalDateTime ldt = LocalDateTime.ofEpochSecond(epochSecond, secondNanos, ZoneOffset.UTC);

    return ""
        + ldt.getYear()
        + "-"
        + Strings.padStart(Integer.toString(ldt.getMonthValue()), 2, '0')
        + "-"
        + Strings.padStart(Integer.toString(ldt.getDayOfMonth()), 2, '0')
        + "T"
        + Strings.padStart(Integer.toString(ldt.getHour()), 2, '0')
        + ":"
        + Strings.padStart(Integer.toString(ldt.getMinute()), 2, '0')
        + ":"
        + Strings.padStart(Integer.toString(ldt.getSecond()), 2, '0')
        + "."
        + Strings.padStart(String.valueOf(ldt.getNano()), 3, '0');
  }

  @Override
  public String toString() {
    return "DateTimeColumnFormatter{"
        + "format="
        + format
        + ", missingValueString='"
        + missingValueString
        + '\''
        + '}';
  }
}
