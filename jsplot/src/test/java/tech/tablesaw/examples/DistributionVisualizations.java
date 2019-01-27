package tech.tablesaw.examples;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.BoxPlot;
import tech.tablesaw.plotly.api.Histogram;
import tech.tablesaw.plotly.api.Histogram2D;

public class DistributionVisualizations extends AbstractExample {

    public static void main(String[] args) throws Exception {

        Table property = Table.read().csv("../data/sacramento_real_estate_transactions.csv");
        out(property.structure());
        out(property.xTabCounts("type"));
        DoubleColumn sqft = property.doubleColumn("sq__ft");
        DoubleColumn price = property.doubleColumn("price");
        sqft.set(sqft.isEqualTo(0), DoubleColumnType.missingValueIndicator());
        price.set(price.isEqualTo(0), DoubleColumnType.missingValueIndicator());

        Plot.show(Histogram.create("Distribution of prices", property.numberColumn("price")));

        Plot.show(Histogram.create("Distribution of property sizes", property.numberColumn("sq__ft")));

        Plot.show(Histogram2D.create("Distribution of price and size", property,"price", "sq__ft"));

        Plot.show(BoxPlot.create("Prices by property type", property, "type", "price"));
    }
}
