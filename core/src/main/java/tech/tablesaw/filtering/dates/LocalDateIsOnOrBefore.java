package tech.tablesaw.filtering.dates;

import javax.annotation.concurrent.Immutable;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.filtering.ColumnFilter;
import tech.tablesaw.util.Selection;

@Immutable
public class LocalDateIsOnOrBefore extends ColumnFilter {

    private final int value;

    public LocalDateIsOnOrBefore(ColumnReference reference, int value) {
        super(reference);
        this.value = value;
    }

    @Override
    public Selection apply(Table relation) {

        DateColumn dateColumn = (DateColumn) relation.column(columnReference().getColumnName());
        return dateColumn.isOnOrBefore(value);
    }
}
