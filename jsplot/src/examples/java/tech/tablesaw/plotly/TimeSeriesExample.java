package tech.tablesaw.plotly;

import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.api.TimeSeriesPlot;

public class TimeSeriesExample {

    public static void main(String[] args) throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        bush = bush.where(bush.stringColumn("who").equalsIgnoreCase("fox"));
        DateColumn x = bush.dateColumn("date");
        NumberColumn y = bush.nCol("approval");
        TimeSeriesPlot.show("Fox approval ratings for George W. Bush","date", x, "rating", y);
    }

}
