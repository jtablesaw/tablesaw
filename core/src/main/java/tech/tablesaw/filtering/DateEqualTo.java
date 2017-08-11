package tech.tablesaw.filtering;

import java.time.LocalDate;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

/**
 */
public class DateEqualTo extends ColumnFilter {

    LocalDate value;

    public DateEqualTo(ColumnReference reference, LocalDate value) {
        super(reference);
        this.value = value;
    }

    public Selection apply(Table relation) {
        DateColumn dateColumn = (DateColumn) relation.column(columnReference.getColumnName());
        return dateColumn.isEqualTo(value);
    }
}
