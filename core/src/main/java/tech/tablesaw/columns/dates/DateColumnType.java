package tech.tablesaw.columns.dates;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.columns.AbstractColumnType;

public class DateColumnType extends AbstractColumnType {

    public DateColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public DateColumn create(String name) {
        return DateColumn.create(name);
    }
}
