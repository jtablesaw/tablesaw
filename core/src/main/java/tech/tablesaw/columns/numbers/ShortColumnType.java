package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.ReadOptions;

public class ShortColumnType extends AbstractColumnType {

    public static final ShortParser DEFAULT_PARSER = new ShortParser(ShortColumnType.INSTANCE);

    private static final int BYTE_SIZE = 2;

    private static ShortColumnType INSTANCE;

    private ShortColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    public static ShortColumnType instance() {
        if (INSTANCE == null) {
            INSTANCE = new ShortColumnType(BYTE_SIZE, "SHORT", "Short");
        }
        return INSTANCE;
    }

    @Override
    public ShortColumn create(String name) {
        return ShortColumn.create(name);
    }

    @Override
    public ShortParser customParser(ReadOptions options) {
        return new ShortParser(this, options);
    }

    public static boolean isMissingValue(int value) {
        return value == missingValueIndicator();
    }

    public static short missingValueIndicator() {
        return Short.MIN_VALUE;
    }
}
