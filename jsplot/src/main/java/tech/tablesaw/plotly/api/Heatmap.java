package tech.tablesaw.plotly.api;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.HeatmapTrace;
import tech.tablesaw.util.DoubleArrays;

import java.util.List;

public class Heatmap {

    private static final int HEIGHT = 600;
    private static final int WIDTH = 800;

    private Heatmap() {}

    public static Figure create(String title, Table table, String categoryCol1, String categoryCol2) {

        Layout layout = Layout.builder()
                .title(title)
                .height(HEIGHT)
                .width(WIDTH)
                .build();

        Table counts = table.xTabCounts(categoryCol1, categoryCol2);
        counts = counts.dropRows(counts.rowCount() - 1);
        List<NumericColumn<?>> columns = counts.numericColumns();
        columns.remove(counts.columnCount() - 1);
        NumericColumn<?> yColumn = columns.remove(0);
        double[][] z = DoubleArrays.to2dArray(columns);

        List<String> x = counts.columnNames();
        Object[] y = yColumn.asObjectArray();
        HeatmapTrace trace = HeatmapTrace.heatmapBuilder(x, y, z)
                .build();
        return new Figure(layout, trace);
    }
}