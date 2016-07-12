package com.github.lwhite1.tablesaw.reducing.functions;


import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.reducing.NumericReduceFunction;

import static com.github.lwhite1.tablesaw.reducing.NumericReduceUtils.sumOfSquares;

/**
 *
 */
public class SumOfSquares extends SummaryFunction {

  public SumOfSquares(Table original, String summarizedColumnName) {
    super(original, summarizedColumnName);
  }

  @Override
  public NumericReduceFunction function() {
    return sumOfSquares;
  }
}
