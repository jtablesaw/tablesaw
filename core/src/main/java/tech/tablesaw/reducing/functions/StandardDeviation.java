package tech.tablesaw.reducing.functions;


import static tech.tablesaw.reducing.NumericReduceUtils.stdDev;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;

/**
 *
 */
public class StandardDeviation extends SummaryFunction {

    public StandardDeviation(Table original, String summarizedColumnName) {
        super(original, summarizedColumnName);
    }

    @Override
    public NumericReduceFunction function() {
        return stdDev;
    }
}
