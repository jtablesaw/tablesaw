package tech.tablesaw.api.plot;

import static tech.tablesaw.api.QueryHelper.column;
import static tech.tablesaw.api.plot.HorizontalBar.show;
import static tech.tablesaw.reducing.NumericReduceUtils.sum;

import tech.tablesaw.api.Table;

/**
 *
 */
public class HorizontalBarExample {

    public static void main(String[] args) throws Exception {
        Table table = Table.read().csv("../data/tornadoes_1950-2014.csv");
        Table t2 = table.countBy(table.categoryColumn("State"));
        t2 = t2.selectWhere(column("Count").isGreaterThan(100));
        show("tornadoes by state", t2.categoryColumn("Category"), t2.nCol("Count"));
        show("T", table.summarize("fatalities", sum).by("Scale"));
    }
}
