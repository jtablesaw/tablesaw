package tech.tablesaw.api.plot;

import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.plot.Line;

/**
 *
 */
public class LinePlotExample {

    public static void main(String[] args) throws Exception {
        Table baseball = Table.read().csv("../data/boston-robberies.csv");
        NumericColumn x = baseball.nCol("Record");
        NumericColumn y = baseball.nCol("Robberies");
        Line.show("Monthly Boston Armed Robberies Jan. 1966 - Oct. 1975", x, y);
    }
}