package tech.tablesaw.columns;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.filtering.StringPredicate;
import tech.tablesaw.mapping.StringMapUtils;
import tech.tablesaw.reducing.CategoryReduceUtils;
import tech.tablesaw.util.DictionaryMap;

/**
 *
 */
public interface CategoryColumnUtils extends Column, StringMapUtils, CategoryReduceUtils, Iterable<String> {

    StringPredicate isMissing = i -> i.equals(CategoryColumn.MISSING_VALUE);
    StringPredicate isNotMissing = i -> !i.equals(CategoryColumn.MISSING_VALUE);

    DictionaryMap dictionaryMap();

    IntArrayList values();
}
