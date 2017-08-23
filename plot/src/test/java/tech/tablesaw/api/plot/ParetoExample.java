package tech.tablesaw.api.plot;

import static tech.tablesaw.api.QueryHelper.column;
import static tech.tablesaw.reducing.NumericReduceUtils.sum;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.plot.Pareto;

/**
 *
 */
public class ParetoExample {

    public static void main(String[] args) throws Exception {
        Table table = Table.read().csv("../data/tornadoes_1950-2014.csv");
        table = table.selectWhere(column("Fatalities").isGreaterThan(3));
        Pareto.show("Tornado Fatalities by State", table.summarize("fatalities", sum).by("State"));
    }
}