package tech.tablesaw.reducing.functions;

import static tech.tablesaw.reducing.NumericReduceUtils.skewness;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;

/**
 *
 */
public class Skewness extends SummaryFunction {

    public Skewness(Table original, String summarizedColumnName) {
        super(original, summarizedColumnName);
    }

    @Override
    public NumericReduceFunction function() {
        return skewness;
    }
}
