package tech.tablesaw.columns.numbers;

import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.table.RollingColumn;

/**
 * Does a calculation on a rolling basis (e.g. mean for last 20 days)
 */
public class NumberRollingColumn extends RollingColumn {

    public NumberRollingColumn(NumberColumn column, int window) {
        super(column, window);
    }

    public NumberColumn mean() {
        return (NumberColumn) calc(AggregateFunctions.mean);
    }

    public NumberColumn median() {
        return (NumberColumn) calc(AggregateFunctions.median);
    }

    public NumberColumn geometricMean() {
        return (NumberColumn) calc(AggregateFunctions.geometricMean);
    }

    public NumberColumn sum() {
        return (NumberColumn) calc(AggregateFunctions.sum);
    }

    public NumberColumn pctChange() {
        return (NumberColumn) calc(AggregateFunctions.pctChange);
    }

}
