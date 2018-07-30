package tech.tablesaw.columns.strings;

import tech.tablesaw.api.StringColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.StringParser;

public class StringColumnType extends AbstractColumnType {

    public StringColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public StringColumn create(String name) {
        return StringColumn.create(name);
    }

    @Override
    public StringParser defaultParser() {
        return null;
    }
}
