package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.Table;

import static com.github.lwhite1.tablesaw.api.QueryHelper.column;

/**
 *
 */
public class HorizontalBarFxTest {

  public static void main(String[] args) throws Exception {
    Table table = Table.createFromCsv("data/tornadoes_1950-2014.csv");
    Table t2 = table.countBy(table.categoryColumn("State"));
    t2 = t2.selectWhere(column("Count").isGreaterThan(100));
    //return HorizontalBar.show("tornados by state", t2, "Category", "Count");
    HorizontalBar.show("tornados by state", t2.categoryColumn("Category"), t2.nCol("Count"));
  }
}
