package tech.tablesaw.filtering.text;

import javax.annotation.concurrent.Immutable;

import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.filtering.ColumnFilter;
import tech.tablesaw.util.Selection;

/**
 * A filtering that selects cells in which the string value is in the given array of strings
 */
@Immutable
public class TextIsIn extends ColumnFilter {

    private String[] strings;

    public TextIsIn(ColumnReference reference, String... strings) {
        super(reference);
        this.strings = strings;
    }

    @Override
    public Selection apply(Table relation) {
        Column column = relation.column(columnReference().getColumnName());
        CategoryColumn textColumn = (CategoryColumn) column;
        return textColumn.isIn(strings);
    }
}
