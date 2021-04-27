package tech.tablesaw.columns.instant;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.concurrent.Immutable;
import tech.tablesaw.columns.TemporalColumnFormatter;
import tech.tablesaw.columns.times.TimeColumnType;

@Immutable
public class InstantColumnFormatter extends TemporalColumnFormatter {

  private final ZoneId zoneId;

  public InstantColumnFormatter() {
    super(null);
    this.zoneId = ZoneOffset.UTC;
  }

  public InstantColumnFormatter(ZoneId zoneId) {
    super(null);
    this.zoneId = zoneId;
  }

  public InstantColumnFormatter(DateTimeFormatter format) {
    super(format);
    this.zoneId = ZoneOffset.UTC;
  }

  public InstantColumnFormatter(DateTimeFormatter format, ZoneId zoneId) {
    super(format);
    this.zoneId = zoneId;
  }

  public InstantColumnFormatter(DateTimeFormatter format, String missingValueString) {
    super(format, missingValueString);
    this.zoneId = ZoneOffset.UTC;
  }

  public InstantColumnFormatter(
      DateTimeFormatter format, ZoneId zoneId, String missingValueString) {
    super(format, missingValueString);
    this.zoneId = zoneId;
  }

  // TODO: Add a missing value test, looks like NPE
  public String format(long value) {
    if (value == TimeColumnType.missingValueIndicator()) {
      return getMissingString();
    }
    if (getFormat() == null) {
      return PackedInstant.toString(value);
    }
    ZonedDateTime time = PackedInstant.asInstant(value).atZone(zoneId);
    if (time == null) {
      return "";
    }
    return getFormat().format(time);
  }

  @Override
  public String toString() {
    return "InstantColumnFormatter{"
        + "format="
        + getFormat()
        + ", missingValueString='"
        + getMissingString()
        + '\''
        + '}';
  }
}
