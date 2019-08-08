package tech.tablesaw.filtering;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.selection.Selection;

import java.time.LocalTime;
import java.util.function.Function;

public class DeferredTimeColumn extends DeferredColumn {

    public DeferredTimeColumn(String columnName) {
        super(columnName);
    }

    public Function<Table, Selection> isMidnight() {
        return table -> table.timeColumn(getColumnName()).isMidnight();
    }

    public Function<Table, Selection> isNoon() {
        return table -> table.timeColumn(getColumnName()).isNoon();
    }

    public Function<Table, Selection> isBefore(LocalTime time) {
        return table -> table.timeColumn(getColumnName()).isBefore(time);
    }

    public Function<Table, Selection> isAfter(LocalTime time) {
        return table -> table.timeColumn(getColumnName()).isAfter(time);
    }

    public Function<Table, Selection> isOnOrAfter(LocalTime time) {
        return table -> table.timeColumn(getColumnName()).isOnOrAfter(time);
    }

    public Function<Table, Selection> isOnOrBefore(LocalTime value) {
        return table -> table.timeColumn(getColumnName()).isOnOrBefore(value);
    }

    public Function<Table, Selection> isBeforeNoon() {
        return table -> table.timeColumn(getColumnName()).isBeforeNoon();
    }

    public Function<Table, Selection> isAfterNoon() {
        return table -> table.timeColumn(getColumnName()).isAfterNoon();
    }

    public Function<Table, Selection> isNotEqualTo(LocalTime value) {
        return table -> table.timeColumn(getColumnName()).isNotEqualTo(value);
    }

    public Function<Table, Selection> isEqualTo(LocalTime value) {
        return table -> table.timeColumn(getColumnName()).isEqualTo(value);
    }

    public Function<Table, Selection> isEqualTo(TimeColumn column) {
        return table -> table.timeColumn(getColumnName()).isEqualTo(column);
    }

    public Function<Table, Selection> isBefore(TimeColumn column) {
        return table -> table.timeColumn(getColumnName()).isBefore(column);
    }

    public Function<Table, Selection> isAfter(TimeColumn column) {
        return table -> table.timeColumn(getColumnName()).isAfter(column);
    }

    public Function<Table, Selection> isNotEqualTo(TimeColumn column) {
        return table -> table.timeColumn(getColumnName()).isNotEqualTo(column);
    }

    public Function<Table, Selection> isMissing() {
        return table -> table.timeColumn(getColumnName()).isMissing();
    }

    public Function<Table, Selection> isNotMissing() {
        return table -> table.timeColumn(getColumnName()).isNotMissing();
    }
}
