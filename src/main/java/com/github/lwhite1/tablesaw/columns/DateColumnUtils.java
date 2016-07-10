package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.api.DateColumn;
import com.github.lwhite1.tablesaw.filtering.IntPredicate;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.time.LocalDate;

/**
 *
 */
public interface DateColumnUtils extends Column, Iterable<LocalDate> {

  IntArrayList data();

  IntPredicate isMissing = i -> i == DateColumn.MISSING_VALUE;

  IntPredicate isNotMissing = i -> i != DateColumn.MISSING_VALUE;
}
