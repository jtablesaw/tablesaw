package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.Table;

import static com.github.lwhite1.tablesaw.api.QueryHelper.column;
import static com.github.lwhite1.tablesaw.reducing.NumericReduceUtils.sum;

/**
 *
 */
public class ParetoExample {

  public static void main(String[] args) throws Exception {
    Table table = Table.createFromCsv("data/tornadoes_1950-2014.csv");
    table = table.selectWhere(column("Fatalities").isGreaterThan(3));
    Pareto.show("Tornado Fatalities by State", table.summarize("fatalities", sum).by("State"));
  }
}