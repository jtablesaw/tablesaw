package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.Histogram2DTrace;

public class Histogram2D {

    public static Figure create(String title, Table table, String xCol, String yCol) {
        Histogram2DTrace trace = Histogram2DTrace.builder(table.numberColumn(xCol), table.numberColumn(yCol))
                .build();

        return new Figure(Layout.builder(title, xCol, yCol).build(), trace);
    }

}
