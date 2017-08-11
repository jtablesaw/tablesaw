package tech.tablesaw.filtering;

import java.time.LocalTime;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

/**
 */
public class TimeEqualTo extends ColumnFilter {

    LocalTime value;

    public TimeEqualTo(ColumnReference reference, LocalTime value) {
        super(reference);
        this.value = value;
    }

    public Selection apply(Table relation) {
        TimeColumn dateColumn = (TimeColumn) relation.column(columnReference.getColumnName());
        return dateColumn.isEqualTo(value);
    }
}
