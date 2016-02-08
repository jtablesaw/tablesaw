package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.columns.ColumnReference;

/**
 */
public class QueryUtil {

  public static ColumnReference valueOf(String columnName) {
    return new ColumnReference(columnName);
  }
}
