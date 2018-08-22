package tech.tablesaw.columns.strings;

import com.google.common.collect.Lists;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import static tech.tablesaw.api.StringColumn.MISSING_VALUE;

public class StringParser extends AbstractParser<String> {

    public StringParser(ColumnType columnType) {
        super(columnType);
    }

    public StringParser(ColumnType columnType, CsvReadOptions readOptions) {
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
