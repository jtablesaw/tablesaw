package tech.tablesaw.plotly.wrappers;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.traces.PieTrace;

public class Pie extends TraceWrapper {
    public static PieTrace.PieBuilder builder(Column<?> labels, NumericColumn<? extends Number> values) {
        return new PieTrace.PieBuilder(columnToStringArray(labels), values.asDoubleArray());
    }
}
