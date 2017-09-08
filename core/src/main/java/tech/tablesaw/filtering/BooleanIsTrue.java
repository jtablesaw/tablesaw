package tech.tablesaw.filtering;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

public class BooleanIsTrue extends ColumnFilter {

    public BooleanIsTrue(ColumnReference reference) {
        super(reference);
    }

    public Selection apply(Table relation) {
        BooleanColumn booleanColumn = (BooleanColumn) relation.column(columnReference.getColumnName());
        return booleanColumn.isTrue();
    }
}