package com.github.lwhite1.tablesaw.filtering;

import java.util.Collection;
import java.util.Set;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.ColumnReference;
import com.github.lwhite1.tablesaw.util.Selection;
import com.google.common.collect.Lists;

/**
 * Implements EqualTo testing for Category and Text Columns
 */
public class StringIsIn extends ColumnFilter {

    private CategoryColumn filterColumn;

    public StringIsIn(ColumnReference reference, CategoryColumn filterColumn) {
        super(reference);
        this.filterColumn = filterColumn;
    }

    public StringIsIn(ColumnReference reference, Collection<String> strings) {
      super(reference);
      this.filterColumn = CategoryColumn.create("temp", Lists.newArrayList(strings));
    }
 
    public StringIsIn(ColumnReference reference, String... strings) {
        super(reference);
        this.filterColumn = CategoryColumn.create("temp", Lists.newArrayList(strings));
    }

    public Selection apply(Table relation) {
        CategoryColumn categoryColumn = (CategoryColumn) relation.column(columnReference.getColumnName());
        Set<String> firstSet = categoryColumn.asSet();
        firstSet.retainAll(filterColumn.data());
        return categoryColumn.select(firstSet::contains);
    }
}
