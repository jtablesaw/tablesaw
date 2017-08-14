package tech.tablesaw.filtering;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntSet;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.util.Selection;

/**
 */
public class IntIsIn extends ColumnFilter {

    private IntColumn filterColumn;

    public IntIsIn(ColumnReference reference, IntColumn filterColumn) {
        super(reference);
        this.filterColumn = filterColumn;
    }

    public IntIsIn(ColumnReference reference, int... ints) {
        super(reference);
        this.filterColumn = new IntColumn("temp", new IntArrayList(ints));
    }

    public Selection apply(Table relation) {
        IntColumn intColumn = (IntColumn) relation.column(columnReference.getColumnName());
        IntSet firstSet = intColumn.asSet();
        firstSet.retainAll(filterColumn.data());
        return intColumn.select(firstSet::contains);
    }
}
