package com.deathrayresearch.outlier;

/**
 */
public class QueryUtil {

  public static ColumnReference valueOf(String columnName) {
    return new ColumnReference(columnName);
  }
}
