package tech.tablesaw.columns.dates;

import static tech.tablesaw.columns.dates.PackedLocalDate.asLocalDate;
import static tech.tablesaw.columns.dates.PackedLocalDate.toDateString;

import java.time.LocalDate;
import javax.annotation.concurrent.Immutable;
import tech.tablesaw.columns.TemporalColumnFormatter;

@Immutable
public class DateColumnFormatter extends TemporalColumnFormatter {

  public String format(int value) {
    if (value == DateColumnType.missingValueIndicator()) {
      return getMissingString();
    }
    if (getFormat() == null) {
      return toDateString(value);
    }
    LocalDate date = asLocalDate(value);
    if (date == null) {
      return "";
    }
    return getFormat().format(date);
  }

  @Override
  public String toString() {
    return "DateColumnFormatter{"
        + "format="
        + getFormat()
        + ", missingString='"
        + getMissingString()
        + '\''
        + '}';
  }
}
