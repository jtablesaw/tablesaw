package tech.tablesaw.reducing.functions;


import static tech.tablesaw.reducing.NumericReduceUtils.sumOfSquares;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;

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
