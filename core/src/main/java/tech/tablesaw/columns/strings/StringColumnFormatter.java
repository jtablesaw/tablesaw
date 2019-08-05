package tech.tablesaw.columns.strings;

import java.util.function.Function;

public class StringColumnFormatter {

  private final Function<String, String> formatter;
  private String missingString = "";

  public StringColumnFormatter() {
    this.formatter = null;
  }

  public StringColumnFormatter(Function<String, String> formatFunction) {
    this.formatter = formatFunction;
  }

  public StringColumnFormatter(Function<String, String> formatFunction, String missingString) {
    this.formatter = formatFunction;
    this.missingString = missingString;
  }

  public String format(String value) {

    if (StringColumnType.missingValueIndicator().equals(value)) {
      return missingString;
    }
    if (formatter == null) {
      return value;
    }
    return formatter.apply(value);
  }

  @Override
  public String toString() {
    return "StringColumnFormatter{"
        + "format="
        + formatter
        + ", missingString='"
        + missingString
        + '\''
        + '}';
  }
}
