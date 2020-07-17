package tech.tablesaw.columns.dates;

import static tech.tablesaw.columns.dates.PackedLocalDate.asLocalDate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;
import javax.annotation.concurrent.Immutable;
import tech.tablesaw.columns.temporal.DateRange;

@Immutable
public class DateRangeColumnFormatter {

  private final DateTimeFormatter formatter;
  private String missingString = "";
  private String separator = "/"; // separates the from and to dates. The '/' is the ISO standard

  public DateRangeColumnFormatter() {
    this.formatter = null;
  }

  public DateRangeColumnFormatter(DateTimeFormatter formatter) {
    this.formatter = formatter;
  }

  /**
   * Returns a new DateRangeColumnFormatter
   *
   * @param formatter A Java DateTimeFormatter to be applied to both the from and to values
   * @param separator The separator for the from and to values
   * @param missingString The value to represent a missing string
   */
  public DateRangeColumnFormatter(
      DateTimeFormatter formatter, String separator, String missingString) {
    this.formatter = formatter;
    this.separator = separator;
    this.missingString = missingString;
  }

  public String format(int from, int to) {
    if (from == DateRangeColumnType.missingValueIndicator()
        && to == DateRangeColumnType.missingValueIndicator()) {
      return missingString;
    }
    if (formatter == null) {
      return new DateRange(from, to).toString();
    }

    String fString = "";
    String tString = "";
    LocalDate f = asLocalDate(from);
    LocalDate t = asLocalDate(to);
    if (f != null) {
      fString = formatter.format(f);
    }
    if (t != null) {
      tString = formatter.format(t);
    }
    return fString + separator + tString;
  }

  public String format(DateRange range) {
    if (range == null) {
      return missingString;
    }
    if (formatter == null) {
      return range.toString();
    }

    LocalDate f = range.getFrom();
    LocalDate t = range.getTo();
    String fString = "";
    String tString = "";
    if (f != null) {
      fString = formatter.format(f);
    }
    if (t != null) {
      tString = formatter.format(t);
    }
    return fString + separator + tString;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", DateRangeColumnFormatter.class.getSimpleName() + "[", "]")
        .add("format=" + formatter)
        .add("missingString='" + missingString + "'")
        .add("separator='" + separator + "'")
        .toString();
  }
}
