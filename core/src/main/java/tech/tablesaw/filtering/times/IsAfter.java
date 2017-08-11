package tech.tablesaw.filtering.times;

import java.time.LocalTime;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.filtering.ColumnFilter;
import tech.tablesaw.util.Selection;

/**
 *
 */
public class IsAfter extends ColumnFilter {

    private LocalTime value;

    public IsAfter(ColumnReference reference, LocalTime value) {
        super(reference);
        this.value = value;
    }

    public Selection apply(Table relation) {
        TimeColumn timeColumn = (TimeColumn) relation.column(columnReference().getColumnName());
        return timeColumn.isAfter(value);
    }
}
