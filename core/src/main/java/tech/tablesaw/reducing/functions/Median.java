package tech.tablesaw.reducing.functions;

import static tech.tablesaw.reducing.NumericReduceUtils.median;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;

/**
 *
 */
public class Median extends SummaryFunction {

    public Median(Table original, String summarizedColumnName) {
        super(original, summarizedColumnName);
    }

    @Override
    public NumericReduceFunction function() {
        return median;
    }
}
