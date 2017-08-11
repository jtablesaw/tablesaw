package tech.tablesaw.api.plot;

import static tech.tablesaw.api.plot.Bar.show;
import static tech.tablesaw.reducing.NumericReduceUtils.sum;

import tech.tablesaw.api.Table;


/**
 * Basic sample vertical bar chart
 */
public class BarExample {


    public static void main(String[] args) throws Exception {
        Table table = Table.createFromCsv("../data/tornadoes_1950-2014.csv");
        Table t2 = table.countBy(table.categoryColumn("State"));
        //show("tornadoes by state", t2.categoryColumn("Category"), t2.numericColumn("Count"));

        //show("T", table.summarize("fatalities", sum).by("State"));
        show("T", table.summarize("fatalities", sum).by("Scale"));
    }
}
