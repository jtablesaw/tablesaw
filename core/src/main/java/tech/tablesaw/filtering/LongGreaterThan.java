package tech.tablesaw.filtering;

import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

/**
 *
 */
public class LongGreaterThan extends ColumnFilter {

    private long value;

    public LongGreaterThan(ColumnReference reference, long value) {
        super(reference);
        this.value = value;
    }

    public Selection apply(Table relation) {
        LongColumn longColumn = (LongColumn) relation.column(columnReference.getColumnName());
        return longColumn.isGreaterThan(value);
    }
}
