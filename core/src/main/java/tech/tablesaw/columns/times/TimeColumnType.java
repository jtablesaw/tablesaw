package tech.tablesaw.columns.times;

import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.AbstractColumnType;

public class TimeColumnType extends AbstractColumnType {

    public TimeColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public TimeColumn create(String name) {
        return TimeColumn.create(name);
    }
}
