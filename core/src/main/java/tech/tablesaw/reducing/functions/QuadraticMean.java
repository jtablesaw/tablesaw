package tech.tablesaw.reducing.functions;

import static tech.tablesaw.reducing.NumericReduceUtils.quadraticMean;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;

/**
 *
 */
public class QuadraticMean extends SummaryFunction {

    public QuadraticMean(Table original, String summarizedColumnName) {
        super(original, summarizedColumnName);
    }

    @Override
    public NumericReduceFunction function() {
        return quadraticMean;
    }
}
