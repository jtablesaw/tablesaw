package tech.tablesaw.columns.numbers;

import com.google.common.collect.Lists;
import java.text.NumberFormat;
import java.text.ParseException;
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
      if (isPercent(AbstractColumnParser.remove(s, ','))) {
        s = AbstractColumnParser.remove(s, ',');
        Number number = NumberFormat.getPercentInstance().parse(s);
      } else {
        Double.parseDouble(AbstractColumnParser.remove(s, ','));
      }
      return true;
    } catch (NumberFormatException | ParseException e) {
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
    if (isPercent(AbstractColumnParser.remove(s, ','))) {
      s = AbstractColumnParser.remove(s, ',').substring(0, s.length() - 1);
      return Double.parseDouble(s) / 100.0;
    }
    return Double.parseDouble(AbstractColumnParser.remove(s, ','));
  }

  private boolean isPercent(String s) {
    return s.charAt(s.length() - 1) == '%';
  }
}
