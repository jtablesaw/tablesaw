package com.deathrayresearch.outlier.filter;

/**
 *
 */
abstract class AbstractColumnFilter {

  int columnNumber;

  AbstractColumnFilter(int columnNumber) {
    this.columnNumber = columnNumber;
  }

}
