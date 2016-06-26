package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.filter.StringPredicate;

/**
 *
 */
public interface CategoryColumnUtils extends Column, Iterable<String> {

  StringPredicate isMissing = i -> i.equals(CategoryColumn.MISSING_VALUE);
  StringPredicate isNotMissing = i -> ! i.equals(CategoryColumn.MISSING_VALUE);

}
