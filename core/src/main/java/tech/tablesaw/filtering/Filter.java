package tech.tablesaw.filtering;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;

public interface Filter {

    Selection apply(Table relation);

    Selection apply(Column<?> column);
}
