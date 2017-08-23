package tech.tablesaw.examples;

import static tech.tablesaw.api.QueryHelper.*;

import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.Table;

/**
 * Usage example using a Tornado dataset
 */
public class ServiceExample {

    public static void main(String[] args) throws Exception {

        Table ops = Table.create("../data/operations.csv");

        out(ops.structure());

        out(ops);

        DateTimeColumn start = ops.dateColumn("Date").atTime(ops.timeColumn("Start"));
        DateTimeColumn end = ops.dateColumn("Date").atTime(ops.timeColumn("End"));

        for (int row : ops) {
            if (ops.timeColumn("End").get(row).isBefore(ops.timeColumn("Start").get(row))) {
                end.get(row).plusDays(1);
            }
        }

        // Calc duration
        LongColumn duration = start.differenceInSeconds(end);
        ops.addColumn(duration);
        duration.setName("Duration");

        out(ops);

        Table q2_429_assembly = ops.selectWhere(
                allOf
                        (column("date").isInQ2(),
                                (column("SKU").startsWith("429")),
                                (column("Operation").isEqualTo("Assembly"))));

        Table durationByFacilityAndShift = q2_429_assembly.median("Duration").by("Facility", "Shift");

        out(durationByFacilityAndShift);

        durationByFacilityAndShift.write().csv("/tmp/durationByFacilityAndShift.csv");
    }

    private static void out(Object obj) {
        System.out.println(String.valueOf(obj));
    }

}
