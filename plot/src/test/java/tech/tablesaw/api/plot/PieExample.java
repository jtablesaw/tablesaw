package tech.tablesaw.api.plot;


import tech.tablesaw.api.Table;
import static tech.tablesaw.api.plot.Pie.show;
import static tech.tablesaw.reducing.NumericReduceUtils.mean;
import static tech.tablesaw.reducing.NumericReduceUtils.sum;

/**
 * Basic sample pie chart
 */
public class PieExample {

    public static void main(String[] args) throws Exception {
        Table table = Table.createFromCsv("../data/tornadoes_1950-2014.csv");

        Table t2 = table.countBy(table.categoryColumn("State"));
        show("tornadoes by state", t2.categoryColumn("Category"), t2.numericColumn("Count"));

        show("Sum of fatalities by State", table.summarize("fatalities", sum).by("State"));
        show("Average fatalities by scale", table.summarize("fatalities", mean).by("Scale"));
    }
}
