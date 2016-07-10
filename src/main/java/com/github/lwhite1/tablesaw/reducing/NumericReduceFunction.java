package com.github.lwhite1.tablesaw.reducing;

import com.github.lwhite1.tablesaw.api.FloatColumn;
import com.github.lwhite1.tablesaw.api.IntColumn;
import com.github.lwhite1.tablesaw.api.LongColumn;
import com.github.lwhite1.tablesaw.api.ShortColumn;

/**
 * Functions that calculate values over the data of an entire column, such as sum, mean, std. dev, etc.
 */
public interface NumericReduceFunction {

  String functionName();

  double reduce(double[] data);

  default double reduce(FloatColumn data) {
    return this.reduce(data.toDoubleArray());
  }

  default double reduce(IntColumn data) {
    return this.reduce(data.toDoubleArray());
  }

  default double reduce(ShortColumn data) {
    return this.reduce(data.toDoubleArray());
  }

  default double reduce(LongColumn data) {
    return this.reduce(data.toDoubleArray());
  }
}
