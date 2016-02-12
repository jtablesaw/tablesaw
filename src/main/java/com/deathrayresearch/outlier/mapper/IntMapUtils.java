package com.deathrayresearch.outlier.mapper;

import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.IntColumn;

/**
 *
 */
public class IntMapUtils {

  public IntColumn plus(IntColumn ... columns) {

    // TODO(lwhite): Assert all columns are the same size.
    String nString = names(columns);
    String name = String.format("sum(%s)", nString);
    IntColumn newColumn = IntColumn.create(name);

    for (int r = 0; r < columns[0].size(); r++) {
      int result = 0;
      for (IntColumn column : columns) {
        result = result + column.get(r);
      }
      newColumn.add(result);
    }
    return newColumn;
  }

  // TODO(lwhite): make this a shared utility
  private String names(IntColumn[] columns) {
    StringBuilder builder = new StringBuilder();
    int count = 0;
    for (Column column: columns) {
      builder.append(column.name());
      if (count < columns.length - 1) {
        builder.append(", ");
      }
      count++;
    }
    return builder.toString();
  }
}
