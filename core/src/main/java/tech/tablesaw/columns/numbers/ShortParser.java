package tech.tablesaw.columns.numbers;

import com.google.common.collect.Lists;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.io.ReadOptions;
import tech.tablesaw.util.StringUtils;

public class ShortParser extends AbstractColumnParser<Short> {

  private final boolean ignoreZeroDecimal;

  public ShortParser(ShortColumnType columnType) {
    super(columnType);
    ignoreZeroDecimal = ReadOptions.DEFAULT_IGNORE_ZERO_DECIMAL;
  }

  public ShortParser(ShortColumnType columnType, ReadOptions readOptions) {
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
      Short.parseShort(normalize(str));
      return true;
    } catch (NumberFormatException e) {
      // it's all part of the plan
      return false;
    }
  }

  @Override
  public Short parse(String s) {
    return parseShort(s);
  }

  @Override
  public double parseDouble(String s) {
    return parseInt(s);
  }

  @Override
  public short parseShort(String str) {
    if (isMissing(str)) {
      return ShortColumnType.missingValueIndicator();
    }
    return Short.parseShort(normalize(str));
  }

  private String normalize(String str) {
    if (ignoreZeroDecimal) {
      str = StringUtils.removeZeroDecimal(str);
    }
    return AbstractColumnParser.remove(str, THOUSANDS_SEP);
  }
}
