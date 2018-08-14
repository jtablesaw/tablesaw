package tech.tablesaw.columns.booleans;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;

public class BooleanColumnType extends AbstractColumnType<Boolean> {

    public static final BooleanStringParser DEFAULT_PARSER = new BooleanStringParser(ColumnType.BOOLEAN);


    public static final BooleanColumnType INSTANCE =
            new BooleanColumnType(Byte.MIN_VALUE,
                    1,
                    "BOOLEAN",
                    "Boolean");

    private BooleanColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public BooleanColumn create(String name) {
        return BooleanColumn.create(name);
    }

    @Override
    public BooleanStringParser defaultParser() {
        return DEFAULT_PARSER;
    }

    @Override
    public BooleanStringParser customParser(CsvReadOptions readOptions) {
        return new BooleanStringParser(this, readOptions);
    }

    @Override
    public boolean compare(int rowNumber, Column temp, Column original) {
        BooleanColumn tempBoolean = (BooleanColumn) temp;
        BooleanColumn originalBoolean = (BooleanColumn) original;
        return originalBoolean.get(rowNumber) == tempBoolean.get(tempBoolean.size() - 1);
    }
}
