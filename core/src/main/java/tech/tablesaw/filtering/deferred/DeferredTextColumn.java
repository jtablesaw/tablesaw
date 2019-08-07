package tech.tablesaw.filtering.deferred;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

import java.util.Collection;
import java.util.function.Function;

public class DeferredTextColumn extends DeferredColumn  {

    public DeferredTextColumn(String columnName) {
        super(columnName);
    }

    public Function<Table, Selection> isEmptyString() {
        return table -> table.textColumn(getColumnName()).isEmptyString();
    }

    public Function<Table, Selection> startsWith(String string) {
        return table -> table.textColumn(getColumnName()).startsWith(string);
    }

    public Function<Table, Selection> endsWith(String string) {
        return table -> table.textColumn(getColumnName()).endsWith(string);
    }

    public Function<Table, Selection> containsString(String string) {
        return table -> table.textColumn(getColumnName()).containsString(string);
    }

    public Function<Table, Selection> matchesRegex(String string) {
        return table -> table.textColumn(getColumnName()).matchesRegex(string);
    }

    public Function<Table, Selection> isAlpha() {
        return table -> table.textColumn(getColumnName()).isAlpha();
    }

    public Function<Table, Selection> isNumeric() {
        return table -> table.textColumn(getColumnName()).isNumeric();
    }

    public Function<Table, Selection> isAlphaNumeric() {
        return table -> table.textColumn(getColumnName()).isAlphaNumeric();
    }

    public Function<Table, Selection> isUpperCase() {
        return table -> table.textColumn(getColumnName()).isUpperCase();
    }

    public Function<Table, Selection> isLowerCase() {
        return table -> table.textColumn(getColumnName()).isLowerCase();
    }

    public Function<Table, Selection> lengthEquals(int stringLength) {
        return table -> table.textColumn(getColumnName()).lengthEquals(stringLength);
    }

    public Function<Table, Selection> isShorterThan(int stringLength) {
        return table -> table.textColumn(getColumnName()).isShorterThan(stringLength);
    }

    public Function<Table, Selection> isLongerThan(int stringLength) {
        return table -> table.textColumn(getColumnName()).isLongerThan(stringLength);
    }

    public Function<Table, Selection> isIn(String... strings) {
        return table -> table.textColumn(getColumnName()).isIn(strings);
    }

    public Function<Table, Selection> isIn(Collection<String> strings) {
        return table -> table.textColumn(getColumnName()).isIn(strings);
    }

    public Function<Table, Selection> isNotIn(String... strings) {
        return table -> table.textColumn(getColumnName()).isNotIn(strings);
    }

    public Function<Table, Selection> isNotIn(Collection<String> strings) {
        return table -> table.textColumn(getColumnName()).isNotIn(strings);
    }

    public Function<Table, Selection> isEqualTo(Column<String> other) {
        return table -> table.textColumn(getColumnName()).isEqualTo(other);
    }

    public Function<Table, Selection> isNotEqualTo(Column<String> other) {
        return table -> table.textColumn(getColumnName()).isNotEqualTo(other);
    }

    public Function<Table, Selection> equalsIgnoreCase(Column<String> other) {
        return table -> table.textColumn(getColumnName()).equalsIgnoreCase(other);
    }

    public Function<Table, Selection> startsWith(Column<String> other) {
        return table -> table.textColumn(getColumnName()).startsWith(other);
    }

    public Function<Table, Selection> isMissing() {
        return table -> table.textColumn(getColumnName()).isMissing();
    }

    public Function<Table, Selection> isNotMissing() {
        return table -> table.textColumn(getColumnName()).isNotMissing();
    }

    public Function<Table, Selection> isEqualTo(String string) {
        return table -> table.textColumn(getColumnName()).isEqualTo(string);
    }

    public Function<Table, Selection> isNotEqualTo(String string) {
        return table -> table.textColumn(getColumnName()).isNotEqualTo(string);
    }
}
