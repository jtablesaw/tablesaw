package tech.tablesaw.columns.datetimes;

import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.columns.AbstractColumnType;

public class DateTimeColumnType extends AbstractColumnType {

    public DateTimeColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public DateTimeColumn create(String name) {
        return DateTimeColumn.create(name);
    }
}
