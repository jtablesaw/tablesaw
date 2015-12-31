package com.deathrayresearch.outlier.filter;

/**
 *
 */
public abstract class AbstractColumnFilter {

  int columnNumber;


  AbstractColumnFilter(int columnNumber) {
    this.columnNumber = columnNumber;
  }

  abstract boolean matches(int rowNumber);

}
