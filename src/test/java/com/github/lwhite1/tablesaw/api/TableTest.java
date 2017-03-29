package com.github.lwhite1.tablesaw.api;

import com.github.lwhite1.tablesaw.columns.Column;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class TableTest {

    @Test
    public void testGetAndRemoveColumn() throws Exception {
        Table table = Table.createFromCsv("data/tornadoes_1950-2014.csv");
        assertTrue(table.columnNames().contains("Width"));

        Column c = table.getAndRemoveColumn("Width");

        assertNotNull(c);
        assertFalse(table.columnNames().contains("Width"));

        Column c1 = table.getAndRemoveColumn(0);
        assertNotNull(c1);
    }
}
