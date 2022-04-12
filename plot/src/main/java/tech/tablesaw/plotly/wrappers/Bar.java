package tech.tablesaw.plotly.wrappers;

import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.plotly.traces.BarTrace;

public class Bar extends TraceWrapper {

    public static BarTrace.BarBuilder builder(CategoricalColumn<?> x, NumericColumn<? extends Number> y) {
        return new BarTrace.BarBuilder(x, y);
    }


    BarBuilder(CategoricalColumn<?> x, NumericColumn<? extends Number> y) {

        this.x = columnToStringArray(x);
        this.y = y.asDoubleArray();
    }

}
