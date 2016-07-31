package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.Table;


/**
 *  Basic sample box plot. It shows the distribution of the number of people injured in tornadoes, broken out
 *  by the scale of the Tornado.
 */
public class BoxExample {

  public static void main(String[] args) throws Exception {
    Table table = Table.createFromCsv("data/tornadoes_1950-2014.csv");
    Box.show("Tornado Injuries by Scale", table, "injuries", "scale");
  }
}
