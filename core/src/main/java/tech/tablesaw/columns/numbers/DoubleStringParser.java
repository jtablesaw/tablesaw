package tech.tablesaw.columns.numbers;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoubleStringParser extends StringParser<Double> {

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    public DoubleStringParser(ColumnType columnType) {
        super(columnType);
    }

    public DoubleStringParser(DoubleColumnType doubleColumnType, CsvReadOptions readOptions) {
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
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            // it's all part of the plan
            return false;
        }
    }

    @Override
    public Double parse(String s) {
        if (isMissing(s)) {
            return DoubleColumnType.missingValueIndicator();
        }
        final Matcher matcher = COMMA_PATTERN.matcher(s);
        return Double.parseDouble(matcher.replaceAll(""));
    }

    @Override
    public double parseDouble(String s) {
        if (isMissing(s)) {
            return DoubleColumnType.missingValueIndicator();
        }
        final Matcher matcher = COMMA_PATTERN.matcher(s);
        return Double.parseDouble(matcher.replaceAll(""));
    }
}
