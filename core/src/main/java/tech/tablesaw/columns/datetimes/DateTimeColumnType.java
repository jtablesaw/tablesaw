package tech.tablesaw.columns.datetimes;

import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.time.LocalDateTime;

public class DateTimeColumnType extends AbstractColumnType {

    public DateTimeColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public DateTimeColumn create(String name) {
        return DateTimeColumn.create(name);
    }

    @Override
    public StringParser<LocalDateTime> defaultParser() {
        return null;
    }

    @Override
    public StringParser<LocalDateTime> customParser(CsvReadOptions options) {
        return null;
    }
}
