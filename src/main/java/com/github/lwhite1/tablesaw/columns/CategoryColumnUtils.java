package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.filtering.StringPredicate;
import com.github.lwhite1.tablesaw.mapping.StringMapUtils;
import com.github.lwhite1.tablesaw.reducing.CategoryReduceUtils;
import com.github.lwhite1.tablesaw.util.DictionaryMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 *
 */
public interface CategoryColumnUtils extends Column, StringMapUtils, CategoryReduceUtils, Iterable<String> {

  StringPredicate isMissing = i -> i.equals(CategoryColumn.MISSING_VALUE);
  StringPredicate isNotMissing = i -> !i.equals(CategoryColumn.MISSING_VALUE);

  DictionaryMap dictionaryMap();

  IntArrayList values();
}
