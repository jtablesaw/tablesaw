package tech.tablesaw.examples;

import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.components.Symbol;
import tech.tablesaw.plotly.traces.ScatterTrace;

public class DotPlotExample {

  public static void main(String[] args) throws Exception {

    Table bush = Table.read().csv("../data/bush.csv");

    NumericColumn<?> x = bush.nCol("approval");
    CategoricalColumn<?> y = bush.stringColumn("who");

    Layout layout = Layout.builder().title("Approval ratings by agency").build();

    ScatterTrace trace = ScatterTrace.builder(x, y).mode(ScatterTrace.Mode.MARKERS).build();
    Plot.show(new Figure(layout, trace));

    // A more complex example involving two traces
    IntColumn year = bush.dateColumn("date").year();
    year.setName("year");
    bush.addColumns(year);
    bush.dropWhere(bush.intColumn("year").isIn((Number) 2001, (Number) 2002));
    Table summary = bush.summarize("approval", AggregateFunctions.mean).by("who", "year");

    Layout layout2 =
        Layout.builder()
            .title("Mean approval ratings by agency and year for 2001 and 2002")
            .build();

    Table year1 = summary.where(summary.intColumn("year").isEqualTo(2001));
    Table year2 = summary.where(summary.intColumn("year").isEqualTo(2002));
    ScatterTrace trace2 =
        ScatterTrace.builder(year1.nCol("Mean [approval]"), year1.stringColumn("who"))
            .name("2001")
            .mode(ScatterTrace.Mode.MARKERS)
            .marker(Marker.builder().symbol(Symbol.DIAMOND).color("red").size(10).build())
            .build();

    ScatterTrace trace3 =
        ScatterTrace.builder(year2.nCol("Mean [approval]"), year2.stringColumn("who"))
            .name("2002")
            .mode(ScatterTrace.Mode.MARKERS)
            .marker(Marker.builder().symbol(Symbol.STAR).size(10).color("blue").build())
            .build();

    Plot.show(new Figure(layout2, trace2, trace3));
  }
}
