package com.github.lwhite1.tablesaw.integration;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.store.StorageManager;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public class AirlineDelays2 {

  private static Table flights2015;

  public static void main(String[] args) throws Exception {

    new AirlineDelays2();
    Stopwatch stopwatch = Stopwatch.createStarted();
    Table sorted = flights2015.sortAscendingOn("ORIGIN", "UNIQUE_CARRIER");
    System.out.println("Sorting " + stopwatch.elapsed(TimeUnit.SECONDS));
    System.out.println(sorted.first(1000).print());
    System.exit(0);
  }

  private AirlineDelays2() throws Exception {
    Stopwatch stopwatch = Stopwatch.createStarted();
    System.out.println("loading");

    flights2015 = StorageManager.readTable("bigdata/3f07b9bf-053f-4f9b-9dff-9d354835b276");

    System.out.println(String.format("loaded %d records in %d seconds",
        flights2015.rowCount(),
        (int) stopwatch.elapsed(TimeUnit.SECONDS)));

    out(flights2015.shape());
    out(flights2015.columnNames().toString());
    out(flights2015.first(10).print());
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
