package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.ReadOptions;

public class IntColumnType extends AbstractColumnType {

    public static final IntParser DEFAULT_PARSER = new IntParser(ColumnType.INTEGER);

    private static final int BYTE_SIZE = 4;

    private static IntColumnType INSTANCE;

    private IntColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    public static IntColumnType instance() {
        if (INSTANCE == null) {
            INSTANCE = new IntColumnType(BYTE_SIZE, "INTEGER", "Integer");
        }
        return INSTANCE;
    }

    @Override
    public IntColumn create(String name) {
        return IntColumn.create(name);
    }

    @Override
    public IntParser customParser(ReadOptions options) {
        return new IntParser(this, options);
    }

    public static boolean isMissingValue(int value) {
        return value == missingValueIndicator();
    }

    public static int missingValueIndicator() {
        return Integer.MIN_VALUE;
    }
}
