package tech.tablesaw.table;

import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/**
 * Does a calculation on a rolling basis (e.g. mean for last 20 days)
 */
public class RollingColumn {

    protected final Column<?> column;
    protected final int window;

    public RollingColumn(Column<?> column, int window) {
        this.column = column;
        this.window = window;
    }

    protected String generateNewColumnName(AggregateFunction<?, ?> function) {
        return new StringBuilder(column.name())
                .append(" ").append(window).append("-period")
                .append(" ").append(function.functionName())
                .toString();
    }

    @SuppressWarnings({"unchecked"})
    public <INCOL extends Column<?>,OUT> Column<?> calc(AggregateFunction<INCOL,OUT> function) {
        // TODO: the subset operation copies the array. creating a view would likely be more efficient
        Column<?> result = function.returnType().create(generateNewColumnName(function));
        for (int i = 0; i < window - 1; i++) {
            result.appendMissing();
        }
        for (int origColIndex = 0; origColIndex < column.size() - window + 1; origColIndex++) {
            Selection selection = new BitmapBackedSelection();
            selection.addRange(origColIndex, origColIndex + window);
            INCOL subsetCol = (INCOL) column.subset(selection.toArray());
            OUT answer = function.summarize(subsetCol);
            if (answer instanceof Number) {
                Number number = (Number) answer;
                ((DoubleColumn) result).append(number.doubleValue());
            } else {
                result.appendObj(answer);
            }
        }
        return result;
    }

}
