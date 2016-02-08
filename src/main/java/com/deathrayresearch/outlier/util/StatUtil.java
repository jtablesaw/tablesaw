package com.deathrayresearch.outlier.util;

import com.deathrayresearch.outlier.columns.FloatColumn;
import org.apache.commons.math3.util.FastMath;

/**
 *
 */
public class StatUtil {

  private StatUtil() {
  }

  public static float sum(final FloatColumn values) {
    float sum;
    sum = 0.0f;
    while (values.hasNext()) {
      float value = values.next();
      if (value != Float.NaN) {
        sum += value;
      }
    }
    return sum;
  }

  public static double product(final float[] values) {
    double product = 1.0;
    boolean empty = true;
    for (float value : values) {
      if (value != Float.NaN) {
        empty = false;
        product *= value;
      }
    }
    if (empty) {
      return Double.NaN;
    }
    return product;
  }

  public static float min(final FloatColumn values) {
    if (values.size() == 0) {
      return Float.NaN;
    }
    float min = values.first();
    while (values.hasNext()) {
      float value = values.next();
      if (!Float.isNaN(value)) {
        min = (min < value) ? min : value;
      }
    }
    return min;
  }

  public static float max(final FloatColumn values) {
    if (values.size() == 0) {
      return Float.NaN;
    }
    float max = values.first();
    values.reset();
    while (values.hasNext()) {
      float value = values.next();
      if (!Float.isNaN(value)) {
        if (value > max) {
          max = value;
        }
      }
    }
    return max;
  }

  /**
   * Returns the (sample) variance of the available values.
   *
   * <p>This method returns the bias-corrected sample variance (using {@code n - 1} in
   * the denominator).
   *
   * @return The variance, Double.NaN if no values have been added
   * or 0.0 for a single value set.
   */
  public static float variance(FloatColumn column) {
    float avg = mean(column);
    column.reset();
    float sumSquaredDiffs = 0.0f;
    while (column.hasNext()) {
      float next = column.next();
      float diff = next - avg;
      float sqrdDiff = diff * diff;
      sumSquaredDiffs += sqrdDiff;
    }
    //float sumMinusAverage = sum(column) - mean(column) * column.size();
    return sumSquaredDiffs / (column.size() - 1);
    //return (sumMinusAverage * sumMinusAverage) / (column.size() - 1);
  }

  /**
   * Returns the standard deviation of the available values.
   *
   * @return The standard deviation, Double.NaN if no values have been added
   * or 0.0 for a single value set.
   */
  public static float standardDeviation(FloatColumn values) {
    float stdDev = Float.NaN;
    int N = values.size();
    if (N > 0) {
      if (N > 1) {
        stdDev = (float) FastMath.sqrt(variance(values));
      } else {
        stdDev = 0.0f;
      }
    }
    return stdDev;
  }


  public static float mean(FloatColumn values) {
    return values.sum() / (float) values.size();
  }

  public static String stats(final FloatColumn values) {
    Stats stats = new Stats();
    stats.min = min(values);
    stats.max = max(values);
    stats.n = values.size();
    stats.mean = values.sum() / (float) stats.n;
    stats.variance = variance(values);
    return stats.printString();
  }
}
