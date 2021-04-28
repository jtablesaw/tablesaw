package tech.tablesaw.columns;

/**
 * Abstract class for Column Formatters Every column type has a specialized print formatter that is
 * used for formatting output for both table printing via, for example, table.printAll(). It is also
 * used for writing text files using table.write().csv()
 */
public abstract class ColumnFormatter {

  // The string to use for missing values
  private final String missingString;

  /** Constructs a new Formatter with the given missing value string. */
  protected ColumnFormatter(String missingString) {
    this.missingString = missingString;
  }

  /** Returns the string to be used in place of any missing values in the column */
  public String getMissingString() {
    return missingString;
  }
}
