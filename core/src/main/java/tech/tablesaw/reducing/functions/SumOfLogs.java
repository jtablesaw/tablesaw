package tech.tablesaw.reducing.functions;

import static tech.tablesaw.reducing.NumericReduceUtils.sumOfLogs;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;


/**
 *
 */
public class SumOfLogs extends SummaryFunction {

    public SumOfLogs(Table original, String summarizedColumnName) {
        super(original, summarizedColumnName);
    }

    @Override
    public NumericReduceFunction function() {
        return sumOfLogs;
    }
}
