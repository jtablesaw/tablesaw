package tech.tablesaw.reducing.functions;

import static tech.tablesaw.reducing.NumericReduceUtils.product;

import tech.tablesaw.api.Table;
import tech.tablesaw.reducing.NumericReduceFunction;

/**
 *
 */
public class Product extends SummaryFunction {

    public Product(Table original, String summarizedColumnName) {
        super(original, summarizedColumnName);
    }

    @Override
    public NumericReduceFunction function() {
        return product;
    }
}
