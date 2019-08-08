package tech.tablesaw.filtering;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

import java.util.function.Function;

public class DeferredBooleanColumn extends DeferredColumn {

    public DeferredBooleanColumn(String columnName) {
        super(columnName);
    }

    public Function<Table, Selection> isFalse() {
        return table -> table.booleanColumn(getColumnName()).isFalse();
    }

    public Function<Table, Selection> isTrue() {
        return table -> table.booleanColumn(getColumnName()).isTrue();
    }

    public Function<Table, Selection> isEqualTo(BooleanColumn other) {
        return table -> table.booleanColumn(getColumnName()).isEqualTo(other);
    }

    public Function<Table, Selection> isMissing() {
        return table -> table.booleanColumn(getColumnName()).isMissing();
    }

    public Function<Table, Selection> isNotMissing() {
        return table -> table.booleanColumn(getColumnName()).isNotMissing();
    }
}
