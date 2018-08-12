package tech.tablesaw.columns.numbers;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Row;
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
    public void copy(IntArrayList rows, Column<Double> oldColumn, Column<Double> newColumn) {
        DoubleColumn oldDouble = (DoubleColumn) oldColumn;
        DoubleColumn newDouble = (DoubleColumn) newColumn;
        for (int index : rows) {
            newDouble.append(oldDouble.getDouble(index));
        }
    }

    @Override
    public void copyFromRows(IntArrayList rows, Column<Double> newColumn, Row row) {
        DoubleColumn newDate = (DoubleColumn) newColumn;
        for (int index : rows) {
            row.at(index);
            double value = row.getDouble(newColumn.name());
            newDate.append(value);
        }
    }

    @Override
    public boolean compare(int rowNumber, Column<Double> temp, Column<Double> original) {
        DoubleColumn tempDouble = (DoubleColumn) temp;
        DoubleColumn originalDouble = (DoubleColumn) original;
        return originalDouble.getDouble(rowNumber) == tempDouble.getDouble(tempDouble.size() - 1);
    }
}
