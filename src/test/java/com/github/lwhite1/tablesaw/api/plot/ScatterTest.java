package com.github.lwhite1.tablesaw.api.plot;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.table.ViewGroup;

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

      // filter to most severe
      tornado = tornado.selectWhere(column("Scale").isGreaterThanOrEqualTo(4));
      ViewGroup subTables = new ViewGroup(tornado, tornado.column("Scale"));
      Scatter.show("US Tornados 1950-2014",
          tornado.numericColumn("Start Lon"),
          tornado.numericColumn("Start Lat"),
          subTables);
    }
}
