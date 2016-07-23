package com.github.lwhite1.tablesaw.api;

import com.github.lwhite1.tablesaw.columns.Column;

/**
 *
 */
public interface NumericColumn extends Column {

  double[] toDoubleArray();

  float getFloat(int index);

  double max();
  double min();

  double product();

  double mean();

  double median();

  double quartile1();

  double quartile3();

  double percentile(double percentile);

  double range();

}
