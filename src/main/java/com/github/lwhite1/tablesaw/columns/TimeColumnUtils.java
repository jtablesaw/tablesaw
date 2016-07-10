package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.api.TimeColumn;
import com.github.lwhite1.tablesaw.filtering.IntPredicate;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.time.LocalTime;

/**
 *
 */
public interface TimeColumnUtils extends Column, Iterable<LocalTime> {

  IntArrayList data();

  IntPredicate isMissing = i -> i == TimeColumn.MISSING_VALUE;

  IntPredicate isNotMissing = i -> i != TimeColumn.MISSING_VALUE;
}
