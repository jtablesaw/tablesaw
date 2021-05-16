package tech.tablesaw.columns.numbers;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.io.ReadOptions;

public class DoubleParser extends AbstractColumnParser<Double> {

  public DoubleParser(ColumnType columnType) {
    super(columnType);
  }

  public DoubleParser(DoubleColumnType doubleColumnType, ReadOptions readOptions) {
    super(doubleColumnType);
    if (readOptions.missingValueIndicators().length > 0) {
      missingValueStrings = Lists.newArrayList(readOptions.missingValueIndicators());
    }
  }

  @Override
  public boolean canParse(String s) {
    if (isMissing(s)) {
      return true;
    }
    try {
      Double.parseDouble(AbstractColumnParser.remove(s, ','));
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
    return Double.parseDouble(AbstractColumnParser.remove(s, ','));
  }
}
