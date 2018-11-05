package tech.tablesaw.plotly;

import tech.tablesaw.examples.AbstractExample;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.plotly.api.BoxPlot;
import tech.tablesaw.plotly.api.Histogram;
import tech.tablesaw.plotly.api.Histogram2D;

public class DistributionVisualizations extends AbstractExample {

    public static void main(String[] args) throws Exception {

        Table property = Table.read().csv("../data/sacramento_real_estate_transactions.csv");
        out(property.structure());
        out(property.xTabCounts("type"));
        NumberColumn sqft = property.numberColumn("sq__ft");
        NumberColumn price = property.numberColumn("price");
        sqft.set(sqft.isEqualTo(0), DoubleColumnType.missingValueIndicator());
        price.set(price.isEqualTo(0), DoubleColumnType.missingValueIndicator());

        Plot.show(Histogram.create("Distribution of prices", property.numberColumn("price")));

        Plot.show(Histogram.create("Distribution of property sizes", property.numberColumn("sq__ft")));

        Plot.show(Histogram2D.create("Distribution of price and size", property,"price", "sq__ft"));

        Plot.show(BoxPlot.create("Prices by property type", property, "type", "price"));
    }
}
