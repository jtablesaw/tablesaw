package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoubleColumnType extends AbstractColumnType {

    public DoubleColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public DoubleColumn create(String name) {
        return DoubleColumn.create(name);
    }

    @Override
    public DoubleStringParser defaultParser() {
        return new DoubleStringParser(this);
    }

    @Override
    public DoubleStringParser customParser(CsvReadOptions options) {
        return new DoubleStringParser(this, options);
    }

    static class DoubleStringParser extends StringParser<Double> {

        private static final Pattern COMMA_PATTERN = Pattern.compile(",");

        public DoubleStringParser(ColumnType columnType) {
            super(columnType);
        }

        public DoubleStringParser(DoubleColumnType doubleColumnType, CsvReadOptions options) {
            super(doubleColumnType);
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
            if (s == null) {
                return DoubleColumn.MISSING_VALUE;
            }
            if (s.trim().equals("") || TypeUtils.MISSING_INDICATORS.contains(s)) {
                return DoubleColumn.MISSING_VALUE;
            }
            final Matcher matcher = COMMA_PATTERN.matcher(s);
            return Double.parseDouble(matcher.replaceAll(""));
        }
    }
}
