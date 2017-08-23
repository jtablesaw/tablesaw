package tech.tablesaw.api.plot;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.plot.Histogram;

/**
 *
 */
public class HistogramExample {

    public static void main(String[] args) throws Exception {
        Table baseball = Table.read().csv("../data/baseball.csv");
        NumericColumn x = baseball.nCol("BA");
        Histogram.show("Distribution of team batting averages", x);
    }
}