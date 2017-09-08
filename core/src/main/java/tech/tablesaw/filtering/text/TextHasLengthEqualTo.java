package tech.tablesaw.filtering.text;


import javax.annotation.concurrent.Immutable;

import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.filtering.ColumnFilter;
import tech.tablesaw.util.Selection;

/**
 * A filtering that selects cells whose length equals the given length
 */
@Immutable
public class TextHasLengthEqualTo extends ColumnFilter {

    private int length;

    public TextHasLengthEqualTo(ColumnReference reference, int length) {
        super(reference);
        this.length = length;
    }

    @Override
    public Selection apply(Table relation) {
        Column column = relation.column(columnReference().getColumnName());
        CategoryColumn textColumn = (CategoryColumn) column;
        return textColumn.hasLengthEqualTo(length);
    }
}
