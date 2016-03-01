package com.deathrayresearch.outlier.aggregator;

import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.FloatColumn;
import org.apache.commons.math3.stat.StatUtils;

/**
 * Contains common utilities for double and long types
 */
public interface NumReduceUtils extends Column {

  // TODO(lwhite): Reimplement these methods to work natively with float[], instead of converting to double[]

  default double range() {
    reset();
    double[] array = this.toDoubleArray();
    return StatUtils.max(array) - StatUtils.min(array);
  }

  default double product() {
    reset();
    return StatUtils.product(toDoubleArray());
  }

  default double geometricMean() {
    reset();
    return StatUtils.geometricMean(toDoubleArray());
  }

  default double sumOfSquares() {
    reset();
    return StatUtils.sumSq(toDoubleArray());
  }

  default double[] normalize() {
    reset();
    return StatUtils.normalize(toDoubleArray());
  }

  default double sumOfLogs() {
    reset();
    return StatUtils.sumLog(toDoubleArray());
  }

  default double percentile(double percentile) {
    reset();
    return StatUtils.percentile(toDoubleArray(), percentile);
  }

  default float quartile1() {
    reset();
    return (float) StatUtils.percentile(toDoubleArray(), 25.0);
  }

  default float median() {
    reset();
    return (float) StatUtils.percentile(toDoubleArray(), 50.0);
  }

  default float quartile3() {
    reset();
    return (float) StatUtils.percentile(toDoubleArray(), 75.0);
  }

  default double percentile99() {
    reset();
    return StatUtils.percentile(toDoubleArray(), 99.0);
  }

  default double variance() {
    reset();
    return StatUtils.variance(toDoubleArray());
  }

  default double stdDev() {
    reset();
    return Math.sqrt(StatUtils.variance(toDoubleArray()));
  }

  default double meanDifference(FloatColumn column2) {
    reset();
    return StatUtils.meanDifference(toDoubleArray(), column2.toDoubleArray());
  }

  default double sumDifference(FloatColumn column2) {
    reset();
    return StatUtils.sumDifference(toDoubleArray(), column2.toDoubleArray());
  }

  double[] toDoubleArray();

  void reset();
}
