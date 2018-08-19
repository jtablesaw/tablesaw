package tech.tablesaw.columns.times;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.time.LocalTime;

public class TimeColumnType extends AbstractColumnType {

    public static final int BYTE_SIZE = 4;

    public static final TimeStringParser DEFAULT_PARSER = new TimeStringParser(ColumnType.LOCAL_TIME);
    public static final TimeColumnType INSTANCE =
            new TimeColumnType(BYTE_SIZE, "LOCAL_TIME", "Time");

    private TimeColumnType(int byteSize, String name, String printerFriendlyName) {
        super(byteSize, name, printerFriendlyName);
    }

    @Override
    public TimeColumn create(String name) {
        return TimeColumn.create(name);
    }

    @Override
    public StringParser<LocalTime> customParser(CsvReadOptions options) {
        return new TimeStringParser(this, options);
    }

    @Override
    public Comparable<?> getMissingValueIndicator() {
        return missingValueIndicator();
    }

    public static int missingValueIndicator() {
        return Integer.MIN_VALUE;
    }
}
