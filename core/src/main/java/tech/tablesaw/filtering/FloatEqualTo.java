package tech.tablesaw.filtering;

import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

/**
 *
 */
public class FloatEqualTo extends ColumnFilter {

    private float value;

    public FloatEqualTo(ColumnReference reference, float value) {
        super(reference);
        this.value = value;
    }

    public Selection apply(Table relation) {
        FloatColumn floatColumn = (FloatColumn) relation.column(columnReference.getColumnName());
        return floatColumn.isEqualTo(value);
    }
}
