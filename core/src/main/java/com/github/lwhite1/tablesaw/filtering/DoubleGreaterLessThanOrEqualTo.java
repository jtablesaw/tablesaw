package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.DoubleColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.util.Selection;

/**
 *
 */
public class DoubleGreaterLessThanOrEqualTo extends ColumnFilter {

    private double value;

    public DoubleGreaterLessThanOrEqualTo(ColumnReference reference, double value) {
        super(reference);
        this.value = value;
    }

    public Selection apply(Table relation) {
        DoubleColumn doubleColumn = (DoubleColumn) relation.column(columnReference.getColumnName());
        return doubleColumn.isLessThanOrEqualTo(value);
    }
}
