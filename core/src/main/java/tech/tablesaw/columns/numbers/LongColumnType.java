package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

public class LongColumnType extends AbstractColumnType {

    public static final LongStringParser DEFAULT_PARSER = new LongStringParser(ColumnType.LONG);

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

    public LongStringParser defaultParser() {
        return DEFAULT_PARSER;
    }

    @Override
    public LongStringParser customParser(CsvReadOptions options) {
        return new LongStringParser(this, options);
    }

    public static boolean isMissingValue(long value) {
	return value == missingValueIndicator();
    }

    public static long missingValueIndicator() {
        return Long.MIN_VALUE;
    }

    @Override
    public Comparable<?> getMissingValueIndicator() {
        return missingValueIndicator();
    }
}
