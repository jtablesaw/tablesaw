package tech.tablesaw.columns.booleans;

import com.google.common.collect.Lists;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.io.csv.CsvReadOptions;

import static tech.tablesaw.api.BooleanColumn.*;

public class BooleanStringParser extends StringParser<Boolean> {

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
        if (isMissing(s)) {
            return null;
        } else if (TypeUtils.TRUE_STRINGS.contains(s)) {
            return true;
        } else if (TypeUtils.FALSE_STRINGS.contains(s)) {
            return false;
        } else {
            throw new IllegalArgumentException("Attempting to convert non-boolean value " +
                    s + " to Boolean");
        }
    }

    @Override
    public byte parseByte(String s) {
        if (isMissing(s)) {
            return MISSING_VALUE;
        } else if (TypeUtils.TRUE_STRINGS.contains(s)) {
            return BooleanColumn.BYTE_TRUE;
        } else if (TypeUtils.FALSE_STRINGS.contains(s)) {
            return BYTE_FALSE;
        } else {
            throw new IllegalArgumentException("Attempting to convert non-boolean value " +
                    s + " to Boolean");
        }
    }
}

