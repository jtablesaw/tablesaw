package com.github.lwhite1.outlier.util;

import com.github.lwhite1.outlier.Table;
import com.github.lwhite1.outlier.columns.CategoryColumn;
import com.github.lwhite1.outlier.columns.FloatColumn;
import org.apache.commons.math3.util.FastMath;

/**
 *
 */
public class IntStats {

  long n;
  long missing;
  long sum;
  long median;
  long min;
  long max;
  double variance;
  double kurtosis;
  double skewness;

  long range() {
    return max - min;
  }

  float stdDev() {
    float stdDev = Float.NaN;
    if (n > 0) {
      if (n > 1) {
        stdDev = (float) FastMath.sqrt(variance);
      } else {
        stdDev = 0.0f;
      }
    }
    return stdDev;
  }

  public long n() {
    return n;
  }

  public double mean() {
    return sum / (double) n;
  }

  public long median() {
    return median;
  }

  public long min() {
    return min;
  }

  public long max() {
    return max;
  }

  public long sum() {
    return sum;
  }

  public double variance() {
    return variance;
  }

  public double kurtosis() {
    return kurtosis;
  }

  public double skewness() {
    return skewness;
  }

  public long missing() {
    return missing;
  }

  public String printString() {
    StringBuilder buffer = new StringBuilder();
    buffer.append("Descriptive Stats \n");
    buffer.append("n: ");
    buffer.append(n);
    buffer.append('\n');
    buffer.append("missing: ");
    buffer.append(missing());
    buffer.append('\n');
    buffer.append("min: ");
    buffer.append(min());
    buffer.append('\n');
    buffer.append("max: ");
    buffer.append(max());
    buffer.append('\n');
    buffer.append("range: ");
    buffer.append(range());
    buffer.append('\n');
    buffer.append("sum: ");
    buffer.append(sum);
    buffer.append('\n');
    buffer.append("mean: ");
    buffer.append(mean());
    buffer.append('\n');
    buffer.append("std.dev: ");
    buffer.append(stdDev());
    buffer.append('\n');
    buffer.append("variance: ");
    buffer.append(variance);
    buffer.append('\n');
    return buffer.toString();
  }

  public Table asTable(String name) {
    Table t = new Table(name);
    CategoryColumn measure = CategoryColumn.create("Measure");
    FloatColumn value = FloatColumn.create("Value");
    t.addColumn(measure);
    t.addColumn(value);

    measure.add("n");
    value.add(n);

    measure.add("Missing");
    value.add(missing);

    measure.add("Mean");
    value.add((float) mean());

    measure.add("Min");
    value.add(min);

    measure.add("Max");
    value.add(max);

    measure.add("Range");
    value.add(range());

    measure.add("Std. Dev");
    value.add(stdDev());

    return t;
  }
}
