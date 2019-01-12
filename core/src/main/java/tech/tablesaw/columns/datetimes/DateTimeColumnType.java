package tech.tablesaw.columns.datetimes;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

public class DateTimeColumnType extends AbstractColumnType {

    public static int BYTE_SIZE = 8;

    public static final DateTimeParser DEFAULT_PARSER = new DateTimeParser(ColumnType.LOCAL_DATE_TIME);

    private static DateTimeColumnType INSTANCE =
            new DateTimeColumnType(BYTE_SIZE, "LOCAL_DATE_TIME", "DateTime");

    private DateTimeColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    public static DateTimeColumnType instance() {
        if (INSTANCE == null) {
            INSTANCE = new DateTimeColumnType(BYTE_SIZE, "LOCAL_DATE_TIME", "DateTime");
        }
        return INSTANCE;
    }

    @Override
    public DateTimeColumn create(String name) {
        return DateTimeColumn.create(name);
    }

    @Override
    public DateTimeParser customParser(CsvReadOptions options) {
        return new DateTimeParser(this, options);
    }

    public static long missingValueIndicator() {
        return Long.MIN_VALUE;
    }
}
