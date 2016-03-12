package com.deathrayresearch.outlier.mapper;

import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.FloatColumn;
import com.deathrayresearch.outlier.columns.IntColumn;

/**
 *
 */
public interface IntMapUtils extends Column {

  default IntColumn plus(IntColumn ... columns) {

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
  default String names(IntColumn[] columns) {
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

  /**
   * Return the elements of this column expressed as ratios of their value and the sum of all
   * elements
   */
  default FloatColumn asRatio() {
    FloatColumn pctColumn = new FloatColumn(name() + " percents");
    float total = sum();
    reset();
    // TODO(lwhite): Handle div by 0 value total
    while (hasNext()) {
      pctColumn.add((float) next() / total);
    }
    return pctColumn;
  }

  int sum();

  int next();

  void reset();
}
