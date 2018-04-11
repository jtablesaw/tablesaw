package tech.tablesaw.aggregate;

import org.junit.Test;
import tech.tablesaw.api.Table;

import static org.junit.Assert.*;

public class CrossTabTest {

    @Test
    public void counts() throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        Table counts = CrossTab.counts(bush, "who");
        Table pcts = CrossTab.percents(bush, "who");
        double sum = counts.numberColumn("Count").sum();
        for (int row : pcts) {
            assertEquals(counts.numberColumn("Count").get(row) / sum,
                    pcts.numberColumn(1).get(row),
                    0.01);
        }
    }

    @Test
    public void counts2() throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        Table counts = CrossTab.counts(bush, "date");
        Table pcts = CrossTab.percents(bush, "date");
        double sum = counts.numberColumn("Count").sum();
        for (int row : pcts) {
            assertEquals(counts.numberColumn("Count").get(row) / sum,
                    pcts.numberColumn(1).get(row),
                    0.01);
        }
    }
}