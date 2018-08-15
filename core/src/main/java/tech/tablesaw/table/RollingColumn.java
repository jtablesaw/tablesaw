package tech.tablesaw.table;

import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.util.StringUtils;

/**
 * Does a calculation on a rolling basis (e.g. mean for last 20 days)
 */
public class RollingColumn {

    protected final Column column;
    protected final int window;

    public RollingColumn(Column column, int window) {
        this.column = column;
        this.window = window;
    }

    protected String generateNewColumnName(AggregateFunction<?, ?> function) {
        boolean useSpaces = column.name().matches("\\s+");
        String separator = useSpaces ? " " : "";
        return new StringBuilder(column.name())
                .append(separator).append(useSpaces ? function.functionName() : StringUtils.capitalize(function.functionName()))
                .append(separator).append(window)
                .toString();
    }

    @SuppressWarnings("unchecked")
    public Column<?> calc(AggregateFunction function) {
        // TODO: the subset operation copies the array. creating a view would likely be more efficient
        Column result = function.returnType().create(generateNewColumnName(function));
        for (int i = 0; i < window - 1; i++) {
            result.appendMissing();
        }
        for (int origColIndex = 0; origColIndex < column.size() - window + 1; origColIndex++) {
            Selection selection = new BitmapBackedSelection();
            selection.addRange(origColIndex, origColIndex + window);
            Object answer = function.summarize(column.subset(selection));
            if (answer instanceof Number) {
                Number number = (Number) answer;
                result.append(number.doubleValue());
            } else {
                result.append(answer);
            }
        }
        return result;
    }

}
