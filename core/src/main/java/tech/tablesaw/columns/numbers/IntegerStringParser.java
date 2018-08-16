package tech.tablesaw.columns.numbers;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntegerStringParser extends DoubleStringParser {

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    public IntegerStringParser(ColumnType columnType) {
        super(columnType);
    }

    public IntegerStringParser(DoubleColumnType doubleColumnType, CsvReadOptions readOptions) {
        super(doubleColumnType);
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
            return DoubleColumn.MISSING_VALUE;
        }
        final Matcher matcher = COMMA_PATTERN.matcher(s);
        return Integer.parseInt(matcher.replaceAll(""));
    }
}
