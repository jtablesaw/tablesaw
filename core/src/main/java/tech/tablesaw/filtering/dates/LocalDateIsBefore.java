package tech.tablesaw.filtering.dates;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.filtering.ColumnFilter;
import tech.tablesaw.util.Selection;

public class LocalDateIsBefore extends ColumnFilter {

    private final int value;

    public LocalDateIsBefore(ColumnReference reference, int value) {
        super(reference);
        this.value = value;
    }

    @Override
    public Selection apply(Table relation) {

        DateColumn dateColumn = (DateColumn) relation.column(columnReference().getColumnName());
        return dateColumn.isBefore(value);
    }
}
