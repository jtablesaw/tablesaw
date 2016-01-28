package com.deathrayresearch.outlier.util;

import org.apache.commons.math3.util.FastMath;

/**
 *
 */
public class Stats {

  int n;
  int missing;
  double mean;
  float median;
  float min;
  float max;
  double variance;
  double kurtosis;
  double skewness;


  float range() {
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

  public int n() {
    return n;
  }

  public double mean() {
    return mean;
  }

  public float median() {
    return median;
  }

  public float min() {
    return min;
  }

  public float max() {
    return max;
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

  public int missing() {
    return missing;
  }

  public String printString() {
    StringBuffer buffer = new StringBuffer();
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
    buffer.append("mean: ");
    buffer.append(mean);
    buffer.append('\n');
    buffer.append("std.dev: ");
    buffer.append(stdDev());
    buffer.append('\n');
    buffer.append("variance: ");
    buffer.append(variance);
    buffer.append('\n');
    return buffer.toString();
  }
}
