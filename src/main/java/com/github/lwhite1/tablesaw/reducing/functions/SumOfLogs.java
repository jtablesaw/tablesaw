package com.github.lwhite1.tablesaw.reducing.functions;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.reducing.NumericReduceFunction;

import static com.github.lwhite1.tablesaw.reducing.NumericReduceUtils.sumOfLogs;


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
