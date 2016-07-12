package com.github.lwhite1.tablesaw.reducing.functions;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.reducing.NumericReduceFunction;

import static com.github.lwhite1.tablesaw.reducing.NumericReduceUtils.geometricMean;

/**
 *
 */
public class GeometricMean extends SummaryFunction {

  public GeometricMean(Table original, String summarizedColumnName) {
    super(original, summarizedColumnName);
  }

  @Override
  public NumericReduceFunction function() {
    return geometricMean;
  }
}
