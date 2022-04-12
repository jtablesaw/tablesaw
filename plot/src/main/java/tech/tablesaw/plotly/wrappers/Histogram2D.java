package tech.tablesaw.plotly.wrappers;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.plotly.traces.Histogram2DTrace;

public class Histogram2D {
    public static Histogram2DTrace.Histogram2DBuilder builder(
            NumericColumn<? extends Number> x, NumericColumn<? extends Number> y) {
        return new Histogram2DTrace.Histogram2DBuilder(x.asDoubleArray(), y.asDoubleArray());
    }
}
