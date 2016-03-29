package com.deathrayresearch.outlier.splitter;

/**
 * Splits a table on the unique values in the given column (or combination of values if more than one column is given).
 */
public class ColumnSplitter implements Splitter {

  private String[] splitColumnNames;


  public ColumnSplitter(String[] splitColumnNames) {
    this.splitColumnNames = splitColumnNames;
  }


  /**
   * When applied to a record in a table, returns a String used to group records
   */
  @Override
  public String groupKey() {
    return null;
  }
}
