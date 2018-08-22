package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

public class ShortColumnType extends AbstractColumnType {

    public static final ShortParser DEFAULT_PARSER = new ShortParser(ColumnType.SHORT);

    private static final int BYTE_SIZE = 2;

    public static final ShortColumnType INSTANCE =
            new ShortColumnType(BYTE_SIZE, "SHORT", "Short");

    private ShortColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    @Override
    public ShortColumn create(String name) {
        return ShortColumn.create(name);
    }

    @Override
    public ShortParser customParser(CsvReadOptions options) {
        return new ShortParser(this, options);
    }

    public static boolean isMissingValue(int value) {
	return value == missingValueIndicator();
    }

    public static short missingValueIndicator() {
        return Short.MIN_VALUE;
    }
}
