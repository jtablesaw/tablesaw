package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

public class LongColumnType extends AbstractColumnType {

    public static final LongParser DEFAULT_PARSER = new LongParser(ColumnType.LONG);

    private static final int BYTE_SIZE = 8;

    public static final LongColumnType INSTANCE =
            new LongColumnType(BYTE_SIZE, "LONG", "Long");

    private LongColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    @Override
    public LongColumn create(String name) {
        return LongColumn.create(name);
    }

    public LongParser defaultParser() {
        return DEFAULT_PARSER;
    }

    @Override
    public LongParser customParser(CsvReadOptions options) {
        return new LongParser(this, options);
    }

    public static boolean isMissingValue(long value) {
	return value == missingValueIndicator();
    }

    public static long missingValueIndicator() {
        return Long.MIN_VALUE;
    }
}
