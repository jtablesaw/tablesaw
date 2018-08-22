package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

public class DoubleColumnType extends AbstractColumnType {

    private static final int BYTE_SIZE = 8;

    public static final DoubleParser DEFAULT_PARSER = new DoubleParser(ColumnType.DOUBLE);

    public static final DoubleColumnType INSTANCE =
            new DoubleColumnType(BYTE_SIZE, "DOUBLE", "Double");

    private DoubleColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    @Override
    public DoubleColumn create(String name) {
        return DoubleColumn.create(name);
    }

    @Override
    public DoubleParser customParser(CsvReadOptions options) {
        return new DoubleParser(this, options);
    }

    public static boolean isMissingValue(double value) {
	return Double.isNaN(value);
    }

    public static double missingValueIndicator() {
        return Double.NaN;
    }
}
