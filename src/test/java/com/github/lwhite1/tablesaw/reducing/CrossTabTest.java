package com.github.lwhite1.tablesaw.reducing;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.io.csv.CsvReader;
import org.junit.Test;

/**
 *
 */
public class CrossTabTest {

  @Test
  public void testXCount() throws Exception {

    Table t = CsvReader.read("data/tornadoes_1950-2014.csv");

    Table xtab = CrossTab.xCount(t, t.categoryColumn("State"), t.shortColumn("Scale"));
  //  System.out.println(xtab.print());

  //  System.out.println(CrossTab.tablePercents(xtab).print());

  //  System.out.println(CrossTab.rowPercents(xtab).print());
  }
}