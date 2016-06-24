package com.github.lwhite1.tablesaw.examples;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.DateTimeColumn;
import com.github.lwhite1.tablesaw.columns.LongColumn;
import it.unimi.dsi.fastutil.floats.FloatArrayList;

import static com.github.lwhite1.tablesaw.aggregator.NumericReduceUtils.median;
import static com.github.lwhite1.tablesaw.api.QueryHelper.allOf;
import static com.github.lwhite1.tablesaw.api.QueryHelper.column;

/**
 * Usage example using a Tornado dataset
 */
public class ServiceExample {

  public static void main(String[] args) throws Exception {

    Table ops = Table.create("data/operations.csv");

    out(ops.structure().print());

    out(ops.print());

    DateTimeColumn start = ops.dateColumn("Date").atTime(ops.timeColumn("Start-Time"));
    DateTimeColumn end = ops.dateColumn("Date").atTime(ops.timeColumn("End-Time"));

    // Calc duration
    LongColumn duration = start.differenceInSeconds(end);
    ops.addColumn(duration);
    duration.setName("Duration");

    out(ops.print());

    Table q2_429_assembly = ops.selectWhere(
          allOf
              (column("date").isInQ2(),
              (column("SKU").startsWith("429")),
              (column("Operation").isEqualTo("Assembly"))));

    Table durationByFacilityAndShift = q2_429_assembly.reduce("Duration", median, "Facility", "Shift");
    FloatArrayList tops = durationByFacilityAndShift.floatColumn("Median").top(5);

    out(durationByFacilityAndShift.print());

    durationByFacilityAndShift.exportToCsv("tmp/durationByFacilityAndShift.csv");
  }

  private static void out(Object obj) {
    System.out.println(String.valueOf(obj));
  }

  private static void out() {
    System.out.println("");
  }

}