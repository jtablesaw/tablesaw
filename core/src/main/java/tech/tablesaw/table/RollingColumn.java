package tech.tablesaw.table;

import org.apache.commons.lang3.StringUtils;
import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/**
 * Does a calculation on a rolling basis (e.g. mean for last 20 days)
 */
public class RollingColumn {

    private final Column column;
    private final int window;

    public RollingColumn(Column column, int window) {
        this.column = column;
        this.window = window;
    }

    public NumberColumn mean() {
        return calc(AggregateFunctions.mean);
    }

    public NumberColumn median() {
        return calc(AggregateFunctions.median);
    }

    public NumberColumn geometricMean() {
        return calc(AggregateFunctions.geometricMean);
    }

    public NumberColumn sum() {
        return calc(AggregateFunctions.sum);
    }

    public NumberColumn pctChange() {
        return calc(AggregateFunctions.pctChange);
    }

    private String generateNewColumnName(AggregateFunction function) {
        boolean useSpaces = column.name().matches("\\s+");
        String separator = useSpaces ? " " : "";
        return new StringBuilder(column.name())
                .append(separator).append(useSpaces ? function.functionName() : StringUtils.capitalize(function.functionName()))
                .append(separator).append(window)
                .toString();
    }

    public NumberColumn calc(AggregateFunction function) {
        // TODO: the subset operation copies the array. creating a view would likely be more efficient
        NumberColumn result = DoubleColumn.create(generateNewColumnName(function), column.size());
        for (int i = 0; i < window - 1; i++) {
            result.append(NumberColumn.MISSING_VALUE);
        }
        for (int origColIndex = 0; origColIndex < column.size() - window + 1; origColIndex++) {
            Selection selection = new BitmapBackedSelection();
            selection.addRange(origColIndex, origColIndex + window);
            Column windowedColumn = column.subset(selection);
            double calc;
            if (windowedColumn instanceof DoubleColumn) {
                calc = function.summarize((NumberColumn) windowedColumn);
            } else {
                throw new IllegalArgumentException("Cannot calculate " + function.functionName()
                        + " on column of type " + windowedColumn.type());
            }
            result.append(calc);
        }
        return result;
    }

}
