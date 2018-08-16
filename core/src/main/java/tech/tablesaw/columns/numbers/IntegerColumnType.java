package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.io.csv.CsvReadOptions;

public class IntegerColumnType extends DoubleColumnType {

    public static final IntegerStringParser DEFAULT_PARSER = new IntegerStringParser(ColumnType.DOUBLE);
    public static final IntegerColumnType INSTANCE =
            new IntegerColumnType(Double.NaN, 8, "INTEGER", "Integer");

    private IntegerColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public NumberColumn create(String name) {
        return NumberColumn.createWithIntegers(name);
    }

    @Override
    public IntegerStringParser defaultParser() {
        return DEFAULT_PARSER;
    }

    @Override
    public IntegerStringParser customParser(CsvReadOptions options) {
        return new IntegerStringParser(this, options);
    }

}
