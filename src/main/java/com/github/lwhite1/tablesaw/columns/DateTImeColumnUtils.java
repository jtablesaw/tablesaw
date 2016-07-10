package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.api.DateTimeColumn;
import com.github.lwhite1.tablesaw.filtering.LongPredicate;
import it.unimi.dsi.fastutil.longs.LongArrayList;

/**
 *
 */
public interface DateTImeColumnUtils extends Column {

  LongArrayList data();

  LongPredicate isMissing = i -> i == DateTimeColumn.MISSING_VALUE;

  LongPredicate isNotMissing = i -> i != DateTimeColumn.MISSING_VALUE;
}
