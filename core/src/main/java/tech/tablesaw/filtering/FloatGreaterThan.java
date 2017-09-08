package tech.tablesaw.filtering;

import static tech.tablesaw.columns.FloatColumnUtils.isGreaterThan;

import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

public class FloatGreaterThan extends ColumnFilter {

    private final float value;

    public FloatGreaterThan(ColumnReference reference, float value) {
        super(reference);
        this.value = value;
    }

    public Selection apply(Table relation) {
        FloatColumn floatColumn = (FloatColumn) relation.column(columnReference.getColumnName());
        return floatColumn.select(isGreaterThan, value);
    }
}
