package tech.tablesaw.reducing.functions;


import static tech.tablesaw.reducing.NumericReduceUtils.quartile3;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;

/**
 *
 */
public class ThirdQuartile extends SummaryFunction {

    public ThirdQuartile(Table original, String summarizedColumnName) {
        super(original, summarizedColumnName);
    }

    @Override
    public NumericReduceFunction function() {
        return quartile3;
    }
}
