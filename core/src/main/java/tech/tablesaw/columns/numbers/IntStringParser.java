package tech.tablesaw.columns.numbers;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntStringParser extends StringParser<Integer> {

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    public IntStringParser(ColumnType columnType) {
        super(columnType);
    }

    public IntStringParser(IntColumnType columnType, CsvReadOptions readOptions) {
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
                s = s.replaceFirst(".0$", "");
            }
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            // it's all part of the plan
            return false;
        }
    }

    @Override
    public Integer parse(String s) {
        if (isMissing(s)) {
            return IntColumnType.missingValueIndicator();
        }
        final Matcher matcher = COMMA_PATTERN.matcher(s);
        return Integer.parseInt(matcher.replaceAll(""));
    }

    @Override
    public double parseDouble(String str) {
        if (isMissing(str)) {
            return IntColumnType.missingValueIndicator();
        }
        String s = str;
        if (s.endsWith(".0")) {
            s = s.replaceFirst(".0$", "");
        }
        final Matcher matcher = COMMA_PATTERN.matcher(s);
        return Integer.parseInt(matcher.replaceAll(""));
    }

    @Override
    public int parseInt(String str) {
        if (isMissing(str)) {
            return IntColumnType.missingValueIndicator();
        }
        String s = str;
        if (s.endsWith(".0")) {
            s = s.replaceFirst(".0$", "");
        }
        final Matcher matcher = COMMA_PATTERN.matcher(s);
        return Integer.parseInt(matcher.replaceAll(""));
    }
}
