package tech.tablesaw.columns;

import java.time.format.DateTimeFormatter;

/**
 * Abstract class for Column Formatters for temporal columns Date, DateTime, Time, and Instant Every
 * column type has a specialized print formatter that is used for formatting output for both table
 * printing via, for example, table.printAll(). It is also used for writing text files using
 * table.write().csv()
 */
public abstract class TemporalColumnFormatter extends ColumnFormatter {

  private final DateTimeFormatter format;

  /** Constructs a new Formatter with the given formatter and an empty missing value string. */
  protected TemporalColumnFormatter(DateTimeFormatter format) {
    super("");
    this.format = format;
  }

  /** Constructs a new Formatter with the given formatter and missing value string. */
  protected TemporalColumnFormatter(DateTimeFormatter format, String missingValueString) {
    super(missingValueString);
    this.format = format;
  }

  /** Constructs a new default Formatter. This produces unformatted output. */
  protected TemporalColumnFormatter() {
    super("");
    this.format = null;
  }

  public DateTimeFormatter getFormat() {
    return format;
  }
}
