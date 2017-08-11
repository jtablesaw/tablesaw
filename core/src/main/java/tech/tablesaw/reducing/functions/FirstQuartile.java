package tech.tablesaw.reducing.functions;

import static tech.tablesaw.reducing.NumericReduceUtils.quartile1;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;

/**
 *
 */
public class FirstQuartile extends SummaryFunction {

    public FirstQuartile(Table original, String summarizedColumnName) {
        super(original, summarizedColumnName);
    }

    @Override
    public NumericReduceFunction function() {
        return quartile1;
    }
}
