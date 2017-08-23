package tech.tablesaw.api;

import org.junit.Test;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import static org.junit.Assert.*;

/**
 *
 */
public class TableTest {

    @Test
    public void testGetAndRemoveColumn() throws Exception {
        Table table = Table.read().csv("../data/tornadoes_1950-2014.csv");
        assertTrue(table.columnNames().contains("Width"));

        Column c = table.getAndRemoveColumn("Width");

        assertNotNull(c);
        assertFalse(table.columnNames().contains("Width"));

        Column c1 = table.getAndRemoveColumn(0);
        assertNotNull(c1);
    }
}
