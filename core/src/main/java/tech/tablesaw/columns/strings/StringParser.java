package tech.tablesaw.columns.strings;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.io.ReadOptions;

public class StringParser extends AbstractColumnParser<String> {

  public StringParser(ColumnType columnType) {
    super(columnType);
  }

  public StringParser(ColumnType columnType, ReadOptions readOptions) {
    super(columnType);
    if (readOptions.missingValueIndicators().length > 0) {
      missingValueStrings = Lists.newArrayList(readOptions.missingValueIndicators());
    }
  }

  @Override
  public boolean canParse(String s) {
    return true;
  }

  @Override
  public String parse(String s) {
    if (isMissing(s)) {
      return StringColumnType.missingValueIndicator();
    }
    return s;
  }
}
