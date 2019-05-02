package tech.tablesaw.api;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import tech.tablesaw.columns.AbstractColumnParser;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.SkipColumnType;
import tech.tablesaw.columns.booleans.BooleanColumnType;
import tech.tablesaw.columns.dates.DateColumnType;
import tech.tablesaw.columns.datetimes.DateTimeColumnType;
import tech.tablesaw.columns.instant.InstantColumnType;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.FloatColumnType;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.columns.numbers.LongColumnType;
import tech.tablesaw.columns.numbers.ShortColumnType;
import tech.tablesaw.columns.strings.StringColumnType;
import tech.tablesaw.columns.strings.TextColumnType;
import tech.tablesaw.columns.times.TimeColumnType;
import tech.tablesaw.io.ReadOptions;

public interface ColumnType {

    Map<String, ColumnType> values = new HashMap<>();

    // standard column types
    ShortColumnType SHORT = ShortColumnType.instance();
    IntColumnType INTEGER = IntColumnType.instance();
    LongColumnType LONG = LongColumnType.instance();
    FloatColumnType FLOAT = FloatColumnType.instance();
    BooleanColumnType BOOLEAN = BooleanColumnType.instance();
    StringColumnType STRING = StringColumnType.instance();
    DoubleColumnType DOUBLE = DoubleColumnType.instance();
    DateColumnType LOCAL_DATE = DateColumnType.instance();
    TimeColumnType LOCAL_TIME = TimeColumnType.instance();
    DateTimeColumnType LOCAL_DATE_TIME = DateTimeColumnType.instance();
    InstantColumnType INSTANT = InstantColumnType.instance();
    TextColumnType TEXT = TextColumnType.instance();
    SkipColumnType SKIP = SkipColumnType.instance();

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

    int byteSize();

    String getPrinterFriendlyName();

    AbstractColumnParser<?> customParser(ReadOptions options);

    default boolean compare(int rowNumber, Column<?> temp, Column<?> original) {
        return original.get(rowNumber).equals(temp.get(temp.size() - 1));
    }
}
