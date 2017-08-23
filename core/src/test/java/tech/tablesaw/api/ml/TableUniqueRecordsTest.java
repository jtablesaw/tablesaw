package tech.tablesaw.api.ml;

import org.junit.Before;
import org.junit.Test;

import tech.tablesaw.api.Table;

import static org.junit.Assert.*;

/**
 *
 */
public class TableUniqueRecordsTest {

    private Table table;
    private Table table2;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv("../data/BushApproval.csv");
        table = table.first(4);
        table2 = Table.create("2 column version");
        table2.addColumn(table.column(1), table.column(2));
    }

    @Test
    public void testUniqueRecord() throws Exception {

        Table uniques = table.uniqueRecords();
        assertEquals(table.rowCount(), uniques.rowCount());
        assertTrue(table.columnCount() == uniques.columnCount());

        Table uniques2 = table2.uniqueRecords();
        assertTrue(table2.rowCount() > uniques2.rowCount());
        assertEquals(table2.columnCount(), uniques2.columnCount());
    }
}
