package tech.tablesaw.columns.strings;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import static tech.tablesaw.api.StringColumn.MISSING_VALUE;

public class StringStringParser extends StringParser<String> {

    public StringStringParser(ColumnType columnType) {
        super(columnType);
    }

    public StringStringParser(ColumnType columnType, CsvReadOptions readOptions) {
        super(columnType);
        if (readOptions.missingValueIndicator() != null) {
            missingValueStrings = Lists.newArrayList(readOptions.missingValueIndicator());
        }
    }

    @Override
    public boolean canParse(String s) {
        return true;
    }

    @Override
    public String parse(String s) {
        if (isMissing(s)) {
            return MISSING_VALUE;
        }
        return s;
    }
}
