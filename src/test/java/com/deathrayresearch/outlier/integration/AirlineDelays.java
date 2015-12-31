package com.deathrayresearch.outlier.integration;

import com.deathrayresearch.outlier.ColumnType;
import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.io.CsvReader;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import static com.deathrayresearch.outlier.ColumnType.*;

/**
 *
 */
public class AirlineDelays {

  static ColumnType[] heading = {
      INTEGER, // year
      INTEGER, // month
      INTEGER, // day
      CAT,  // dow
      LOCAL_TIME, // DepTime
      LOCAL_TIME, // CRSDepTime
      LOCAL_TIME, // ArrTime
      LOCAL_TIME, // CRSArrTime
      CAT, // Carrier
      CAT, // FlightNum
      CAT, // TailNum
      FLOAT, // ActualElapsedTime
      FLOAT, // CRSElapsedTime
      FLOAT, // AirTime
      FLOAT, // ArrDelay
      FLOAT, // DepDelay
      CAT, // Origin
      CAT, // Dest
      FLOAT, // Distance
      FLOAT, // TaxiIn
      FLOAT, // TaxiOut
      BOOLEAN, // Cancelled
      CAT, // CancellationCode
      BOOLEAN, // Diverted
      FLOAT, // CarrierDelay
      FLOAT, // WeatherDelay
      FLOAT, // NASDelay
      FLOAT, // SecurityDelay
      FLOAT  // LateAircraftDelay
      };

  private static Table flights2008;

  public static void main(String[] args) throws Exception {

    new AirlineDelays();

    Stopwatch stopwatch = Stopwatch.createStarted();
    flights2008.sortAscendingOn("Origin", "UniqueCarrier");
    System.out.println("Sorting " + stopwatch.elapsed(TimeUnit.SECONDS));
/*    Table xtab = flights2008.xCount("Origin", "UniqueCarrier");
    System.out.println("xtabs " + stopwatch.elapsed(TimeUnit.SECONDS));
*/

/*
    out("Cross Tab: Origin by Unique Carrier");
    out(xtab.print());
    out(xtab.shape());
*/
  }

  private AirlineDelays() throws Exception {
    Stopwatch stopwatch = Stopwatch.createStarted();
    System.out.println("loading");
    flights2008 = CsvReader.read("bigdata/2008.csv", reduced_set);
    System.out.println(String.format("loaded %d records in %d seconds",
        flights2008.rowCount(),
        (int) stopwatch.elapsed(TimeUnit.SECONDS)));
    out(flights2008.shape());
    out(flights2008.columnNames().toString());
    flights2008.head(10).print();

    stopwatch.reset().start();
    System.out.println("Selecting");
/*
    Table ord = flights2008.selectIf(
        both(
            column("Origin").isEqualTo("ORD"),
            column("Cancelled").isFalse()));
*/

    System.out.println("selecting " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

/*
    BooleanColumn depDelayed = ord.when(column("DepDelay").isGreaterThanOrEqualTo(15.0f));
    depDelayed.setName("DepDelayed");
    ord.addColumn(depDelayed);

    stopwatch.reset();
    System.out.println("Extracting hour from localtime");
    TextColumn hourOfDay = ord.timeColumn("CRSDepTime").hour();
    hourOfDay.setName("HourOfDay");
    ord.addColumn(hourOfDay);
    System.out.println("Extracting hour from localtime " + stopwatch.elapsed(TimeUnit.SECONDS));

    ord = ord.rejectColumns("Cancelled", "Origin");

    out(ord.shape());
    out("total flights: " + ord.rowCount());
    out("total delays: " + depDelayed.countTrue());


    CsvWriter.write("selected.csv", ord);
    */
  }

  private static void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }

  static ColumnType[] reduced_set = {
      SKIP, // year
      INTEGER, // month
      INTEGER, // day
      CAT,  // dow
      SKIP, // DepTime
      LOCAL_TIME, // CRSDepTime
      SKIP, // ArrTime
      SKIP, // CRSArrTime
      CAT, // Carrier
      SKIP, // FlightNum
      SKIP, // TailNum
      SKIP, // ActualElapsedTime
      SKIP, // CRSElapsedTime
      SKIP, // AirTime
      SKIP, // ArrDelay
      INTEGER, // DepDelay
      CAT, // Origin
      CAT, // Dest
      INTEGER, // Distance
      SKIP, // TaxiIn
      SKIP, // TaxiOut
      BOOLEAN, // Cancelled
      SKIP, // CancellationCode
      SKIP, // Diverted
      SKIP, // CarrierDelay
      SKIP, // WeatherDelay
      SKIP, // NASDelay
      SKIP, // SecurityDelay
      SKIP  // LateAircraftDelay
  };
}
