package tech.tablesaw.reducing.functions;

import static tech.tablesaw.reducing.NumericReduceUtils.n;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;

/**
 *
 */
public class Count extends SummaryFunction {

    public Count(Table original, String summarizedColumnName) {
        super(original, summarizedColumnName);
    }

    @Override
    public NumericReduceFunction function() {
        return n;
    }
}
