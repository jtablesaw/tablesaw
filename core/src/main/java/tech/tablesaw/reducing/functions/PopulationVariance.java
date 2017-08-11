package tech.tablesaw.reducing.functions;

import static tech.tablesaw.reducing.NumericReduceUtils.populationVariance;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;

/**
 *
 */
public class PopulationVariance extends SummaryFunction {

    public PopulationVariance(Table original, String summarizedColumnName) {
        super(original, summarizedColumnName);
    }

    @Override
    public NumericReduceFunction function() {
        return populationVariance;
    }
}
