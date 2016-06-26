package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.filter.IntPredicate;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterable;

/**
 *
 */
public interface TimeColumnUtils extends Column, IntIterable {

  IntArrayList data();

  IntPredicate isMissing = i -> i == TimeColumn.MISSING_VALUE;

  IntPredicate isNotMissing = i -> i != TimeColumn.MISSING_VALUE;
}
