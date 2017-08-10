package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.LongColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.util.Selection;

/**
 *
 */
public class LongEqualTo extends ColumnFilter {

    private long value;

    public LongEqualTo(ColumnReference reference, long value) {
        super(reference);
        this.value = value;
    }

    public Selection apply(Table relation) {
        LongColumn longColumn = (LongColumn) relation.column(columnReference.getColumnName());
        return longColumn.isEqualTo(value);
    }
}
