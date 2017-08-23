package tech.tablesaw;

import org.junit.Before;
import org.junit.Test;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import static org.junit.Assert.*;

/**
 * Tests for Table
 */
public class TableTest {

    private Table table;
    private FloatColumn floatColumn = new FloatColumn("f1");
    private DoubleColumn doubleColumn = new DoubleColumn("d1");

    @Before
    public void setUp() throws Exception {
        table = Table.create("t");
        table.addColumn(floatColumn);
    }

    @Test
    public void testColumn() throws Exception {
        Column column1 = table.column(0);
        assertNotNull(column1);
    }

    @Test
    public void testFullCopy() throws Exception {
        doubleColumn.append(2.23424);
        Table t = Table.create("test");
        t.addColumn(doubleColumn);
        Table c = t.fullCopy();
        DoubleColumn doubles = c.doubleColumn(0);
        assertNotNull(doubles);
        assertEquals(1, doubles.size());
    }

    @Test
    public void testColumnCount() throws Exception {
        assertEquals(0, Table.create("t").columnCount());
        assertEquals(1, table.columnCount());
    }

    @Test
    public void testSampleSplit() throws Exception {
        Table t = Table.read().csv("../data/BushApproval.csv");
        Table[] results = t.sampleSplit(.75);
        assertEquals(t.rowCount(), results[0].rowCount() + results[1].rowCount());
    }

    @Test
    public void testRowCount() throws Exception {
        assertEquals(0, table.rowCount());
        FloatColumn floatColumn = this.floatColumn;
        floatColumn.append(2f);
        assertEquals(1, table.rowCount());
        floatColumn.append(2.2342f);
        assertEquals(2, table.rowCount());
    }
}