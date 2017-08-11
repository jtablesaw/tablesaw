package tech.tablesaw.filtering;

import java.time.LocalDate;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

/**
 */
public class LocalDateBetween extends ColumnFilter {
    private LocalDate low;
    private LocalDate high;

    public LocalDateBetween(ColumnReference reference, LocalDate lowValue, LocalDate highValue) {
        super(reference);
        this.low = lowValue;
        this.high = highValue;
    }

    public Selection apply(Table relation) {
        DateColumn column = (DateColumn) relation.column(columnReference.getColumnName());
        Selection matches = column.isAfter(low);
        matches.and(column.isBefore(high));
        return matches;
    }
}
