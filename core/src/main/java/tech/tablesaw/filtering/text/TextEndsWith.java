package tech.tablesaw.filtering.text;

import javax.annotation.concurrent.Immutable;

import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.filtering.ColumnFilter;
import tech.tablesaw.util.Selection;

/**
 * A filtering that selects cells which end with the given text
 */
@Immutable
public class TextEndsWith extends ColumnFilter {

    private String string;

    public TextEndsWith(ColumnReference reference, String string) {
        super(reference);
        this.string = string;
    }

    @Override
    public Selection apply(Table relation) {
        Column column = relation.column(columnReference().getColumnName());
        CategoryColumn textColumn = (CategoryColumn) column;
        return textColumn.endsWith(string);
    }
}
