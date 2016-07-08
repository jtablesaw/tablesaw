package com.github.lwhite1.tablesaw.mapping;

import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.columns.IntColumnUtils;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.api.FloatColumn;

/**
 *
 */
public interface IntMapUtils extends IntColumnUtils {

  default IntColumn plus(IntColumn... columns) {

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
    for (Column column : columns) {
      builder.append(column.name());
      if (count < columns.length - 1) {
        builder.append(", ");
      }
      count++;
    }
    return builder.toString();
  }

  /**
   * Return the elements of this column as the ratios of their value and the sum of all
   * elements
   */
  default FloatColumn asRatio() {
    FloatColumn pctColumn = new FloatColumn(name() + " percents");
    float total = sum();
    for (int next : this) {
      if (total != 0) {
        pctColumn.add((float) next / total);
      } else {
        pctColumn.add(FloatColumn.MISSING_VALUE);
      }
    }
    return pctColumn;
  }

  /**
   * Return the elements of this column as the percentages of their value relative to the sum of all
   * elements
   */
  default FloatColumn asPercent() {
    FloatColumn pctColumn = new FloatColumn(name() + " percents");
    float total = sum();
    for (int next : this) {
      if (total != 0) {
        pctColumn.add(((float) next / total) * 100);
      } else {
        pctColumn.add(FloatColumn.MISSING_VALUE);
      }
    }
    return pctColumn;
  }

  long sum();

  int get(int index);

  default IntColumn difference(IntColumn column2) {
    IntColumn result = IntColumn.create(name() + " - " + column2.name());
    for (int r = 0; r < size(); r++) {
      result.set(r, get(r) - column2.get(r));
    }
    return result;
  }
}