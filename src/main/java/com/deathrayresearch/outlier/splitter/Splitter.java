package com.deathrayresearch.outlier.splitter;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.TableGroup;

/**
 *
 */
public interface Splitter {

  TableGroup split(Table t);

  
}
