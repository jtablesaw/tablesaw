package tech.tablesaw.reducing.functions;

import static tech.tablesaw.reducing.NumericReduceUtils.kurtosis;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;

/**
 *
 */
public class Kurtosis extends SummaryFunction {

    public Kurtosis(Table original, String summarizedColumnName) {
        super(original, summarizedColumnName);
    }

    @Override
    public NumericReduceFunction function() {
        return kurtosis;
    }
}
