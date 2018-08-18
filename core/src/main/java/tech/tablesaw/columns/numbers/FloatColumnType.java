package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

public class FloatColumnType extends AbstractColumnType {

    public static final int BYTE_SIZE = 4;

    public static final FloatStringParser DEFAULT_PARSER = new FloatStringParser(ColumnType.FLOAT);

    public static final FloatColumnType INSTANCE =
            new FloatColumnType(BYTE_SIZE, "FLOAT", "Float");

    FloatColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    @Override
    public NumberColumn create(String name) {
        return NumberColumn.createWithFloats(name);
    }

    @Override
    public FloatStringParser defaultParser() {
        return DEFAULT_PARSER;
    }

    @Override
    public FloatStringParser customParser(CsvReadOptions options) {
        return new FloatStringParser(this, options);
    }

    public static float missingValueIndicator() {
        return Float.NaN;
    }

    @Override
    public Comparable<?> getMissingValueIndicator() {
        return missingValueIndicator();
    }
}
