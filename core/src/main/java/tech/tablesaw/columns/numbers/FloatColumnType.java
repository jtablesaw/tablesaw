package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

public class FloatColumnType extends AbstractColumnType {

    public static final int BYTE_SIZE = 4;

    public static final FloatParser DEFAULT_PARSER = new FloatParser(ColumnType.FLOAT);

    public static final FloatColumnType INSTANCE =
            new FloatColumnType(BYTE_SIZE, "FLOAT", "Float");

    private FloatColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    @Override
    public FloatColumn create(String name) {
        return FloatColumn.create(name);
    }

    @Override
    public FloatParser customParser(CsvReadOptions options) {
        return new FloatParser(this, options);
    }

    public static boolean isMissingValue(float value) {
	return Float.isNaN(value);
    }

    public static float missingValueIndicator() {
        return Float.NaN;
    }
}
