package tech.tablesaw.reducing.functions;

import static tech.tablesaw.reducing.NumericReduceUtils.range;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;

/**
 *
 */
public class Range extends SummaryFunction {

    public Range(Table original, String summarizedColumnName) {
        super(original, summarizedColumnName);
    }

    @Override
    public NumericReduceFunction function() {
        return range;
    }
}
