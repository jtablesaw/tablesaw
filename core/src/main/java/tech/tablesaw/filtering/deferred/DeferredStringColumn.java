package tech.tablesaw.filtering.deferred;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

import java.util.Collection;
import java.util.function.Function;

public class DeferredStringColumn extends DeferredColumn  {

    public DeferredStringColumn(String columnName) {
        super(columnName);
    }

    public Function<Table, Selection> isEmptyString() {
        return table -> table.stringColumn(getColumnName()).isEmptyString();
    }

    public Function<Table, Selection> startsWith(String string) {
        return table -> table.stringColumn(getColumnName()).startsWith(string);
    }

    public Function<Table, Selection> endsWith(String string) {
        return table -> table.stringColumn(getColumnName()).endsWith(string);
    }

    public Function<Table, Selection> containsString(String string) {
        return table -> table.stringColumn(getColumnName()).containsString(string);
    }

    public Function<Table, Selection> matchesRegex(String string) {
        return table -> table.stringColumn(getColumnName()).matchesRegex(string);
    }

    public Function<Table, Selection> isAlpha() {
        return table -> table.stringColumn(getColumnName()).isAlpha();
    }

    public Function<Table, Selection> isNumeric() {
        return table -> table.stringColumn(getColumnName()).isNumeric();
    }

    public Function<Table, Selection> isAlphaNumeric() {
        return table -> table.stringColumn(getColumnName()).isAlphaNumeric();
    }

    public Function<Table, Selection> isUpperCase() {
        return table -> table.stringColumn(getColumnName()).isUpperCase();
    }

    public Function<Table, Selection> isLowerCase() {
        return table -> table.stringColumn(getColumnName()).isLowerCase();
    }

    public Function<Table, Selection> lengthEquals(int stringLength) {
        return table -> table.stringColumn(getColumnName()).lengthEquals(stringLength);
    }

    public Function<Table, Selection> isShorterThan(int stringLength) {
        return table -> table.stringColumn(getColumnName()).isShorterThan(stringLength);
    }

    public Function<Table, Selection> isLongerThan(int stringLength) {
        return table -> table.stringColumn(getColumnName()).isLongerThan(stringLength);
    }

    public Function<Table, Selection> isIn(String... strings) {
        return table -> table.stringColumn(getColumnName()).isIn(strings);
    }

    public Function<Table, Selection> isIn(Collection<String> strings) {
        return table -> table.stringColumn(getColumnName()).isIn(strings);
    }

    public Function<Table, Selection> isNotIn(String... strings) {
        return table -> table.stringColumn(getColumnName()).isNotIn(strings);
    }

    public Function<Table, Selection> isNotIn(Collection<String> strings) {
        return table -> table.stringColumn(getColumnName()).isNotIn(strings);
    }

    public Function<Table, Selection> isEqualTo(Column<String> other) {
        return table -> table.stringColumn(getColumnName()).isEqualTo(other);
    }

    public Function<Table, Selection> isNotEqualTo(Column<String> other) {
        return table -> table.stringColumn(getColumnName()).isNotEqualTo(other);
    }

    public Function<Table, Selection> equalsIgnoreCase(Column<String> other) {
        return table -> table.stringColumn(getColumnName()).equalsIgnoreCase(other);
    }

    public Function<Table, Selection> startsWith(Column<String> other) {
        return table -> table.stringColumn(getColumnName()).startsWith(other);
    }

    public Function<Table, Selection> isMissing() {
        return table -> table.stringColumn(getColumnName()).isMissing();
    }

    public Function<Table, Selection> isNotMissing() {
        return table -> table.stringColumn(getColumnName()).isNotMissing();
    }

    public Function<Table, Selection> isEqualTo(String string) {
        return table -> table.stringColumn(getColumnName()).isEqualTo(string);
    }

    public Function<Table, Selection> isNotEqualTo(String string) {
        return table -> table.stringColumn(getColumnName()).isNotEqualTo(string);
    }
}
