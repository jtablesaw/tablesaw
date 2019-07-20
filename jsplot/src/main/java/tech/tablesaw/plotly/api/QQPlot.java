package tech.tablesaw.plotly.api;

import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;

public class QQPlot {

    /**
     * Returns a figure containing a QQ Plot describing the differences between the distribution of values
     * in the columns of interest
     * @param title         A title for the plot
     * @param table         The table containing the columns of interest
     * @param columnName1   The name of the first numeric column containing the data to plot
     * @param columnName2   The name of the second numeric column containing the data to plot
     * @return              A quantile plot
     */
    public static Figure create(String title, Table table, String columnName1, String columnName2) {

        NumberColumn<?> xCol = table.nCol(columnName1);
        NumberColumn<?> yCol = table.nCol(columnName2);

        double min = Math.min(xCol.min(), yCol.min());
        double max = Math.max(xCol.max(), yCol.max());
        double[] line = {min, max};

        NumberColumn<?> xCopy = xCol.copy();
        NumberColumn<?> yCopy = yCol.copy();
        xCopy.sortAscending();
        yCopy.sortAscending();

        ScatterTrace trace1 = ScatterTrace.builder(line, line)
                .mode(ScatterTrace.Mode.LINE)
                .build();
        ScatterTrace trace2 = ScatterTrace.builder(xCopy.asDoubleArray(), yCopy.asDoubleArray())
                .build();
        Layout layout = Layout.builder()
                .title(title)
                .xAxis(Axis.builder().title(xCol.name()).build())
                .yAxis(Axis.builder().title(yCol.name()).build())
                .build();
        return new Figure(layout, trace1, trace2);
    }
}
