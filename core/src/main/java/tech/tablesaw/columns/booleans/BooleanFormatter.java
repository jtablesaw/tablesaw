package tech.tablesaw.columns.booleans;

import tech.tablesaw.columns.ColumnFormatter;

public class BooleanFormatter extends ColumnFormatter {
  private String trueString = "true";
  private String falseString = "false";

  public BooleanFormatter(String trueString, String falseString, String missingString) {
    super(missingString);
    this.trueString = trueString;
    this.falseString = falseString;
  }

  public BooleanFormatter(String trueString, String falseString) {
    super("");
    this.trueString = trueString;
    this.falseString = falseString;
  }

  public BooleanFormatter(String missingString) {
    super(missingString);
  }

  public String format(Boolean value) {
    if (value == null) {
      return getMissingString();
    }
    if (value) {
      return trueString;
    }
    return falseString;
  }

  public String format(byte value) {
    if (value == BooleanColumnType.MISSING_VALUE) {
      return getMissingString();
    }
    if (value == (byte) 1) {
      return trueString;
    }
    return falseString;
  }

  @Override
  public String toString() {
    return "BooleanFormatter{"
        + "trueString='"
        + trueString
        + '\''
        + ", falseString='"
        + falseString
        + '\''
        + ", missingString='"
        + getMissingString()
        + '\''
        + '}';
  }
}
