package tech.tablesaw.columns.numbers;

import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.table.RollingColumn;

/**
 * Does a calculation on a rolling basis (e.g. mean for last 20 days)
 */
public class NumberRollingColumn extends RollingColumn {

    public NumberRollingColumn(NumericColumn<?> column, int window) {
        super(column, window);
    }

    public DoubleColumn mean() {
        return (DoubleColumn) calc(AggregateFunctions.mean);
    }

    public DoubleColumn median() {
        return (DoubleColumn) calc(AggregateFunctions.median);
    }

    public DoubleColumn geometricMean() {
        return (DoubleColumn) calc(AggregateFunctions.geometricMean);
    }

    public DoubleColumn sum() {
        return (DoubleColumn) calc(AggregateFunctions.sum);
    }

    public DoubleColumn pctChange() {
        return (DoubleColumn) calc(AggregateFunctions.pctChange);
    }

}
