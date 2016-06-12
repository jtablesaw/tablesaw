package com.github.lwhite1.tablesaw.examples;

import com.github.lwhite1.tablesaw.Table;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.columns.IntColumn;
import com.github.lwhite1.tablesaw.io.CsvReader;
import com.github.lwhite1.tablesaw.io.CsvWriter;
import com.github.lwhite1.tablesaw.store.StorageManager;

import static com.github.lwhite1.tablesaw.api.ColumnType.*;
import static com.github.lwhite1.tablesaw.api.QueryHelper.*;

/**
 * Usage example using a Tornado dataset
 */
public class TornadoExample {

  public static void main(String[] args) throws Exception {

    Table tornadoes = CsvReader.read(COLUMN_TYPES_OLD, "data/1950-2014_torn.csv");

    out(tornadoes.structure().print());
    out();

    tornadoes.removeColumns("Year", "Month", "Day", "NS", "SN", "SG", "FIPS 1", "FIPS 2", "FIPS 3", "FIPS 4");

    CsvWriter.write("data/tornadoes_1950-2014.csv", tornadoes);

    tornadoes = CsvReader.read(COLUMN_TYPES, "data/tornadoes_1950-2014.csv");

    out(tornadoes.structure().print());

    out();
    out("Column names");
    out(tornadoes.columnNames());

    tornadoes.setName("tornadoes");

    out();
    out("print the table's shape:");
    out(tornadoes.shape());

    out();
    out("first five rows:");
    out(tornadoes.head(5).print());

    out();
    out("remove a column");
    tornadoes.removeColumns("State No");
    out("Revised column names");
    out(tornadoes.columnNames());

    out();
    out("Extact month from the date and make it a separate column");

    // TODO(lwhite): Add function to return month as string
    IntColumn month = tornadoes.localDateColumn("Date").month();
    tornadoes.addColumn(month);
    out(tornadoes.columnNames());
    out(month.stats().asTable("").print());

    out();
    out("Filtering");
    Table fatal = tornadoes.selectWhere(column("Fatalities").isGreaterThan(0));
    out(fatal.shape());

    out();
    out("Writing the revised table to a new csv file");
    CsvWriter.write("data/rev_tornadoes_1950-2014.csv", tornadoes);

    out();
    out("Saving to Tablesaw format");
    StorageManager.saveTable("/tmp/tablesaw/testdata", tornadoes);

    out();
    out("Reading from Tablesaw format");
    tornadoes = StorageManager.readTable("/tmp/tablesaw/testdata/tornadoes.saw");

  }

  private static void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }
  private static void out() {
    System.out.println("");
  }

  // column types for the tornado table
  private static final ColumnType[] COLUMN_TYPES = {
      INTEGER,     // number by year
      LOCAL_DATE,  // date
      LOCAL_TIME,  // time
      CATEGORY,    // tz
      CATEGORY,    // state
      CATEGORY,    // state FIPS
      INTEGER,     // state torn number
      INTEGER,     // scale
      INTEGER,     // injuries
      INTEGER,     // fatalities
      CATEGORY,    // loss
      FLOAT,       // crop loss
      FLOAT,       // St. Lat
      FLOAT,       // St. Lon
      FLOAT,       // End Lat
      FLOAT,       // End Lon
      FLOAT,       // length
      FLOAT        // width
  };

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
      CATEGORY, // loss
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