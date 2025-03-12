package tech.tablesaw.columns.numbers;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.io.ReadOptions;
import tech.tablesaw.util.StringUtils;

public class LongParser extends AbstractColumnParser<Long> {

  private final boolean ignoreZeroDecimal;

  public LongParser(ColumnType columnType) {
    super(columnType);
    ignoreZeroDecimal = ReadOptions.DEFAULT_IGNORE_ZERO_DECIMAL;
  }

  public LongParser(LongColumnType columnType, ReadOptions readOptions) {
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
    String s = str;
    try {
      if (ignoreZeroDecimal) {
        s = StringUtils.removeZeroDecimal(s);
      }
      Long.parseLong(AbstractColumnParser.remove(s, ','));
      return true;
    } catch (NumberFormatException e) {
      // it's all part of the plan
      return false;
    }
  }

  @Override
  public Long parse(String s) {
    return parseLong(s);
  }

  @Override
  public double parseDouble(String str) {
    return parseLong(str);
  }

  @Override
  public long parseLong(String str) {
    if (isMissing(str)) {
      return LongColumnType.missingValueIndicator();
    }
    String s = str;
    if (ignoreZeroDecimal) {
      s = StringUtils.removeZeroDecimal(s);
    }
    return Long.parseLong(AbstractColumnParser.remove(s, ','));
  }
}
