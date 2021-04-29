package tech.tablesaw.columns.numbers;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.io.ReadOptions;

public class FloatParser extends AbstractColumnParser<Float> {

  public FloatParser(ColumnType columnType) {
    super(columnType);
  }

  public FloatParser(FloatColumnType columnType, ReadOptions readOptions) {
    super(columnType);
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
      Float.parseFloat(AbstractColumnParser.remove(s, ','));
      return true;
    } catch (NumberFormatException e) {
      // it's all part of the plan
      return false;
    }
  }

  @Override
  public Float parse(String s) {
    return parseFloat(s);
  }

  @Override
  public float parseFloat(String s) {
    if (isMissing(s)) {
      return FloatColumnType.missingValueIndicator();
    }
    return Float.parseFloat(AbstractColumnParser.remove(s, ','));
  }
}
