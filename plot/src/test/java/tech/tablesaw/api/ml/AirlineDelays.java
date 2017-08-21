package tech.tablesaw.api.ml;

import com.google.common.base.Stopwatch;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.ml.classification.LogisticRegression;
import tech.tablesaw.api.plot.Bar;
import tech.tablesaw.api.plot.Pareto;
import tech.tablesaw.reducing.NumericSummaryTable;
import tech.tablesaw.store.StorageManager;

import java.util.concurrent.TimeUnit;

import static java.lang.System.out;
import static tech.tablesaw.api.QueryHelper.*;
import static tech.tablesaw.reducing.NumericReduceUtils.mean;

/**
 *
 */
public class AirlineDelays {

    private static Table flt2007;

    private AirlineDelays() throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        out.println("loading");

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

        LogisticRegression logit = LogisticRegression.learn(
                ord.booleanColumn("Delayed?"),
                ord.nCol("dayOfWeek"),
                ord.nCol("CRSDepTime[hour]"));

        out(logit.toString());
    }

    public static void main(String[] args) throws Exception {

        new AirlineDelays();
    }

    private static void out(Object obj) {
        System.out.println(String.valueOf(obj));
    }
}
