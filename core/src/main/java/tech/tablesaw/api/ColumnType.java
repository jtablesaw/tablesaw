package tech.tablesaw.api;

import com.google.common.base.Preconditions;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.columns.booleans.BooleanColumnType;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.dates.DateColumnType;
import tech.tablesaw.columns.datetimes.DateTimeColumnType;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.SkipColumnType;
import tech.tablesaw.columns.strings.StringColumnType;
import tech.tablesaw.columns.times.TimeColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.util.HashMap;
import java.util.Map;

public interface ColumnType {

    Map<String, ColumnType> values = new HashMap<>();

    // standard column types
    ColumnType BOOLEAN = new BooleanColumnType(Byte.MIN_VALUE, 1, "BOOLEAN", "Boolean");
    ColumnType STRING = new StringColumnType("", 4, "STRING", "String");
    ColumnType NUMBER = new DoubleColumnType(Double.NaN, 8, "NUMBER", "Number");
    ColumnType LOCAL_DATE = new DateColumnType(Integer.MIN_VALUE, 4, "LOCAL_DATE", "Date");
    ColumnType LOCAL_DATE_TIME = new DateTimeColumnType(Long.MIN_VALUE, 8, "LOCAL_DATE_TIME","DateTime");
    ColumnType LOCAL_TIME = new TimeColumnType(Integer.MIN_VALUE, 4, "LOCAL_TIME", "Time");
    ColumnType SKIP = new SkipColumnType(null, 0, "SKIP", "Skipped");

    static void register(ColumnType type) {
        values.put(type.name(), type);
    }

    static ColumnType[] values() {
        return values.values().toArray(new ColumnType[0]);
    }

    static ColumnType valueOf(String name) {
        Preconditions.checkNotNull(name);

        ColumnType result = values.get(name);
        if (result == null) {
            throw new IllegalArgumentException(name + " is not a registered column type.");
        }
        return result;
    }

    Column create(String name);

    String name();

    Comparable<?> getMissingValue();

    int byteSize();

    String getPrinterFriendlyName();

    StringParser defaultParser();

    StringParser customParser(CsvReadOptions options);
}
