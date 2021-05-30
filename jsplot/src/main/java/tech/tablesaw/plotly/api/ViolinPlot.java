package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.BoxTrace;
import tech.tablesaw.plotly.traces.ViolinTrace;

public class ViolinPlot {

    private static final int HEIGHT = 600;
    private static final int WIDTH = 800;

    public static Figure create(
            String title, Table table, String groupingColumn, String numericColumn, boolean showBoxPlot, boolean showMeanLine) {
        Layout layout = Layout.builder().title(title).height(HEIGHT).width(WIDTH).build();
        ViolinTrace trace =
                ViolinTrace.builder(table.categoricalColumn(groupingColumn), table.nCol(numericColumn))
                        .boxPlot(showBoxPlot)
                        .meanLine(showMeanLine)
                        .build();
        return new Figure(layout, trace);
    }
}
