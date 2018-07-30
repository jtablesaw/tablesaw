package tech.tablesaw.columns.times;

import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.AbstractColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.time.LocalTime;

public class TimeColumnType extends AbstractColumnType {

    public TimeColumnType(Comparable<?> missingValue, int byteSize, String name, String printerFriendlyName) {
        super(missingValue, byteSize, name, printerFriendlyName);
    }

    @Override
    public TimeColumn create(String name) {
        return TimeColumn.create(name);
    }

    @Override
    public StringParser<LocalTime> defaultParser() {
        return null;
    }

    @Override
    public StringParser<LocalTime> customParser(CsvReadOptions options) {
        return null;
    }
}
