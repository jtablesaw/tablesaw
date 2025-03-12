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
    try {
      Long.parseLong(normalize(str));
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
    return Long.parseLong(normalize(str));
  }

  private String normalize(String str) {
    if (ignoreZeroDecimal) {
      str = StringUtils.removeZeroDecimal(str);
    }
    return AbstractColumnParser.remove(str, THOUSANDS_SEP);
  }
}
