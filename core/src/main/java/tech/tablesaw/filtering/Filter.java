package tech.tablesaw.filtering;

import tech.tablesaw.selection.Selection;

public interface Filter {

    Selection apply(int sizeOfCollectionFiltered);
}
