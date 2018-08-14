package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;

public class DoubleColumnType extends AbstractColumnType<Double> {

    public static final DoubleStringParser DEFAULT_PARSER = new DoubleStringParser(ColumnType.DOUBLE);
    public static final DoubleColumnType INSTANCE =
            new DoubleColumnType(Double.NaN, 8, "DOUBLE", "Double");

    private DoubleColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public DoubleColumn create(String name) {
        return DoubleColumn.create(name);
    }

    @Override
    public DoubleStringParser defaultParser() {
        return DEFAULT_PARSER;
    }

    @Override
    public DoubleStringParser customParser(CsvReadOptions options) {
        return new DoubleStringParser(this, options);
    }

    @Override
    public boolean compare(int rowNumber, Column<Double> temp, Column<Double> original) {
        DoubleColumn tempDouble = (DoubleColumn) temp;
        DoubleColumn originalDouble = (DoubleColumn) original;
        return originalDouble.getDouble(rowNumber) == tempDouble.getDouble(tempDouble.size() - 1);
    }
}
