package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;

public class CandlestickPlot {

    public static Figure create(String title, Table table, String xCol, String openCol, String highCol, String lowCol, String closeCol) {
        Layout layout = Layout.builder(title, xCol).build();
        ScatterTrace trace = ScatterTrace.builder(
                table.dateColumn(xCol),
                table.numberColumn(openCol),
                table.numberColumn(highCol),
                table.numberColumn(lowCol),
                table.numberColumn(closeCol))
                .type("candlestick")
                .build();
        return new Figure(layout, trace);
    }
}
