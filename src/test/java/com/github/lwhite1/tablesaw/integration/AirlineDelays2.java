package com.github.lwhite1.tablesaw.integration;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.BooleanColumn;
import com.github.lwhite1.tablesaw.io.CsvReader;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import static com.github.lwhite1.tablesaw.api.QueryHelper.column;
import static java.lang.System.out;

/**
 *
 */
public class AirlineDelays2 {

  private static Table flt2007;

  public static void main(String[] args) throws Exception {

    new AirlineDelays2();
    Stopwatch stopwatch = Stopwatch.createStarted();
    Table sorted = flt2007.sortAscendingOn("ORIGIN", "UniqueCarrier");
    System.out.println("Sorting " + stopwatch.elapsed(TimeUnit.SECONDS));
    System.out.println(sorted.first(1000).print());
    System.exit(0);
  }

  private AirlineDelays2() throws Exception {
    Stopwatch stopwatch = Stopwatch.createStarted();
    out.println("loading");
    out.println(CsvReader.printColumnTypes("/Users/larrywhite/Downloads/flight delays/2007.csv", true, ','));
    flt2007 = Table.create("/Users/larrywhite/Downloads/flight delays/2007.csv");

    out.println(flt2007.first(5).print());

    out.println(String.format("loaded %d records in %d seconds",
        flt2007.rowCount(),
        (int) stopwatch.elapsed(TimeUnit.SECONDS)));

    out(flt2007.shape());

    Table ord = flt2007.selectWhere(column("Origin").isEqualTo("ORD"));

    BooleanColumn delayed = new BooleanColumn("Delayed?", ord.floatColumn("DepDelay").isGreaterThan(15), ord.rowCount());
    ord.addColumn(delayed);

    out("total flights: " + ord.rowCount());
    out("delayed flights: " + delayed.countTrue());

    out(flt2007.columnNames().toString());
    out(flt2007.first(10).print());
  }

  private static void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }

  // A reduced set of available columns
  static ColumnType[] reduced_set = {
      ColumnType.LOCAL_DATE, // flight date
      ColumnType.CATEGORY,  // unique carrier
      ColumnType.SKIP,  // airline id
      ColumnType.SKIP,  // carrier
      ColumnType.SKIP,  // TailNum
      ColumnType.SKIP,  // FlightNum
      ColumnType.SKIP, // Origin airport id
      ColumnType.CATEGORY,  // Origin
      ColumnType.SKIP, // Dest airport id
      ColumnType.CATEGORY,  // Dest
      ColumnType.LOCAL_TIME, // CRSDepTime
      ColumnType.LOCAL_TIME, // DepTime
      ColumnType.FLOAT, // DepDelay
      ColumnType.FLOAT, // TaxiOut
      ColumnType.FLOAT, // TaxiIn
      ColumnType.SKIP, // CRSArrTime
      ColumnType.SKIP, // ArrTime
      ColumnType.FLOAT,   // ArrDelay
      ColumnType.BOOLEAN, // Cancelled
      ColumnType.SKIP,     // CancellationCode
      ColumnType.SKIP, // Diverted
      ColumnType.SKIP, // CRSElapsedTime
      ColumnType.SKIP, // ActualElapsedTime
      ColumnType.FLOAT, // AirTime
      ColumnType.FLOAT, // Distance
      ColumnType.SKIP, // CarrierDelay
      ColumnType.SKIP, // WeatherDelay
      ColumnType.SKIP, // NASDelay
      ColumnType.SKIP, // SecurityDelay
      ColumnType.SKIP  // LateAircraftDelay
  };

  // The full set of available columns in the dataset
  static ColumnType[] heading = {
      ColumnType.LOCAL_DATE, // flight date
      ColumnType.CATEGORY,  // unique carrier
      ColumnType.CATEGORY,  // airline id
      ColumnType.CATEGORY,  // carrier
      ColumnType.CATEGORY,  // TailNum
      ColumnType.CATEGORY,  // FlightNum
      ColumnType.CATEGORY,  // Origin airport id
      ColumnType.CATEGORY,  // Origin
      ColumnType.CATEGORY,  // Dest airport id
      ColumnType.CATEGORY,  // Dest
      ColumnType.LOCAL_TIME, // CRSDepTime
      ColumnType.LOCAL_TIME, // DepTime
      ColumnType.FLOAT, // DepDelay
      ColumnType.FLOAT, // TaxiOut
      ColumnType.FLOAT, // TaxiIn
      ColumnType.LOCAL_TIME, // CRSArrTime
      ColumnType.LOCAL_TIME, // ArrTime
      ColumnType.FLOAT,   // ArrDelay
      ColumnType.BOOLEAN, // Cancelled
      ColumnType.CATEGORY,     // CancellationCode
      ColumnType.BOOLEAN, // Diverted
      ColumnType.FLOAT, // CRSElapsedTime
      ColumnType.FLOAT, // ActualElapsedTime
      ColumnType.FLOAT, // AirTime
      ColumnType.FLOAT, // Distance
      ColumnType.FLOAT, // CarrierDelay
      ColumnType.FLOAT, // WeatherDelay
      ColumnType.FLOAT, // NASDelay
      ColumnType.FLOAT, // SecurityDelay
      ColumnType.FLOAT  // LateAircraftDelay
  };
}
