package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.ReadOptions;

public class FloatColumnType extends AbstractColumnType {

    public static final int BYTE_SIZE = 4;

    public static final FloatParser DEFAULT_PARSER = new FloatParser(ColumnType.FLOAT);

    private static FloatColumnType INSTANCE;

    private FloatColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    public static FloatColumnType instance() {
        if (INSTANCE == null) {
            INSTANCE = new FloatColumnType(BYTE_SIZE, "FLOAT", "float");
        }
        return INSTANCE;
    }

    @Override
    public FloatColumn create(String name) {
        return FloatColumn.create(name);
    }

    @Override
    public FloatParser customParser(ReadOptions options) {
        return new FloatParser(this, options);
    }

    public static boolean isMissingValue(float value) {
        return Float.isNaN(value);
    }

    public static float missingValueIndicator() {
        return Float.NaN;
    }
}
