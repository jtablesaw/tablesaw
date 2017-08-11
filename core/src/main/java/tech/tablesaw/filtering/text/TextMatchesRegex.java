package tech.tablesaw.filtering.text;

import javax.annotation.concurrent.Immutable;

import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.filtering.ColumnFilter;
import tech.tablesaw.util.Selection;

/**
 * A filtering that selects cells in which all text is uppercase
 */
@Immutable
public class TextMatchesRegex extends ColumnFilter {

    private String string;

    public TextMatchesRegex(ColumnReference reference, String string) {
        super(reference);
        this.string = string;
    }

    @Override
    public Selection apply(Table relation) {

        Column column = relation.column(columnReference().getColumnName());
        CategoryColumn textColumn = (CategoryColumn) column;
        return textColumn.matchesRegex(string);
    }
}
