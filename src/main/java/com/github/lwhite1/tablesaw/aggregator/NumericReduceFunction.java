package com.github.lwhite1.tablesaw.aggregator;

import com.github.lwhite1.tablesaw.columns.Column;

/**
 *
 */
public interface NumericReduceFunction {

  String functionName();

  double reduce(Column data);
}
