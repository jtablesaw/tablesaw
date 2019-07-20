package tech.tablesaw.plotly.api;

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;

public class QuantilePlot {

    /**
     * Returns a figure containing a Quantile Plot describing the distribution of values in the column of interest
     * @param title         A title for the plot
     * @param table         The table containing the column of interest
     * @param columnName    The name of the numeric column containing the data to plot
     * @return              A quantile plot
     */
    public static Figure create(String title, Table table, String columnName) {

        NumberColumn<?> xCol = table.nCol(columnName);

        double[] x = new double[xCol.size()];

        for (int i = 0; i < x.length; i++) {
            x[i] = i / (float) x.length;
        }

        NumberColumn<?> copy = xCol.copy();
        copy.sortAscending();

        ScatterTrace trace = ScatterTrace.builder(x, copy.asDoubleArray()).build();
        Layout layout = Layout.builder().title(title).build();
        return new Figure(layout, trace);
    }
}
