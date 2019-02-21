package tech.tablesaw.columns.numbers;

import com.google.common.collect.Lists;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.io.ReadOptions;

public class ShortParser extends AbstractColumnParser<Short> {

    public ShortParser(ShortColumnType columnType) {
        super(columnType);
    }

    public ShortParser(ShortColumnType columnType, ReadOptions readOptions) {
        super(columnType);
        if (readOptions.missingValueIndicator() != null) {
            missingValueStrings = Lists.newArrayList(readOptions.missingValueIndicator());
        }
    }

    @Override
    public boolean canParse(String str) {
        if (isMissing(str)) {
            return true;
        }
        String s = str;
        try {
            if (s.endsWith(".0")) {
                s = s.substring(0, s.length() - 2);
            }
            Short.parseShort(AbstractColumnParser.remove(s, ','));
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
        String s = str;
        if (s.endsWith(".0")) {
            s = s.substring(0, s.length() - 2);
        }
        return Short.parseShort(AbstractColumnParser.remove(s, ','));
    }
}
