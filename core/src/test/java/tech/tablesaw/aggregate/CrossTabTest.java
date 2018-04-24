package tech.tablesaw.aggregate;

import org.junit.Test;
import tech.tablesaw.api.Table;

import static org.junit.Assert.*;

public class CrossTabTest {

    @Test
    public void testCounts1() throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        Table counts = CrossTab.counts(bush, "who");
        Table pcts = CrossTab.percents(bush, "who");
        double sum = counts.numberColumn("Count").sum();
        for (int row = 0; row < pcts.rowCount(); row++) {
            assertEquals(counts.numberColumn("Count").get(row) / sum,
                    pcts.numberColumn(1).get(row),
                    0.01);
        }
    }

    @Test
    public void testCounts2() throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        Table counts = CrossTab.counts(bush, "date");
        Table pcts = CrossTab.percents(bush, "date");
        double sum = counts.numberColumn("Count").sum();
        for (int row = 0; row < pcts.rowCount(); row++) {
            assertEquals(counts.numberColumn("Count").get(row) / sum,
                    pcts.numberColumn(1).get(row),
                    0.01);
        }
    }

    @Test
    public void testColumnPercents() throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        bush.addColumns(bush.dateColumn("date").year());
        Table xtab = CrossTab.columnPercents(bush, "who", "date year");
        assertEquals(1, xtab.numberColumn(1).get(xtab.rowCount()-1), 0.001);
    }

    @Test
    public void testRowPercents() throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        bush.addColumns(bush.dateColumn("date").year());
        Table xtab = CrossTab.rowPercents(bush, "who", "date year");
        assertEquals(1, xtab.numberColumn( xtab.columnCount() - 1).get(0), 0.001);
    }

    @Test
    public void testTablePercents() throws Exception {
        Table bush = Table.read().csv("../data/bush.csv");
        bush.addColumns(bush.dateColumn("date").year());
        Table xtab = CrossTab.tablePercents(bush, "who", "date year");
        assertEquals(1, xtab.numberColumn( xtab.columnCount() - 1).get(xtab.rowCount()-1), 0.001);
    }
}