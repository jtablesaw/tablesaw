package com.github.lwhite1.tablesaw.filtering;

import com.github.lwhite1.tablesaw.api.BooleanColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.util.Selection;

/**
 *
 */
public class BooleanIsTrue extends ColumnFilter {

    public BooleanIsTrue(ColumnReference reference) {
        super(reference);
    }

    public Selection apply(Table relation) {
        BooleanColumn booleanColumn = (BooleanColumn) relation.column(columnReference.getColumnName());
        return booleanColumn.isTrue();
    }
}