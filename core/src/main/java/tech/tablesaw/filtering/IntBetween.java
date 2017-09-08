package tech.tablesaw.filtering;

import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

public class IntBetween extends ColumnFilter {

    private final int low;
    private final int high;

    public IntBetween(ColumnReference reference, int lowValue, int highValue) {
        super(reference);
        this.low = lowValue;
        this.high = highValue;
    }

    public Selection apply(Table relation) {
        IntColumn intColumn = (IntColumn) relation.column(columnReference.getColumnName());
        Selection matches = intColumn.isGreaterThan(low);
        matches.toBitmap().and(intColumn.isLessThan(high).toBitmap());
        return matches;
    }
}
