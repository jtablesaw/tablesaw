package tech.tablesaw.reducing.functions;

import static tech.tablesaw.reducing.NumericReduceUtils.sum;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;

/**
 *
 */
public class Sum extends SummaryFunction {

    public Sum(Table original, String summarizedColumnName) {
        super(original, summarizedColumnName);
    }

    @Override
    public NumericReduceFunction function() {
        return sum;
    }
}
