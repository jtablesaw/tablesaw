package tech.tablesaw.filtering.times;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.filtering.ColumnFilter;
import tech.tablesaw.util.Selection;

/**
 *
 */
public class IsBeforeNoon extends ColumnFilter {

    public IsBeforeNoon(ColumnReference reference) {
        super(reference);
    }

    @Override
    public Selection apply(Table relation) {

        String name = columnReference().getColumnName();
        Column column = relation.column(name);
        ColumnType type = column.type();
        switch (type) {
            case LOCAL_TIME:
                TimeColumn timeColumn = relation.timeColumn(name);
                return timeColumn.isBeforeNoon();
            case LOCAL_DATE_TIME:
                DateTimeColumn dateTimeColumn = relation.dateTimeColumn(name);
                return dateTimeColumn.isBeforeNoon();
            default:
                throw new UnsupportedOperationException("Columns of type " + type.name() + " do not support the operation "
                        + "isBeforeNoon() ");
        }
    }
}
