package tech.tablesaw.columns.numbers;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntStringParser extends StringParser {

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
    public boolean canParse(String s) {
        if (isMissing(s)) {
            return true;
        }
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            // it's all part of the plan
            return false;
        }
    }

    @Override
    public Double parse(String s) {
        if (isMissing(s)) {
            return Double.NaN;
        }
        final Matcher matcher = COMMA_PATTERN.matcher(s);
        return (double) Integer.parseInt(matcher.replaceAll(""));
    }

    @Override
    public double parseDouble(String s) {
        if (isMissing(s)) {
            return NumberColumn.MISSING_VALUE;
        }
        final Matcher matcher = COMMA_PATTERN.matcher(s);
        return Integer.parseInt(matcher.replaceAll(""));
    }
}
