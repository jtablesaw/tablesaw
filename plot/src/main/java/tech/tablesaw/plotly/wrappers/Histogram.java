package tech.tablesaw.plotly.wrappers;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.traces.HistogramTrace;

public class Histogram {

    public static HistogramTrace.HistogramBuilder builder(NumericColumn<? extends Number> values) {
        return new HistogramTrace.HistogramBuilder(values.asDoubleArray());
    }

    public static HistogramTrace.HistogramBuilder builder(
            Column<?> xValues, NumericColumn<? extends Number> values) {
        return new HistogramTrace.HistogramBuilder(xValues.asObjectArray(), values.asDoubleArray());
    }

    public HistogramTrace.HistogramBuilder y(NumericColumn<? extends Number> values) {
        this.y = values.asObjectArray();
        return this;
    }


}
