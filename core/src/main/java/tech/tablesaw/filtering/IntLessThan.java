package tech.tablesaw.filtering;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

/**
 */
public class IntLessThan extends ColumnFilter {

    private int value;

    public IntLessThan(ColumnReference reference, int value) {
        super(reference);
        this.value = value;
    }

    public Selection apply(Table relation) {
        String name = columnReference.getColumnName();
        Column column = relation.column(name);
        ColumnType type = column.type();
        switch (type) {
            case INTEGER:
                IntColumn intColumn = relation.intColumn(name);
                return intColumn.isLessThan(value);
            case LONG_INT:
                LongColumn longColumn = relation.longColumn(name);
                return longColumn.isLessThan(value);
            case SHORT_INT:
                ShortColumn shortColumn = relation.shortColumn(name);
                return shortColumn.isLessThan(value);
            default:
                throw new UnsupportedOperationException("Columns of type " + type.name() + " do not support the operation "
                        + "lessThan(anInt) ");
        }
    }
}
