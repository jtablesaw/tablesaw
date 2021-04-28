package tech.tablesaw.columns.instant;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.concurrent.Immutable;
import tech.tablesaw.columns.TemporalColumnFormatter;

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

  public String format(long value) {
    if (value == InstantColumnType.missingValueIndicator()) {
      return getMissingString();
    }
    if (getFormat() == null) {
      return PackedInstant.toString(value);
    }
    Instant instant = PackedInstant.asInstant(value);
    if (instant == null) {
      return "";
    }
    ZonedDateTime time = instant.atZone(zoneId);
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
