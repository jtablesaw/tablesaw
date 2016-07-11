package com.github.lwhite1.tablesaw.reducing.functions;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.reducing.NumericReduceFunction;

import static com.github.lwhite1.tablesaw.reducing.NumericReduceUtils.mean;

/**
 *
 */
public class Average extends SummaryFunction {

  public Average(Table original, String summarizedColumnName) {
    super(original, summarizedColumnName);
  }

  @Override
  public String summaryFunctionName() {
    return "Mean";
  }

  @Override
  public NumericReduceFunction function() {
    return mean;
  }
}
