package tech.tablesaw.plotly.api;

import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.HistogramTrace;
import tech.tablesaw.plotly.traces.Trace;

public class Histogram {

    private static final int HEIGHT = 600;
    private static final int WIDTH = 800;

    public static void show(String title, double[] data) {
        Plot.show(asFigure(title, data));
    }

    public static void show(String title, NumberColumn data) {
        Plot.show(asFigure(title, data));
    }

    public static void show(String title, Table table, String numericColumName) {
        Plot.show(asFigure(title, table, numericColumName));
    }

    public static void show(String title, Table table, String groupCol, String... numericColumnName) {

        Layout layout = standardLayout(title).build();
        CategoricalColumn catCol = table.categoricalColumn(groupCol);

        Trace[] traces = new Trace[catCol.unique().size()];

        for (int i = 0; i < numericColumnName.length; i++) {
            String name = catCol.getString(i);
            HistogramTrace trace = HistogramTrace.builder(
                    table.categoricalColumn(groupCol),
                    table.numberColumn(name))
                    .showLegend(true)
                    .name(name)
                    .build();
            traces[i] = trace;
        }
        Plot.show(new Figure(layout, traces));
    }

    private static Figure asFigure(String title, NumberColumn data) {
        return asFigure(title, data.asDoubleArray());
    }

    private static Figure asFigure(String title, Table table, String numericColumnName) {
        return asFigure(title, table.numberColumn(numericColumnName));
    }

    private static Figure asFigure(String title, double[] data) {
        HistogramTrace trace = HistogramTrace.builder(data).build();
        return new Figure(standardLayout(title), trace);
    }

    private static Layout.LayoutBuilder standardLayout(String title) {
        return Layout.builder()
                .title(title)
                .height(HEIGHT)
                .width(WIDTH);
    }
}
