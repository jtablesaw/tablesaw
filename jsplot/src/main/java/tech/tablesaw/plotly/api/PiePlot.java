package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.PieTrace;

public class PiePlot {

    public static Figure create(String title, Table table, String groupColName, String numberColName) {

        Layout layout = Layout.builder(title).build();

        PieTrace trace = PieTrace.builder(
                table.column(groupColName),
                table.numberColumn(numberColName))
                .showLegend(true)
                .build();
        return new Figure(layout, trace);
    }
}