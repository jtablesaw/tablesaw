package tech.tablesaw.api;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.SkipColumnType;
import tech.tablesaw.columns.StringParser;
import tech.tablesaw.columns.booleans.BooleanColumnType;
import tech.tablesaw.columns.dates.DateColumnType;
import tech.tablesaw.columns.datetimes.DateTimeColumnType;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.FloatColumnType;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.columns.numbers.LongColumnType;
import tech.tablesaw.columns.strings.StringColumnType;
import tech.tablesaw.columns.times.TimeColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

public interface ColumnType {

    Map<String, ColumnType> values = new HashMap<>();

    // standard column types
    IntColumnType INTEGER = IntColumnType.INSTANCE;
    LongColumnType LONG = LongColumnType.INSTANCE;
    FloatColumnType FLOAT = FloatColumnType.INSTANCE;
    BooleanColumnType BOOLEAN = BooleanColumnType.INSTANCE;
    StringColumnType STRING = StringColumnType.INSTANCE;
    DoubleColumnType DOUBLE = DoubleColumnType.INSTANCE;
    DateColumnType LOCAL_DATE = DateColumnType.INSTANCE;
    DateTimeColumnType LOCAL_DATE_TIME = DateTimeColumnType.INSTANCE;
    TimeColumnType LOCAL_TIME = TimeColumnType.INSTANCE;
    SkipColumnType SKIP = SkipColumnType.INSTANCE;

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

    Column<?> create(String name);

    String name();

    Comparable<?> getMissingValueIndicator();

    int byteSize();

    String getPrinterFriendlyName();

    StringParser<?> defaultParser();

    StringParser<?> customParser(CsvReadOptions options);

    default boolean compare(int rowNumber, Column<?> temp, Column<?> original) {
        return original.get(rowNumber).equals(temp.get(temp.size() - 1));
    }
}
