package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.filter.GreaterThan;

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

  public GreaterThan isGreaterThan(float f) {
    return new GreaterThan(this, f);
  }

  public String getColumnName() {
    return columnName;
  }
}
