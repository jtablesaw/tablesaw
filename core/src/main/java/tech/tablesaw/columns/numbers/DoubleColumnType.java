package tech.tablesaw.columns.numbers;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.StringParser;

public class DoubleColumnType extends AbstractColumnType {

    public DoubleColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public DoubleColumn create(String name) {
        return DoubleColumn.create(name);
    }

    @Override
    public StringParser defaultParser() {
        return null;
    }
}
