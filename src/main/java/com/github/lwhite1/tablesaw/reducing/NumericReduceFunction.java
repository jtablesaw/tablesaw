package com.github.lwhite1.tablesaw.reducing;

/**
 *
 */
public interface NumericReduceFunction {

  String functionName();

  double reduce(double[] data);
}
