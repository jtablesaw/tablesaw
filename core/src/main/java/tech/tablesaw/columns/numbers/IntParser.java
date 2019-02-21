package tech.tablesaw.columns.numbers;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.io.ReadOptions;

public class IntParser extends AbstractColumnParser<Integer> {

    public IntParser(ColumnType columnType) {
        super(columnType);
    }

    public IntParser(IntColumnType columnType, ReadOptions readOptions) {
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
            Integer.parseInt(AbstractColumnParser.remove(s, ','));
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
        String s = str;
        if (s.endsWith(".0")) {
            s = s.substring(0, s.length() - 2);
        }
        return Integer.parseInt(AbstractColumnParser.remove(s, ','));
    }
}
