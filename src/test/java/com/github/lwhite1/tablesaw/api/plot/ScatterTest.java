package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.api.plot.swing.Quantile;
import com.github.lwhite1.tablesaw.api.plot.swing.Scatter;

import static com.github.lwhite1.tablesaw.api.QueryHelper.*;

/**
 *
 */
public class ScatterTest {
    public static void main(String[] args) throws Exception {

      Table tornado = Table.createFromCsv("data/tornadoes_1950-2014.csv");

      System.out.println(tornado.count("Scale").by("Scale").print());
      tornado = tornado.selectWhere(
          both(
              column("Start Lat").isGreaterThan(0f),
              column("Scale").isGreaterThanOrEqualTo(0)));


      Scatter.show("US Tornados 1950-2014",
          tornado.numericColumn("Start Lon"),
          tornado.numericColumn("Start Lat"));

      // baseball example with multiple series
      Table baseball = Table.createFromCsv("data/baseball.csv");
      System.out.println(baseball.structure().print());
      Scatter.show("Regular season wins by year",
          baseball.numericColumn("W"),
          baseball.numericColumn("Year"),
          baseball.splitOn(baseball.column("Playoffs")));

      Table bush = Table.createFromCsv("data/BushApproval.csv");
      String title = "Quantiles: George W. Bush (Feb. 2001 - Feb. 2004)";
      Quantile.show(title, bush.numericColumn("approval"));
    }
}
