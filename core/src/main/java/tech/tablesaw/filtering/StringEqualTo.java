package tech.tablesaw.filtering;

import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

/**
 * Implements EqualTo testing for Category and Text Columns
 */
public class StringEqualTo extends ColumnFilter {

    private String value;

    public StringEqualTo(ColumnReference reference, String value) {
        super(reference);
        this.value = value;
    }

    public Selection apply(Table relation) {
        Column column = relation.column(columnReference.getColumnName());
        ColumnType type = column.type();
        switch (type) {
            case CATEGORY: {
                CategoryColumn categoryColumn = (CategoryColumn) relation.column(columnReference.getColumnName());
                return categoryColumn.isEqualTo(value);
            }
            default:
                throw new UnsupportedOperationException(
                        String.format("ColumnType %s does not support equalTo on a String value", type));
        }
    }
}
