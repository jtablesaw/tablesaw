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

    public DoubleColumn min() {
        return (DoubleColumn) calc(AggregateFunctions.min);
    }

    public DoubleColumn max() {
        return (DoubleColumn) calc(AggregateFunctions.max);
    }

    public DoubleColumn countMissing() {
        return (DoubleColumn) calc(AggregateFunctions.countMissing);
    }

    public DoubleColumn countNonMissing() {
        return (DoubleColumn) calc(AggregateFunctions.countNonMissing);
    }

    public DoubleColumn variance() {
        return (DoubleColumn) calc(AggregateFunctions.variance);
    }

    public DoubleColumn skewness() {
        return (DoubleColumn) calc(AggregateFunctions.skewness);
    }

    public DoubleColumn kurtosis() {
        return (DoubleColumn) calc(AggregateFunctions.kurtosis);
    }

}
