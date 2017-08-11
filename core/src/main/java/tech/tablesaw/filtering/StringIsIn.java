package tech.tablesaw.filtering;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Lists;

import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

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
