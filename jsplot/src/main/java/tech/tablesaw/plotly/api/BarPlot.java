package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.BarTrace;
import tech.tablesaw.plotly.traces.BarTrace.Orientation;
import tech.tablesaw.plotly.traces.Trace;

public class BarPlot {

    private static final int HEIGHT = 700;
    private static final int WIDTH = 900;

    public static void show(Orientation orientation, String title, Table table, String groupColName, String numberColName) {

        Layout layout = Layout.builder()
                .title(title)
                .height(HEIGHT)
                .width(WIDTH)
                .build();

        BarTrace trace = BarTrace.builder(
                table.categoricalColumn(groupColName),
                table.numberColumn(numberColName))
                .orientation(orientation)
                .build();
        Plot.show(new Figure(layout, trace));
    }

    public static void showHorizontal(String title, Table table, String groupColName, String numberColName) {
        show(Orientation.HORIZONTAL, title, table, groupColName, numberColName);
    }

    public static void showVertical(String title, Table table, String groupColName, String numberColName) {
        show(Orientation.VERTICAL, title, table, groupColName, numberColName);
    }

    public static void show(Orientation orientation, String title, Table table, String groupColName, String... numberColNames) {

        Layout layout = Layout.builder()
                .title(title)
                .height(HEIGHT)
                .width(WIDTH)
                .barMode(Layout.BarMode.GROUP)
                .showLegend(true)
                .build();

        Trace[] traces = new Trace[numberColNames.length];
        for (int i = 0; i < numberColNames.length; i++) {
            String name = numberColNames[i];
            BarTrace trace = BarTrace.builder(
                    table.categoricalColumn(groupColName),
                    table.numberColumn(name))
                    .orientation(orientation)
                    .showLegend(true)
                    .name(name)
                    .build();
            traces[i] = trace;
        }
        Plot.show(new Figure(layout, traces));
    }
}
