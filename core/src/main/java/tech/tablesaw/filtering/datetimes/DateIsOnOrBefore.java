package tech.tablesaw.filtering.datetimes;


import javax.annotation.concurrent.Immutable;

import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.filtering.ColumnFilter;
import tech.tablesaw.util.Selection;

/**
 *
 */
@Immutable
public class DateIsOnOrBefore extends ColumnFilter {

    private int value;

    public DateIsOnOrBefore(ColumnReference reference, int value) {
        super(reference);
        this.value = value;
    }

    @Override
    public Selection apply(Table relation) {

        DateTimeColumn dateColumn = (DateTimeColumn) relation.column(columnReference().getColumnName());
        return dateColumn.isOnOrBefore(value);
    }
}
