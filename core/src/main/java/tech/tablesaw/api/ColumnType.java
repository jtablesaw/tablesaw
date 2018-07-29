package tech.tablesaw.api;

import com.google.common.base.Preconditions;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.ColumnTypeImpl;

import java.util.HashMap;
import java.util.Map;

public interface ColumnType {

    Map<String, ColumnType> values = new HashMap<>();

    // standard column types
    ColumnType BOOLEAN = new ColumnTypeImpl(Byte.MIN_VALUE, 1, "BOOLEAN", "Boolean");
    ColumnType STRING = new ColumnTypeImpl("", 4, "STRING", "String");
    ColumnType NUMBER = new ColumnTypeImpl(Double.NaN, 8, "NUMBER", "Number");
    ColumnType LOCAL_DATE = new ColumnTypeImpl(Integer.MIN_VALUE, 4, "LOCAL_DATE", "Date");
    ColumnType LOCAL_DATE_TIME = new ColumnTypeImpl(Long.MIN_VALUE, 8, "LOCAL_DATE_TIME","DateTime");
    ColumnType LOCAL_TIME = new ColumnTypeImpl(Integer.MIN_VALUE, 4, "LOCAL_TIME", "Time");
    ColumnType SKIP = new ColumnTypeImpl(null, 0, "SKIP", "Skipped");

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

    default Column create(String name) {
        final String columnTypeName = this.name();
        switch (columnTypeName) {
            case "BOOLEAN": return BooleanColumn.create(name);
            case "STRING": return StringColumn.create(name);
            case "NUMBER": return DoubleColumn.create(name);
            case "LOCAL_DATE": return DateColumn.create(name);
            case "LOCAL_DATE_TIME": return DateTimeColumn.create(name);
            case "LOCAL_TIME": return TimeColumn.create(name);
            case "SKIP": throw new IllegalArgumentException("Cannot create column of type SKIP");
            default:
                throw new UnsupportedOperationException("Column type " + name() + " doesn't support column creation");
        }
    }

    String name();

    Comparable<?> getMissingValue();

    int byteSize();

    String getPrinterFriendlyName();
}
