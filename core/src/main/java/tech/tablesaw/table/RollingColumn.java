package tech.tablesaw.table;

import org.apache.commons.lang3.StringUtils;
import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.AbstractColumn;
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

    private String generateNewColumnName(AggregateFunction function) {
        boolean useSpaces = column.name().matches("\\s+");
        String separator = useSpaces ? " " : "";
        return new StringBuilder(column.name())
                .append(separator).append(useSpaces ? function.functionName() : StringUtils.capitalize(function.functionName()))
                .append(separator).append(window)
                .toString();
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    public Column calc(AggregateFunction function) {
        // TODO: the subset operation copies the array. creating a view would likely be more efficient
        AbstractColumn result = (AbstractColumn) Column.create(generateNewColumnName(function), function.returnType());
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
