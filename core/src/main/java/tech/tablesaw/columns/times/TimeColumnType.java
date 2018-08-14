package tech.tablesaw.columns.times;

import java.time.LocalTime;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.csv.CsvReadOptions;

public class TimeColumnType extends AbstractColumnType {

    public static final TimeStringParser DEFAULT_PARSER = new TimeStringParser(ColumnType.LOCAL_TIME);
    public static final TimeColumnType INSTANCE =
            new TimeColumnType(Integer.MIN_VALUE, 4, "LOCAL_TIME", "Time");

    private TimeColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public TimeColumn create(String name) {
        return TimeColumn.create(name);
    }

    @Override
    public StringParser<LocalTime> defaultParser() {
        return new TimeStringParser(this);
    }

    @Override
    public StringParser<LocalTime> customParser(CsvReadOptions options) {
        return new TimeStringParser(this, options);
    }

}
