package tech.tablesaw.filtering;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

/**
 * A filtering that matches all missing values in a column
 */
public class IsMissing extends ColumnFilter {

    public IsMissing(ColumnReference reference) {
        super(reference);
    }

    @Override
    public Selection apply(Table relation) {
        Column column = relation.column(columnReference.getColumnName());
        return column.isMissing();
    }
}
