package tech.tablesaw.plotly.wrappers;

import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.plotly.traces.ViolinTrace;

public class Violin extends TraceWrapper {

    public static ViolinTrace.ViolinBuilder builder(CategoricalColumn<?> x, NumericColumn<? extends Number> y) {
        return new ViolinTrace.ViolinBuilder(x, y);
    }

    ViolinBuilder(CategoricalColumn<?> x, NumericColumn<? extends Number> y) {
        this.x = columnToStringArray(x);
        this.y = y.asDoubleArray();
    }
}
