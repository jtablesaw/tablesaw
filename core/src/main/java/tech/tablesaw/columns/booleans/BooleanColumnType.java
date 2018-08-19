package tech.tablesaw.columns.booleans;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

public class BooleanColumnType extends AbstractColumnType {

    public static final BooleanStringParser DEFAULT_PARSER = new BooleanStringParser(ColumnType.BOOLEAN);

    private static byte BYTE_SIZE = 1;

    public static final BooleanColumnType INSTANCE =
            new BooleanColumnType(BYTE_SIZE,
                    "BOOLEAN",
                    "Boolean");

    private BooleanColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    @Override
    public BooleanColumn create(String name) {
        return BooleanColumn.create(name);
    }

    @Override
    public Comparable<?> getMissingValueIndicator() {
        return missingValueIndicator();
    }

    @Override
    public BooleanStringParser customParser(CsvReadOptions readOptions) {
        return new BooleanStringParser(this, readOptions);
    }

    public static byte missingValueIndicator() {
        return Byte.MIN_VALUE;
    }
}
