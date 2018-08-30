package tech.tablesaw.columns.numbers;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.io.csv.CsvReadOptions;

public class IntParser extends AbstractParser<Integer> {

    public IntParser(ColumnType columnType) {
        super(columnType);
    }

    public IntParser(IntColumnType columnType, CsvReadOptions readOptions) {
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
            Integer.parseInt(AbstractParser.remove(s, ','));
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

    public short parseShort(String str) {
        if (isMissing(str)) {
            return Short.MIN_VALUE;
        }
        String s = str;
        if (s.endsWith(".0")) {
            s = s.substring(0, s.length() - 2);
        }
        try {
            return Short.parseShort(AbstractParser.remove(s, ','));
        } catch (NumberFormatException e) {
            long longValue = Long.parseLong(s);
            throw new NumberOutOfRangeException(str, longValue, IntColumnType.INSTANCE);
        }

    }

    public byte parseByte(String str) {
        if (isMissing(str)) {
            return Byte.MIN_VALUE;
        }
        String s = str;
        if (s.endsWith(".0")) {
            s = s.substring(0, s.length() - 2);
        }
        try {
            return Byte.parseByte(AbstractParser.remove(s, ','));
        } catch (NumberFormatException e) {
            long longValue = Long.parseLong(s);
            throw new NumberOutOfRangeException(str, longValue, IntColumnType.INSTANCE);
        }

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
        try {
            return Integer.parseInt(AbstractParser.remove(s, ','));
        } catch (NumberFormatException e) {
            long longValue = Long.parseLong(s);
            throw new NumberOutOfRangeException(str, longValue, IntColumnType.INSTANCE);
        }
    }
}
