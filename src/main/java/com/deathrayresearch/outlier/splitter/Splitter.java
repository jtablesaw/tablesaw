package com.deathrayresearch.outlier.splitter;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.View;

import java.util.List;

/**
 *
 */
public interface Splitter {

  List<View> split(Table t);
}
