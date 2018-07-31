package tech.tablesaw.columns.strings;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.util.Locale;

public class StringColumnType extends AbstractColumnType {

    public StringColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public StringColumn create(String name) {
        return StringColumn.create(name);
    }

    @Override
    public StringStringParser defaultParser() {
        return new StringStringParser(this);
    }

    @Override
    public StringStringParser customParser(CsvReadOptions options) {
        return new StringStringParser(this, options);
    }

    static class StringStringParser extends StringParser<String> {

        private Locale locale = Locale.getDefault();

        public StringStringParser(ColumnType columnType) {
            super(columnType);
        }

        public StringStringParser(ColumnType columnType, CsvReadOptions readOptions) {
            super(columnType);
            if (readOptions.locale() != null) {
                locale = readOptions.locale();
            }
            if (readOptions.missingValueIndicator() != null) {
                missingValueStrings = Lists.newArrayList(readOptions.missingValueIndicator());
            }
        }

        @Override
        public boolean canParse(String s) {
            return false;
        }

        @Override
        public String parse(String s) {
            return null;
        }
    }
}
