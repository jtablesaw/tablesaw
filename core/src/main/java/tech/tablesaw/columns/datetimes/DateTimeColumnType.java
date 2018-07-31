package tech.tablesaw.columns.datetimes;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.time.LocalDateTime;

public class DateTimeColumnType extends AbstractColumnType {

    public static final DateTimeStringParser DEFAULT_PARSER = new DateTimeStringParser(ColumnType.LOCAL_DATE_TIME);

    public DateTimeColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public DateTimeColumn create(String name) {
        return DateTimeColumn.create(name);
    }

    @Override
    public StringParser<LocalDateTime> defaultParser() {
        return new DateTimeStringParser(this);
    }

    @Override
    public DateTimeStringParser customParser(CsvReadOptions options) {
        return new DateTimeStringParser(this, options);
    }

}
