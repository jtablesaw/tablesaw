package tech.tablesaw.filtering.deferred;

import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

import java.util.function.Function;

public class Q {

    public static Function<Table, Selection> not(Function<Table, Selection> deferredSelection) {
        return new Not(deferredSelection);
    }

    public static DeferredBooleanColumn booleanColumn(String columnName) {
        return new DeferredBooleanColumn(columnName);
    }

    public static DeferredStringColumn stringColumn(String columnName) {
        return new DeferredStringColumn(columnName);
    }

    public static DeferredTextColumn textColumn(String columnName) {
        return new DeferredTextColumn(columnName);
    }

    public static DeferredNumberColumn numberColumn(String columnName) {
        return new DeferredNumberColumn(columnName);
    }

    public static DeferredDateColumn dateColumn(String columnName) {
        return new DeferredDateColumn(columnName);
    }

    public static DeferredDateTimeColumn dateTimeColumn(String columnName) {
        return new DeferredDateTimeColumn(columnName);
    }

    public static DeferredInstantColumn instantColumn(String columnName) {
        return new DeferredInstantColumn(columnName);
    }

    public static DeferredTimeColumn timeColumn(String columnName) {
        return new DeferredTimeColumn(columnName);
    }
}
