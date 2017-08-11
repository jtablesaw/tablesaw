package tech.tablesaw;

import org.junit.Before;
import org.junit.Test;

import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import static org.junit.Assert.*;

/**
 * Tests for Table
 */
public class TableTest {

    private Table table;
    private FloatColumn column = new FloatColumn("f1");

    @Before
    public void setUp() throws Exception {
        table = Table.create("t");
        table.addColumn(column);
    }

    @Test
    public void testColumn() throws Exception {
        Column column1 = table.column(0);
        assertNotNull(column1);
    }

    @Test
    public void testColumnCount() throws Exception {
        assertEquals(0, Table.create("t").columnCount());
        assertEquals(1, table.columnCount());
    }

    @Test
    public void testSampleSplit() throws Exception {
        Table t = Table.createFromCsv("../data/BushApproval.csv");
        Table[] results = t.sampleSplit(.75);
        assertEquals(t.rowCount(), results[0].rowCount() + results[1].rowCount());
    }

    @Test
    public void testRowCount() throws Exception {
        assertEquals(0, table.rowCount());
        FloatColumn floatColumn = column;
        floatColumn.append(2f);
        assertEquals(1, table.rowCount());

        floatColumn.append(2.2342f);
        assertEquals(2, table.rowCount());
    }
}