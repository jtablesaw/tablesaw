package tech.tablesaw.filtering.times;

import java.time.LocalTime;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.filtering.ColumnFilter;
import tech.tablesaw.util.Selection;

public class IsBefore extends ColumnFilter {

    private LocalTime value;

    public IsBefore(ColumnReference reference, LocalTime value) {
        super(reference);
        this.value = value;
    }

    public Selection apply(Table relation) {
        TimeColumn timeColumn = (TimeColumn) relation.column(columnReference().getColumnName());
        return timeColumn.isBefore(value);
    }
}
