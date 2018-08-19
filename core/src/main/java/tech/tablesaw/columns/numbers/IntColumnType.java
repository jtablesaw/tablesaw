package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

public class IntColumnType extends AbstractColumnType {

    public static final IntStringParser DEFAULT_PARSER = new IntStringParser(ColumnType.INTEGER);

    private static final int BYTE_SIZE = 4;

    public static final IntColumnType INSTANCE =
            new IntColumnType(BYTE_SIZE, "INTEGER", "Integer");

    private IntColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    @Override
    public IntColumn create(String name) {
        return IntColumn.create(name);
    }

    @Override
    public IntStringParser customParser(CsvReadOptions options) {
        return new IntStringParser(this, options);
    }

    public static boolean isMissingValue(int value) {
	return value == missingValueIndicator();
    }

    public static int missingValueIndicator() {
        return Integer.MIN_VALUE;
    }

    @Override
    public Comparable<?> getMissingValueIndicator() {
        return missingValueIndicator();
    }
}
