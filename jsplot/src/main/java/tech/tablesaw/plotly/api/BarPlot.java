package tech.tablesaw.plotly.api;

import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.BarTrace;
import tech.tablesaw.plotly.traces.BarTrace.Orientation;
import tech.tablesaw.plotly.traces.Trace;

class BarPlot {

    static final int HEIGHT = 700;
    static final int WIDTH = 900;

    static void show(Orientation orientation, String title, Table table, String groupColName, String numberColName) {

        Layout layout = standardLayout(title).build();

        BarTrace trace = BarTrace.builder(
                table.categoricalColumn(groupColName),
                table.numberColumn(numberColName))
                .orientation(orientation)
                .build();
        Plot.show(new Figure(layout, trace));
    }

    static Figure create(Orientation orientation, String title, Table table, String groupColName, String numberColName) {

        Layout layout = standardLayout(title).build();

        BarTrace trace = BarTrace.builder(
                table.categoricalColumn(groupColName),
                table.numberColumn(numberColName))
                .orientation(orientation)
                .build();
        return new Figure(layout, trace);
    }

    static void show(Orientation orientation, String title, Table table, String groupColName, String... numberColNames) {

        Layout layout = standardLayout(title)
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

    static Figure create(Orientation orientation, String title, Table table, String groupColName, String... numberColNames) {

        Layout layout = standardLayout(title)
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
        return new Figure(layout, traces);
    }

    private static Layout.LayoutBuilder standardLayout(String title) {
        return Layout.builder()
                .title(title)
                .height(HEIGHT)
                .width(WIDTH);
    }
}
