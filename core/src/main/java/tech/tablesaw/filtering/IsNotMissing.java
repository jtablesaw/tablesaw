package tech.tablesaw.filtering;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

/**
 * A filtering that matches all non-missing values in a column
 */
public class IsNotMissing extends ColumnFilter {

    public IsNotMissing(ColumnReference reference) {
        super(reference);
    }

    public Selection apply(Table relation) {
        Column column = relation.column(columnReference.getColumnName());
        return column.isNotMissing();
    }
}
