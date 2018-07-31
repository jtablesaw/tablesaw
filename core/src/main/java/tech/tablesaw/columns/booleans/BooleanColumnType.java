package tech.tablesaw.columns.booleans;

import com.google.common.collect.Lists;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.io.csv.CsvReadOptions;

public class BooleanColumnType extends AbstractColumnType {

    public BooleanColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public Column create(String name) {
        return BooleanColumn.create(name);
    }

    @Override
    public BooleanStringParser defaultParser() {
        return new BooleanStringParser(this);
    }

    @Override
    public BooleanStringParser customParser(CsvReadOptions readOptions) {
        return new BooleanStringParser(this, readOptions);
    }

    static class BooleanStringParser extends StringParser<Boolean> {

        public BooleanStringParser(ColumnType columnType) {
            super(columnType);
        }

        public BooleanStringParser(BooleanColumnType booleanColumnType, CsvReadOptions readOptions) {
            super(booleanColumnType);
            if (readOptions.missingValueIndicator() != null) {
                missingValueStrings = Lists.newArrayList(readOptions.missingValueIndicator());
            }
        }

        @Override
        public boolean canParse(String s) {
            if (isMissing(s)) {
                return true;
            }
            return TypeUtils.TRUE_STRINGS_FOR_DETECTION.contains(s)
                    || TypeUtils.FALSE_STRINGS_FOR_DETECTION.contains(s);
        }

        @Override
        public Boolean parse(String s) {
            return null;
        }
    }

}
