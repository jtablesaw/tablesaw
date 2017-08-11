package tech.tablesaw.reducing.functions;

import static tech.tablesaw.reducing.NumericReduceUtils.geometricMean;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;

/**
 *
 */
public class GeometricMean extends SummaryFunction {

    public GeometricMean(Table original, String summarizedColumnName) {
        super(original, summarizedColumnName);
    }

    @Override
    public NumericReduceFunction function() {
        return geometricMean;
    }
}
