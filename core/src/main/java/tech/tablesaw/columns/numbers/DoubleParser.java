package tech.tablesaw.columns.numbers;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.io.ReadOptions;

public class DoubleParser extends AbstractColumnParser<Double> {

  private boolean percentage = false;

  public DoubleParser(ColumnType columnType) {
    super(columnType);
  }

  public DoubleParser(DoubleColumnType doubleColumnType, ReadOptions readOptions) {
    super(doubleColumnType);
    if (readOptions.missingValueIndicator() != null) {
      missingValueStrings = Lists.newArrayList(readOptions.missingValueIndicator());
    }
    percentage = readOptions.percentage();
  }

  @Override
  public boolean canParse(String s) {
    try {
      parseDouble(s);
      return true;
    } catch (NumberFormatException e) {
      // it's all part of the plan
      return false;
    }
  }

  @Override
  public Double parse(String s) {
    return parseDouble(s);
  }

  @Override
  public double parseDouble(String s) {
    if (isMissing(s)) {
      return DoubleColumnType.missingValueIndicator();
    }
    boolean isPercentage = false;
    if (percentage && s.endsWith("%")) {
      isPercentage = true;
      s = s.substring(0, s.length() - 1);
    }
    double d = Double.parseDouble(AbstractColumnParser.remove(s, ','));
    return isPercentage ? d / 100.0 : d;
  }
}
