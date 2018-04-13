package tech.tablesaw.filtering;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

public interface Filter {

    Selection apply(Table relation);

    /**
     * Returns a selection created by applying this filter (and any optional params added in extending classes) to the
     * given column
     * @param columnBeingFiltered
     * @return
     */
    Selection apply(Column columnBeingFiltered);
}
