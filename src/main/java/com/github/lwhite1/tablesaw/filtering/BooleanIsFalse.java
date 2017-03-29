package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.BooleanColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.util.Selection;

/**
 *
 */
public class BooleanIsFalse extends ColumnFilter {

    public BooleanIsFalse(ColumnReference reference) {
        super(reference);
    }

    public Selection apply(Table relation) {
        BooleanColumn booleanColumn = (BooleanColumn) relation.column(columnReference.getColumnName());
        return booleanColumn.isFalse();
    }
}