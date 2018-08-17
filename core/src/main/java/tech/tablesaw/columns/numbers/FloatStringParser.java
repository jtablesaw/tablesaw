package tech.tablesaw.columns.numbers;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FloatStringParser extends StringParser<Float> {

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    public FloatStringParser(ColumnType columnType) {
        super(columnType);
    }

    public FloatStringParser(FloatColumnType columnType, CsvReadOptions readOptions) {
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
            Float.parseFloat(s);
            return true;
        } catch (NumberFormatException e) {
            // it's all part of the plan
            return false;
        }
    }

    @Override
    public Float parse(String s) {
        if (isMissing(s)) {
            return FloatColumnType.missingValueIndicator();
        }
        final Matcher matcher = COMMA_PATTERN.matcher(s);
        return Float.parseFloat(matcher.replaceAll(""));
    }

    @Override
    public float parseFloat(String s) {
        if (isMissing(s)) {
            return FloatColumnType.missingValueIndicator();
        }
        final Matcher matcher = COMMA_PATTERN.matcher(s);
        return Float.parseFloat(matcher.replaceAll(""));
    }
}
