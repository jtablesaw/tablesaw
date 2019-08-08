package tech.tablesaw.filtering;

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

import java.util.function.Function;

public class DeferredNumberColumn extends DeferredColumn {

    public DeferredNumberColumn(String columnName) {
        super(columnName);
    }
    
    public Function<Table, Selection> isEqualTo(NumberColumn other) {
        return table -> table.numberColumn(getColumnName()).isEqualTo(other);
    }

    public Function<Table, Selection> isBetweenExclusive(double start, double end) {
        return table -> table.numberColumn(getColumnName()).isBetweenExclusive(start, end);
    }

    public Function<Table, Selection> isBetweenInclusive(double start, double end) {
        return table -> table.numberColumn(getColumnName()).isBetweenInclusive(start, end);
    }

    public Function<Table, Selection> isGreaterThan(double f) {
        return table -> table.numberColumn(getColumnName()).isGreaterThan(f);
    }

    public Function<Table, Selection> isGreaterThanOrEqualTo(double f) {
        return table -> table.numberColumn(getColumnName()).isGreaterThanOrEqualTo(f);
    }

    public Function<Table, Selection> isLessThan(double f) {
        return table -> table.numberColumn(getColumnName()).isLessThan(f);
    }

    public Function<Table, Selection> isLessThanOrEqualTo(double f) {
        return table -> table.numberColumn(getColumnName()).isLessThanOrEqualTo(f);
    }

    public Function<Table, Selection> isIn(Number... numbers) {
        return table -> table.numberColumn(getColumnName()).isIn(numbers);
    }

    public Function<Table, Selection> isIn(double... doubles) {
        return table -> table.numberColumn(getColumnName()).isIn(doubles);
    }

    public Function<Table, Selection> isNotIn(Number... numbers) {
        return table -> table.numberColumn(getColumnName()).isNotIn(numbers);
    }

    public Function<Table, Selection> isNotIn(double... doubles) {
        return table -> table.numberColumn(getColumnName()).isNotIn(doubles);
    }

    public Function<Table, Selection> isZero() {
        return table -> table.numberColumn(getColumnName()).isZero();
    }

    public Function<Table, Selection> isPositive() {
        return table -> table.numberColumn(getColumnName()).isPositive();
    }

    public Function<Table, Selection> isNegative() {
        return table -> table.numberColumn(getColumnName()).isNegative();
    }

    public Function<Table, Selection> isNonNegative() {
        return table -> table.numberColumn(getColumnName()).isNonNegative();
    }

    public Function<Table, Selection> isCloseTo(Number target, Number margin) {
        return table -> table.numberColumn(getColumnName()).isCloseTo(target, margin);
    }

    public Function<Table, Selection> isMissing() {
        return table -> table.numberColumn(getColumnName()).isMissing();
    }

    public Function<Table, Selection> isNotMissing() {
        return table -> table.numberColumn(getColumnName()).isNotMissing();
    }

    public Function<Table, Selection> isGreaterThan(NumericColumn<?> d) {
        return table -> table.numberColumn(getColumnName()).isGreaterThan(d);
    }

    public Function<Table, Selection> isGreaterThanOrEqualTo(NumericColumn<?> d) {
        return table -> table.numberColumn(getColumnName()).isGreaterThanOrEqualTo(d);
    }

    public Function<Table, Selection> isEqualTo(NumericColumn<?> d) {
        return table -> table.numberColumn(getColumnName()).isEqualTo(d);
    }

    public Function<Table, Selection> isNotEqualTo(NumericColumn<?> d) {
        return table -> table.numberColumn(getColumnName()).isNotEqualTo(d);
    }

    public Function<Table, Selection> isLessThan(NumericColumn<?> d) {
        return table -> table.numberColumn(getColumnName()).isLessThan(d);
    }

    public Function<Table, Selection> isLessThanOrEqualTo(NumericColumn<?> d) {
        return table -> table.numberColumn(getColumnName()).isLessThanOrEqualTo(d);
    }
}
