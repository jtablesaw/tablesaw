package tech.tablesaw.plotly.api;

import com.google.common.base.Preconditions;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;

import java.util.Arrays;

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

        return create(title, xCol.name(), xCol.asDoubleArray(), yCol.name(), yCol.asDoubleArray());
    }

    /**
     * Returns a figure containing a QQ Plot describing the differences between the distribution of values
     * in the columns of interest
     * @param title    A title for the plot
     * @param xTitle   The name of the first numeric column containing the data to plot
     * @param xData    The data to plot on the x Axis
     * @param yTitle   The name of the second numeric column containing the data to plot
     * @param yData    The data to plot on the y Axis
     * @return         A quantile plot
     */
    public static Figure create(String title, String xTitle, double[] xData, String yTitle, double[] yData) {

        Preconditions.checkArgument(xData.length != 0, "x Data array is empty");
        Preconditions.checkArgument(yData.length != 0, "x Data array is empty");
        Preconditions.checkArgument(xData.length == yData.length, "Current implementation requires both arrays have the same length");

        Arrays.sort(xData);
        Arrays.sort(yData);
        double min = Math.min(xData[0], yData[0]);
        double max = Math.max(xData[xData.length - 1], yData[yData.length - 1]);
        double[] line = {min, max};

        ScatterTrace trace1 = ScatterTrace.builder(line, line)
                .mode(ScatterTrace.Mode.LINE)
                .name("y = x")
                .build();
        ScatterTrace trace2 = ScatterTrace.builder(xData, yData)
                .name("distributions")
                .build();
        Layout layout = Layout.builder()
                .title(title)
                .xAxis(Axis.builder().title(xTitle).build())
                .yAxis(Axis.builder().title(yTitle).build())
                .build();
        return new Figure(layout, trace1, trace2);
    }
}
