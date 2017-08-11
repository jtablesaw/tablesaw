package tech.tablesaw.reducing.functions;

import static tech.tablesaw.reducing.NumericReduceUtils.min;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;

/**
 *
 */
public class Minimum extends SummaryFunction {

    public Minimum(Table original, String summarizedColumnName) {
        super(original, summarizedColumnName);
    }

    @Override
    public NumericReduceFunction function() {
        return min;
    }
}
