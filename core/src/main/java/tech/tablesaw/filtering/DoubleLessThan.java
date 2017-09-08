package tech.tablesaw.filtering;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

public class DoubleLessThan extends ColumnFilter {

    private final double value;

    public DoubleLessThan(ColumnReference reference, double value) {
        super(reference);
        this.value = value;
    }

    public Selection apply(Table relation) {
        DoubleColumn doubleColumn = (DoubleColumn) relation.column(columnReference.getColumnName());
        return doubleColumn.isLessThan(value);
    }
}
