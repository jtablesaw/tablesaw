package com.deathrayresearch.outlier.splitter;


import com.deathrayresearch.outlier.columns.ColumnReference;

/**
 *
 */
public abstract class AbstractSplitter implements Splitter {

  private ColumnReference columnReference;

  public AbstractSplitter(ColumnReference columnReference) {
    this.columnReference = columnReference;
  }

  public void setColumnReference(ColumnReference columnReference) {
    this.columnReference = columnReference;
  }

  public AbstractSplitter() {}

  public String columnName() {
    return columnReference.getColumnName();
  }

  public ColumnReference getColumnReference() {
    return columnReference;
  }
}
