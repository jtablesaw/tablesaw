package tech.tablesaw.plotly.wrappers;

import tech.tablesaw.plotly.traces.*;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.columns.Column;

public class Scatter {

    private ScatterTrace.ScatterBuilder getBuilder(Column<?> x, Column<?> y) {
        ScatterTrace.ScatterBuilder builder = ScatterTrace.builder(x.asObjectArray(), y.asObjectArray());
        this.x = x.asObjectArray();
        this.y = y.asObjectArray();
    }

    private ScatterTrace.ScatterBuilder getBuilder(
            Column<?> x,
            NumericColumn<? extends Number> open,
            NumericColumn<? extends Number> high,
            NumericColumn<? extends Number> low,
            NumericColumn<? extends Number> close) {
        this.x = x.asObjectArray();
        this.open = open.asDoubleArray();
        this.high = high.asDoubleArray();
        this.low = low.asDoubleArray();
        this.close = close.asDoubleArray();
    }

    public static ScatterTrace.ScatterBuilder builder(Column<?> x, Column<?> y) {
        return new ScatterTrace.ScatterBuilder(x, y);
    }

    public static ScatterTrace.ScatterBuilder builder(
            Column<?> x,
            NumericColumn<? extends Number> open,
            NumericColumn<? extends Number> high,
            NumericColumn<? extends Number> low,
            NumericColumn<? extends Number> close) {
        return new ScatterTrace.ScatterBuilder(x, open, high, low, close);
    }

}
