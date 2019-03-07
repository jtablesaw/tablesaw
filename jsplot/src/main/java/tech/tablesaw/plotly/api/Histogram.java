package tech.tablesaw.plotly.api;

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.HistogramTrace;

public class Histogram {

    public static Figure create(String title, NumberColumn<?> data) {
        return create(title, data.asDoubleArray());
    }

    public static Figure create(String title, Table table, String numericColumnName) {
        return create(title, table.numberColumn(numericColumnName));
    }

    public static Figure create(String title, double[] data) {
        HistogramTrace trace = HistogramTrace.builder(data).build();
        return new Figure(Layout.builder(title).build(), trace);
    }

}
