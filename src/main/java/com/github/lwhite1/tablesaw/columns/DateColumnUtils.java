package com.github.lwhite1.tablesaw.columns;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterable;

/**
 *
 */
public interface DateColumnUtils extends Column, IntIterable {

  IntArrayList data();
}
