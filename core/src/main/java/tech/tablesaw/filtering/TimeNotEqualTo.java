package tech.tablesaw.filtering;

import java.time.LocalTime;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

/**
 */
public class TimeNotEqualTo extends ColumnFilter {

    LocalTime value;

    public TimeNotEqualTo(ColumnReference reference, LocalTime value) {
        super(reference);
        this.value = value;
    }

    public Selection apply(Table relation) {
        TimeColumn dateColumn = (TimeColumn) relation.column(columnReference.getColumnName());
        return dateColumn.isNotEqualTo(value);
    }
}
