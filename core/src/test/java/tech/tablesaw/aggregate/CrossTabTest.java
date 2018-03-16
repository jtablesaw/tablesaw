package tech.tablesaw.aggregate;

import org.junit.Test;
import tech.tablesaw.api.Table;

import static org.junit.Assert.*;

public class CrossTabTest {

    @Test
    public void counts() throws Exception {
        Table bush = Table.read().csv("../data/BushApproval.csv");
        Table counts = CrossTab.counts(bush, "who");
        Table pcts = CrossTab.percents(bush, "who");
        double sum = counts.intColumn("Count").sum();
        for (int row : pcts) {
            assertEquals(counts.intColumn("Count").get(row) / sum,
                    pcts.floatColumn(1).get(row),
                    0.01);
        }
    }
}