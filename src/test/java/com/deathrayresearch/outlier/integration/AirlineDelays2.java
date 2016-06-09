package com.deathrayresearch.outlier.integration;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.api.ColumnType;
import com.deathrayresearch.outlier.store.StorageManager;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import static com.deathrayresearch.outlier.api.ColumnType.*;

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
    System.out.println(sorted.head(1000).print());
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
    out(flights2015.head(10).print());
  }

  private static void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }

  // A reduced set of available columns
  static ColumnType[] reduced_set = {
      LOCAL_DATE, // flight date
      CATEGORY,  // unique carrier
      SKIP,  // airline id
      SKIP,  // carrier
      SKIP,  // TailNum
      SKIP,  // FlightNum
      SKIP, // Origin airport id
      CATEGORY,  // Origin
      SKIP, // Dest airport id
      CATEGORY,  // Dest
      LOCAL_TIME, // CRSDepTime
      LOCAL_TIME, // DepTime
      FLOAT, // DepDelay
      FLOAT, // TaxiOut
      FLOAT, // TaxiIn
      SKIP, // CRSArrTime
      SKIP, // ArrTime
      FLOAT,   // ArrDelay
      BOOLEAN, // Cancelled
      SKIP,     // CancellationCode
      SKIP, // Diverted
      SKIP, // CRSElapsedTime
      SKIP, // ActualElapsedTime
      FLOAT, // AirTime
      FLOAT, // Distance
      SKIP, // CarrierDelay
      SKIP, // WeatherDelay
      SKIP, // NASDelay
      SKIP, // SecurityDelay
      SKIP  // LateAircraftDelay
  };

  // The full set of available columns in the dataset
  static ColumnType[] heading = {
      LOCAL_DATE, // flight date
      CATEGORY,  // unique carrier
      CATEGORY,  // airline id
      CATEGORY,  // carrier
      CATEGORY,  // TailNum
      CATEGORY,  // FlightNum
      CATEGORY,  // Origin airport id
      CATEGORY,  // Origin
      CATEGORY,  // Dest airport id
      CATEGORY,  // Dest
      LOCAL_TIME, // CRSDepTime
      LOCAL_TIME, // DepTime
      FLOAT, // DepDelay
      FLOAT, // TaxiOut
      FLOAT, // TaxiIn
      LOCAL_TIME, // CRSArrTime
      LOCAL_TIME, // ArrTime
      FLOAT,   // ArrDelay
      BOOLEAN, // Cancelled
      CATEGORY,     // CancellationCode
      BOOLEAN, // Diverted
      FLOAT, // CRSElapsedTime
      FLOAT, // ActualElapsedTime
      FLOAT, // AirTime
      FLOAT, // Distance
      FLOAT, // CarrierDelay
      FLOAT, // WeatherDelay
      FLOAT, // NASDelay
      FLOAT, // SecurityDelay
      FLOAT  // LateAircraftDelay
  };
}
