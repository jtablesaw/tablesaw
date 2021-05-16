package tech.tablesaw.columns.strings;

import java.util.function.Function;
import tech.tablesaw.columns.ColumnFormatter;

public class StringColumnFormatter extends ColumnFormatter {

  private final Function<String, String> formatter;

  public StringColumnFormatter() {
    super("");
    this.formatter = null;
  }

  public StringColumnFormatter(Function<String, String> formatFunction) {
    super("");
    this.formatter = formatFunction;
  }

  public StringColumnFormatter(Function<String, String> formatFunction, String missingString) {
    super(missingString);
    this.formatter = formatFunction;
  }

  public String format(String value) {

    if (StringColumnType.missingValueIndicator().equals(value)) {
      return getMissingString();
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
        + getMissingString()
        + '\''
        + '}';
  }
}
