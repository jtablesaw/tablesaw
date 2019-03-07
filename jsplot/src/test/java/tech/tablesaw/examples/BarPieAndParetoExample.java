package tech.tablesaw.examples;

import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.HorizontalBarPlot;
import tech.tablesaw.plotly.api.PiePlot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.BarTrace;

import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.sum;

public class BarPieAndParetoExample {

    public static void main(String[] args) throws Exception {

        //***************** Setup *************************
        // load the data into a table
        Table tornadoes = Table.read().csv("../data/tornadoes_1950-2014.csv");

        // Get the scale column and replace any values of -9 with the column's missing value indicator
        IntColumn scale = tornadoes.intColumn("scale");
        scale.set(scale.isEqualTo(-9), IntColumnType.missingValueIndicator());

        //***************** Plotting **********************

        // BAR PLOTS

        // Sum the number of fatalities from each tornado, grouping by scale
        Table fatalities1 =
                tornadoes.summarize("fatalities", sum).by("scale");

        // Plot
        Plot.show(
                HorizontalBarPlot.create(
                        "fatalities by scale",		        // plot title
                        fatalities1,				            // table
                        "scale",					// grouping column name
                        "sum [fatalities]"));		// numeric column name

        // Plot the mean injuries rather than a sum.
        Table injuries1 = tornadoes.summarize("injuries", mean).by("scale");

        Plot.show(
                HorizontalBarPlot.create("Average number of tornado injuries by scale",
                        injuries1, "scale", "mean [injuries]"));


        // PIE PLOT
        Plot.show(
                PiePlot.create("fatalities by scale", fatalities1, "scale", "sum [fatalities]"));

        // PARETO PLOT
        Table t2 = tornadoes.summarize("fatalities", sum).by("State");

        t2 = t2.sortDescendingOn(t2.column(1).name());
        Layout layout = Layout.builder().title("Tornado Fatalities by State").build();
        BarTrace trace = BarTrace.builder(
                t2.categoricalColumn(0),
                t2.numberColumn(1))
                .build();
        Plot.show(new Figure(layout, trace));
    }

}
