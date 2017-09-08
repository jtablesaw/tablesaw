package tech.tablesaw.filtering;

import tech.tablesaw.columns.ColumnReference;

public abstract class ColumnFilter extends Filter {

    final ColumnReference columnReference;

    public ColumnFilter(ColumnReference columnReference) {
        this.columnReference = columnReference;
    }

    public ColumnReference columnReference() {
        return columnReference;
    }

    public ColumnReference getColumnReference() {
        return columnReference;
    }

}
