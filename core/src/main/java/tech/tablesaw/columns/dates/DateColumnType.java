package tech.tablesaw.columns.dates;

import java.time.LocalDate;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.csv.CsvReadOptions;

public class DateColumnType extends AbstractColumnType {

    public static final DateStringParser DEFAULT_PARSER = new DateStringParser(ColumnType.LOCAL_DATE);
    public static final DateColumnType INSTANCE =
            new DateColumnType(Integer.MIN_VALUE, 4, "LOCAL_DATE", "Date");

    private DateColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public DateColumn create(String name) {
        return DateColumn.create(name);
    }

    @Override
    public StringParser<LocalDate> defaultParser() {
        return new DateStringParser(this);
    }

    @Override
    public StringParser<LocalDate> customParser(CsvReadOptions options) {
        return new DateStringParser(this, options);
    }

}
