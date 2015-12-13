package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.Relation;

/**
 *
 */
public interface Filter {

  AbstractColumnFilter asColumnFilter(Relation t);
}
