package com.deathrayresearch.outlier.filter;

import com.deathrayresearch.outlier.FloatColumnReference;
import com.deathrayresearch.outlier.Relation;

/**
 *
 */
public class GreaterThan implements Filter {

  final String columnName;
  final float constant;

  public GreaterThan(FloatColumnReference columnReference, float constant) {
    this.columnName = columnReference.getColumnName();
    this.constant = constant;
  }

  @Override
  public AbstractColumnFilter asColumnFilter(Relation t) {
    int columnIndex = t.columnIndex(columnName);
    return new GreaterThanFilter(columnIndex, constant);
  }
}
