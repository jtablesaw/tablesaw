package tech.tablesaw.filtering;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

/**
 *
 */
public class DoubleGreaterThanOrEqualTo extends ColumnFilter {

    private double value;

    public DoubleGreaterThanOrEqualTo(ColumnReference reference, double value) {
        super(reference);
        this.value = value;
    }

    public Selection apply(Table relation) {
        DoubleColumn doubleColumn = (DoubleColumn) relation.column(columnReference.getColumnName());
        return doubleColumn.isGreaterThanOrEqualTo(value);
    }
}
