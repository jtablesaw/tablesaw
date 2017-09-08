package tech.tablesaw.filtering;

import java.time.LocalDateTime;

import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

public class DateTimeEqualTo extends ColumnFilter {

    final LocalDateTime value;

    public DateTimeEqualTo(ColumnReference reference, LocalDateTime value) {
        super(reference);
        this.value = value;
    }

    public Selection apply(Table relation) {
        DateTimeColumn dateColumn = (DateTimeColumn) relation.column(columnReference.getColumnName());
        return dateColumn.isEqualTo(value);
    }
}
