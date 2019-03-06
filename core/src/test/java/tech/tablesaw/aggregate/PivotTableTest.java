package tech.tablesaw.aggregate;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PivotTableTest {

    @Test
    public void pivot() throws Exception {
        Table t = Table.read().csv(
                CsvReadOptions.builder("../data/bush.csv")
                        .missingValueIndicator(":")
                        .build());
        t.addColumns(t.dateColumn("date").year());

        Table pivot = PivotTable.pivot(t,
                t.categoricalColumn("who"),
                t.categoricalColumn("date year"),
                t.numberColumn("approval"),
                AggregateFunctions.mean);
        assertTrue(pivot.columnNames().contains("who"));
        assertTrue(pivot.columnNames().contains("2001"));
        assertTrue(pivot.columnNames().contains("2002"));
        assertTrue(pivot.columnNames().contains("2003"));
        assertTrue(pivot.columnNames().contains("2004"));
        assertEquals(6, pivot.rowCount());
    }
}