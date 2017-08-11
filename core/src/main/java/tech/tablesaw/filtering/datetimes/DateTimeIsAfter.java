package tech.tablesaw.filtering.datetimes;


import javax.annotation.concurrent.Immutable;

import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.filtering.ColumnFilter;
import tech.tablesaw.util.Selection;

import java.time.LocalDateTime;

/**
 *
 */
@Immutable
public class DateTimeIsAfter extends ColumnFilter {

    private LocalDateTime value;

    public DateTimeIsAfter(ColumnReference reference, LocalDateTime value) {
        super(reference);
        this.value = value;
    }

    @Override
    public Selection apply(Table relation) {

        DateTimeColumn dateColumn = relation.dateTimeColumn(columnReference().getColumnName());
        return dateColumn.isAfter(value);
    }
}
