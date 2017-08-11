package tech.tablesaw.filtering.datetimes;

import java.time.LocalDateTime;

import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.filtering.ColumnFilter;
import tech.tablesaw.util.Selection;

/**
 *
 */
public class DateTimeIsBefore extends ColumnFilter {

    private LocalDateTime value;

    public DateTimeIsBefore(ColumnReference reference, LocalDateTime value) {
        super(reference);
        this.value = value;
    }

    @Override
    public Selection apply(Table relation) {

        DateTimeColumn dateColumn = (DateTimeColumn) relation.column(columnReference().getColumnName());
        return dateColumn.isBefore(value);
    }
}
