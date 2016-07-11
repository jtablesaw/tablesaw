package com.github.lwhite1.tablesaw.integration;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.store.StorageManager;
import com.github.lwhite1.tablesaw.table.ViewGroup;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import static com.github.lwhite1.tablesaw.reducing.NumericReduceUtils.mean;
import static com.github.lwhite1.tablesaw.reducing.NumericReduceUtils.sum;

/**
 *
 */
public class Taxi {

  static Table trips;

  public static void main(String[] args) throws Exception {
    Stopwatch stopwatch = Stopwatch.createStarted();
    System.out.println("loading");
    //trips = CsvReader.read("/Users/larrywhite/IdeaProjects/testdata/bigdata/yellow_tripdata_2015-06.csv");
    trips = StorageManager.readTable("/Users/larrywhite/IdeaProjects/testdata/bigdata/Trips.saw");
    System.out.println(String.format("loaded %d records in %d seconds",
        trips.rowCount(),
        stopwatch.elapsed(TimeUnit.SECONDS)));
    out(trips.shape());
    out(trips.structure().print());
    //trips.setName("Trips");
    //trips.save("/Users/larrywhite/IdeaProjects/testdata/bigdata/");
    Column c = trips.column("total_amount");

    stopwatch.reset().start();
    // Create a grouping for subsequent queries
    ViewGroup vg = ViewGroup.create(trips, "passenger_count");
    out(String.format("Grouped %d records in %d ms",
        trips.rowCount(),
        stopwatch.elapsed(TimeUnit.MILLISECONDS)));

    stopwatch.reset().start();
    Table mean_total_fare = vg.reduce("total_amount", mean);
    out(String.format("Averaged %d records in %d ms",
        trips.rowCount(),
        stopwatch.elapsed(TimeUnit.MILLISECONDS)));
    out(mean_total_fare.print());

    stopwatch.reset().start();
    Table sum_total_fare = vg.reduce("total_amount", sum);
    out(String.format("Summed %d records in %d ms",
        trips.rowCount(),
        stopwatch.elapsed(TimeUnit.MILLISECONDS)));
    out(sum_total_fare.print());

    sum_total_fare = trips.sum("total_amount").by("passenger_count");
  }

  private static void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }

}
