package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.HeatmapTrace;
import tech.tablesaw.util.DoubleArrays;

import java.util.List;

public class Heatmap {

    private Heatmap() {}

    public static Figure create(String title, Table table, String categoryCol1, String categoryCol2) {
        Layout layout = Layout.builder(title).build();

        Table counts = table.xTabCounts(categoryCol1, categoryCol2);
        counts = counts.dropRows(counts.rowCount() - 1);
        List<Column<?>> columns = counts.columns();
        columns.remove(counts.columnCount() - 1);
        Column<?> yColumn = columns.remove(0);
        double[][] z = DoubleArrays.to2dArray(counts.numericColumns());

        Object[] x = counts.columnNames().toArray();
        Object[] y = yColumn.asObjectArray();
        HeatmapTrace trace = HeatmapTrace.builder(x, y, z).build();
        return new Figure(layout, trace);
    }
}
