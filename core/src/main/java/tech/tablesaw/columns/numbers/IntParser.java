package tech.tablesaw.columns.numbers;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.io.ReadOptions;
import tech.tablesaw.util.StringUtils;

public class IntParser extends AbstractColumnParser<Integer> {

  private final boolean ignoreZeroDecimal;

  public IntParser(ColumnType columnType) {
    super(columnType);
    ignoreZeroDecimal = ReadOptions.DEFAULT_IGNORE_ZERO_DECIMAL;
  }

  public IntParser(IntColumnType columnType, ReadOptions readOptions) {
    super(columnType);
    if (readOptions.missingValueIndicators().length > 0) {
      missingValueStrings = Lists.newArrayList(readOptions.missingValueIndicators());
    }
    ignoreZeroDecimal = readOptions.ignoreZeroDecimal();
  }

  @Override
  public boolean canParse(String str) {
    if (isMissing(str)) {
      return true;
    }
    try {
      if (ignoreZeroDecimal) {
        str = StringUtils.removeZeroDecimal(str);
      }
      Integer.parseInt(AbstractColumnParser.remove(str, ','));
      return true;
    } catch (NumberFormatException e) {
      // it's all part of the plan
      return false;
    }
  }

  @Override
  public Integer parse(String s) {
    return parseInt(s);
  }

  @Override
  public double parseDouble(String s) {
    return parseInt(s);
  }

  @Override
  public int parseInt(String str) {
    if (isMissing(str)) {
      return IntColumnType.missingValueIndicator();
    }
    if (ignoreZeroDecimal) {
      str = StringUtils.removeZeroDecimal(str);
    }
    return Integer.parseInt(AbstractColumnParser.remove(str, ','));
  }
}
