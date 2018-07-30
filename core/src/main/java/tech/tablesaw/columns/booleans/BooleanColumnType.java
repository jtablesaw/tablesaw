package tech.tablesaw.columns.booleans;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.StringParser;

public class BooleanColumnType extends AbstractColumnType {

    public BooleanColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public Column create(String name) {
        return BooleanColumn.create(name);
    }

    @Override
    public StringParser defaultParser() {
        return null;
    }
}
