package com.github.lwhite1.tablesaw.aggregator;

/**
 *
 */
public interface NumericReduceFunction {

  String functionName();

  double reduce(double[] data);
}
