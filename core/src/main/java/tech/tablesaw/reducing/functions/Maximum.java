package tech.tablesaw.reducing.functions;

import static tech.tablesaw.reducing.NumericReduceUtils.max;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;

/**
 *
 */
public class Maximum extends SummaryFunction {

    public Maximum(Table original, String summarizedColumnName) {
        super(original, summarizedColumnName);
    }

    @Override
    public NumericReduceFunction function() {
        return max;
    }
}
