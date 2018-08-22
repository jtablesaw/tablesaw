package tech.tablesaw.columns.dates;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.AbstractParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.time.LocalDate;

public class DateColumnType extends AbstractColumnType {

    public static final int BYTE_SIZE = 4;
    public static final DateParser DEFAULT_PARSER = new DateParser(ColumnType.LOCAL_DATE);

    public static final DateColumnType INSTANCE =
            new DateColumnType(BYTE_SIZE, "LOCAL_DATE", "Date");

    private DateColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    @Override
    public DateColumn create(String name) {
        return DateColumn.create(name);
    }

    @Override
    public AbstractParser<LocalDate> customParser(CsvReadOptions options) {
        return new DateParser(this, options);
    }

    public static int missingValueIndicator() {
        return Integer.MIN_VALUE;
    }
}
