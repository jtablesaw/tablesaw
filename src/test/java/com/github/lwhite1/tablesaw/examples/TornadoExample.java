package com.github.lwhite1.tablesaw.examples;

import com.github.lwhite1.tablesaw.api.CategoryColumn;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.api.plot.Scatter;

import static com.github.lwhite1.tablesaw.api.ColumnType.*;
import static com.github.lwhite1.tablesaw.api.QueryHelper.column;

/**
 * Usage example using a Tornado dataset
 */
public class TornadoExample {

  public static void main(String[] args) throws Exception {

    Table tornadoes = Table.createFromCsv(COLUMN_TYPES_OLD, "data/1950-2014_torn.csv");
    assert(tornadoes != null);

    out(tornadoes.structure().print());
    out();

    tornadoes.removeColumns("Number", "Year", "Month", "Day", "Zone", "State FIPS", "Loss", "Crop Loss", "End Lat", "End Lon", "NS", "SN", "SG", "FIPS 1", "FIPS 2", "FIPS 3", "FIPS 4");

    tornadoes.exportToCsv("data/tornadoes_1950-2014.csv");

    //tornadoes = Table.createFromCsv(COLUMN_TYPES, "data/tornadoes_1950-2014.csv");
    tornadoes = Table.createFromCsv("data/tornadoes_1950-2014.csv");
    assert(tornadoes != null);

    out(tornadoes.structure().print());
    out(tornadoes.structure().selectWhere(column("Column Type").isEqualTo("INTEGER")).print());

    tornadoes.setName("tornadoes");

    out();
    out("Column names");
    out(tornadoes.columnNames());

    out();
    out("Remove the 'State No' column");
    tornadoes.removeColumns("State No");
    out(tornadoes.columnNames());

    out();
    out("print the table's shape:");
    out(tornadoes.shape());

    out();
    out("Use first(3) to view the first 3 rows:");
    out(tornadoes.first(3).print());

    tornadoes = tornadoes.selectWhere(column("Start Lat").isGreaterThan(20f));
    Scatter.show("US Tornadoes 1950-2014", tornadoes.nCol("Start Lon"), tornadoes.nCol("Start Lat"));

    out();
    out("Extact month from the date and make it a separate column");
    CategoryColumn month = tornadoes.dateColumn("Date").month();
    out(month.summary().print());

    out("Add the month column to the table");
    tornadoes.addColumn(2, month);
    out(tornadoes.columnNames());

    out();
    out("Filtering: Tornadoes where there were fatalities");
    Table fatal = tornadoes.selectWhere(column("Fatalities").isGreaterThan(0));
    out(fatal.shape());

    out();
    out(fatal.first(5).print());

    out();
    out("Total fatalities: " + fatal.shortColumn("Fatalities").sum());

    out();
    out("Sorting on Fatalities in descending order");
    fatal = fatal.sortDescendingOn("Fatalities");
    out(fatal.first(5).print());

    out("");
    out("Calculating basic descriptive statistics on Fatalities");
    out(fatal.shortColumn("Fatalities").summary().print());


    //TODO(lwhite): Provide a param for title of the new table (or auto-generate a better one).
    Table injuriesByScale = tornadoes.median("Injuries").by("Scale");
    injuriesByScale.setName("Median injuries by Tornado Scale");
    out(injuriesByScale.print());


    //TODO(lwhite): Provide a param for title of the new table (or auto-generate a better one).
    Table injuriesByScaleState = tornadoes.median("Injuries").by("Scale", "State");
    injuriesByScaleState.setName("Median injuries by Tornado Scale and State");
    out(injuriesByScaleState.print());


    out();
    out("Writing the revised table to a new csv file");
    tornadoes.exportToCsv("data/rev_tornadoes_1950-2014.csv");

    out();
    out("Saving to Tablesaw format");
    String dbName = tornadoes.save("/tmp/tablesaw/testdata");

    // NOTE: dbName is equal to "/tmp/tablesaw/testdata/tornadoes.saw"

    out();
    out("Reading from Tablesaw format");
    tornadoes = Table.readTable(dbName);
  }

  private static void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }
  private static void out() {
    System.out.println("");
  }

  // column types for the tornado table
  private static final ColumnType[] COLUMN_TYPES_OLD = {
      INTEGER,     // number by year
      INTEGER,     // year
      INTEGER,     // month
      INTEGER,     // day
      LOCAL_DATE,  // date
      LOCAL_TIME,  // time
      CATEGORY,    // tz
      CATEGORY,    // st
      CATEGORY,    // state fips
      INTEGER,     // state torn number
      INTEGER,     // scale
      INTEGER,     // injuries
      INTEGER,     // fatalities
      FLOAT,       // loss
      FLOAT,   // crop loss
      FLOAT,   // St. Lat
      FLOAT,   // St. Lon
      FLOAT,   // End Lat
      FLOAT,   // End Lon
      FLOAT,   // length
      FLOAT,   // width
      FLOAT,   // NS
      FLOAT,   // SN
      FLOAT,   // SG
      CATEGORY,  // Count FIPS 1-4
      CATEGORY,
      CATEGORY,
      CATEGORY};
}