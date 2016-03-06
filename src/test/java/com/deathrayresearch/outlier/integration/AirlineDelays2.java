package com.deathrayresearch.outlier.integration;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.columns.ColumnType;
import com.deathrayresearch.outlier.io.CsvReader;
import com.deathrayresearch.outlier.store.StorageManager;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import static com.deathrayresearch.outlier.columns.ColumnType.*;

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
  }

  private AirlineDelays2() throws Exception {
    Stopwatch stopwatch = Stopwatch.createStarted();
    System.out.println("loading");

/*
    flights2015 = CsvReader.read("bigdata/2015.csv", reduced_set );
    StorageManager.write("bigdata", flights2015);
*/


    flights2015 = StorageManager.readTable("bigdata/00d9730b-8636-4a19-8f25-683ca0664e13");

    System.out.println(String.format("loaded %d records in %d seconds",
        flights2015.rowCount(),
        (int) stopwatch.elapsed(TimeUnit.SECONDS)));
    out(flights2015.shape());
    out(flights2015.columnNames().toString());
    flights2015.head(10).print();
    stopwatch.reset().start();

   // StorageManager.write("databases", flights2015);
  }

  private static void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }

  // A reduced set of available columns
  static ColumnType[] reduced_set = {
      LOCAL_DATE, // flight date
      CAT,  // unique carrier
      SKIP,  // airline id
      SKIP,  // carrier
      SKIP,  // TailNum
      SKIP,  // FlightNum
      SKIP, // Origin airport id
      CAT,  // Origin
      SKIP, // Dest airport id
      CAT,  // Dest
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
      CAT,  // unique carrier
      CAT,  // airline id
      CAT,  // carrier
      CAT,  // TailNum
      CAT,  // FlightNum
      CAT,  // Origin airport id
      CAT,  // Origin
      CAT,  // Dest airport id
      CAT,  // Dest
      LOCAL_TIME, // CRSDepTime
      LOCAL_TIME, // DepTime
      FLOAT, // DepDelay
      FLOAT, // TaxiOut
      FLOAT, // TaxiIn
      LOCAL_TIME, // CRSArrTime
      LOCAL_TIME, // ArrTime
      FLOAT,   // ArrDelay
      BOOLEAN, // Cancelled
      CAT,     // CancellationCode
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
