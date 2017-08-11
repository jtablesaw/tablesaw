package tech.tablesaw.api.plot;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.plot.Quantile;

/**
 *
 */
public class QuantileExample {

    public static void main(String[] args) throws Exception {
        Table baseball = Table.createFromCsv("../data/baseball.csv");
        NumericColumn x = baseball.nCol("BA");
        Quantile.show("Distribution of team batting averages", x);
    }
}