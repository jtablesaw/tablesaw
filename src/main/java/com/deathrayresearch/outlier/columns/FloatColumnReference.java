package com.deathrayresearch.outlier.columns;

/**
 *
 */
public class FloatColumnReference {

  private String columnName;

  private FloatColumnReference(String columnName) {
    this.columnName = columnName;
  }

  public static FloatColumnReference col(String columnName) {
    return new FloatColumnReference(columnName);
  }

  public static FloatColumnReference column(String columnName) {
    return FloatColumnReference.col(columnName);
  }

  public String getColumnName() {
    return columnName;
  }
}
