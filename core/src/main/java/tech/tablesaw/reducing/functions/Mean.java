package tech.tablesaw.reducing.functions;

import static tech.tablesaw.reducing.NumericReduceUtils.mean;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;

/**
 *
 */
public class Mean extends SummaryFunction {

    public Mean(Table original, String summarizedColumnName) {
        super(original, summarizedColumnName);
    }

    @Override
    public NumericReduceFunction function() {
        return mean;
    }
}
