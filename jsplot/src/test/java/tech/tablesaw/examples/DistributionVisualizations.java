package tech.tablesaw.examples;

import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.numbers.IntColumnType;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.BoxPlot;
import tech.tablesaw.plotly.api.Histogram;
import tech.tablesaw.plotly.api.Histogram2D;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.HistogramTrace;

public class DistributionVisualizations extends AbstractExample {

    public static void main(String[] args) throws Exception {

        Table property = Table.read().csv("../data/sacramento_real_estate_transactions.csv");

        IntColumn sqft = property.intColumn("sq__ft");
        IntColumn price = property.intColumn("price");

        sqft.set(sqft.isEqualTo(0), IntColumnType.missingValueIndicator());
        price.set(price.isEqualTo(0), IntColumnType.missingValueIndicator());

        Plot.show(Histogram.create("Distribution of prices", property.numberColumn("price")));

        Layout layout = Layout.builder().title("Distribution of property sizes").build();
        HistogramTrace trace = HistogramTrace.builder(property.numberColumn("sq__ft"))
                .marker(Marker.builder().color("#B10DC9").opacity(.70).build())
                .build();
        Plot.show(new Figure(layout, trace));


       // Plot.show(Histogram.create("Distribution of property sizes", property.numberColumn("sq__ft")));

        Plot.show(Histogram2D.create("Distribution of price and size", property,"price", "sq__ft"));

        Plot.show(BoxPlot.create("Prices by property type", property, "type", "price"));
    }
}
