package com.github.lwhite1.tablesaw.api.ml;

import com.github.lwhite1.tablesaw.api.BooleanColumn;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.api.ml.classification.LogisticRegression;
import com.github.lwhite1.tablesaw.api.plot.Bar;
import com.github.lwhite1.tablesaw.api.plot.Pareto;
import com.github.lwhite1.tablesaw.reducing.NumericSummaryTable;
import com.github.lwhite1.tablesaw.store.StorageManager;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

import static com.github.lwhite1.tablesaw.api.ColumnType.*;
import static com.github.lwhite1.tablesaw.api.QueryHelper.*;
import static com.github.lwhite1.tablesaw.reducing.NumericReduceUtils.mean;
import static java.lang.System.out;

/**
 *
 */
public class AirlineDelays {

  private static Table flt2007;

  public static void main(String[] args) throws Exception {

    new AirlineDelays();
  }

  private AirlineDelays() throws Exception {
    Stopwatch stopwatch = Stopwatch.createStarted();
    out.println("loading");
    ColumnType[] columnTypes = {
        SHORT_INT,  // 0     Year
        SHORT_INT,  // 1     Month
        SHORT_INT,  // 2     DayofMonth
        SHORT_INT,  // 3     DayOfWeek
        LOCAL_TIME,  // 4     DepTime
        LOCAL_TIME,  // 5     CRSDepTime
        LOCAL_TIME,  // 6     ArrTime
        LOCAL_TIME,  // 7     CRSArrTime
        CATEGORY,   // 8     UniqueCarrier
        SHORT_INT,  // 9     FlightNum
        CATEGORY,   // 10    TailNum
        SHORT_INT,  // 11    ActualElapsedTime
        SHORT_INT,  // 12    CRSElapsedTime
        SHORT_INT,  // 13    AirTime
        SHORT_INT,  // 14    ArrDelay
        SHORT_INT,  // 15    DepDelay
        CATEGORY,   // 16    Origin
        CATEGORY,   // 17    Dest
        SHORT_INT,  // 18    Distance
        SHORT_INT,  // 19    TaxiIn
        SHORT_INT,  // 20    TaxiOut
        SHORT_INT,  // 21    Cancelled
        CATEGORY,   // 22    CancellationCode
        SHORT_INT,  // 23    Diverted
        SHORT_INT,  // 24    CarrierDelay
        SHORT_INT,  // 25    WeatherDelay
        SHORT_INT,  // 26    NASDelay
        SHORT_INT,  // 27    SecurityDelay
        SHORT_INT,  // 28    LateAircraftDelay
    };

  //  flt2007 = Table.createFromCsv(columnTypes, "/Users/larrywhite/Downloads/flight delays/2007.csv");


    //String tableName = StorageManager.saveTable("bigdata", flt2007);
    //out("Wrote to saw store " + tableName);

    flt2007 = StorageManager.readTable("bigdata/2007.csv.saw");
    out.println(String.format("loaded %d records in %d seconds",
        flt2007.rowCount(),
        (int) stopwatch.elapsed(TimeUnit.SECONDS)));

    out(flt2007.shape());


    Table ord = flt2007.selectWhere(
        both(column("Origin").isEqualTo("ORD"),
             column("DepDelay").isNotMissing()));

    BooleanColumn delayed = ord.selectIntoColumn("Delayed?", column("DepDelay").isGreaterThanOrEqualTo(15));
    ord.addColumn(delayed);

    out("total flights: " + ord.rowCount());
    out("total delays: " + delayed.countTrue());

    // Compute average number of delayed flights per month

    NumericSummaryTable monthGroup = ord.summarize("DepDelay", mean).by("Month");
    Bar.show("Departure delay by month", monthGroup);

    NumericSummaryTable dayOfWeekGroup = ord.summarize("DepDelay", mean).by("DayOfWeek");
    Bar.show("Departure delay by day-of-week", dayOfWeekGroup);

    ord.addColumn(ord.timeColumn("CRSDepTime").hour());
    NumericSummaryTable hourGroup = ord.summarize("DepDelay", mean).by("CRSDepTime[hour]");
    Bar.show("Departure delay by hour-of-day", hourGroup);

    // Compute average number of delayed flights per carrier
    NumericSummaryTable carrierGroup = ord.mean("DepDelay").by("UniqueCarrier");
    Pareto.show("Departure delay by Carrier", carrierGroup);

    // we have no cancelled flights because we removed them earlier by filtering where delay is missing;
    out(ord.shape());

    double lambda = 0.1;
    LogisticRegression logit = LogisticRegression.learn(
        ord.booleanColumn("Delayed?"),
        ord.nCol("dayOfWeek"),
        ord.nCol("CRSDepTime[hour]"));

    out(logit.toString());
  }

  private static void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }
}
