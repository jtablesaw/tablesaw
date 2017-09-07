package tech.tablesaw;

import org.junit.Before;
import org.junit.Test;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.util.Random;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

/**
 * Tests for Table
 */
public class TableTest {

    private static final int ROWS_BOUNDARY = 1000;

    private Table table;
    private FloatColumn floatColumn = new FloatColumn("f1");
    private DoubleColumn doubleColumn = new DoubleColumn("d1");
    private Random random = new Random();

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

    @Test
    public void testAppend() throws Exception {
        int appendSize = random.nextInt(ROWS_BOUNDARY);
        appendTableWithSingleColumn(appendSize);
    }

    @Test
    public void testAppendEmptyTable() throws Exception {
        appendTableWithSingleColumn(0);
    }

    @Test
    public void testAppendToNonEmptyTable() throws Exception {
        int appendSize = random.nextInt(ROWS_BOUNDARY);
        populateColumn(floatColumn);
        assumeFalse(table.isEmpty());
        appendTableWithSingleColumn(appendSize);
    }

    @Test
    public void testAppendEmptyTableToNonEmptyTable() throws Exception {
        populateColumn(floatColumn);
        assumeFalse(table.isEmpty());
        appendTableWithSingleColumn(0);
    }

    @Test
    public void testAppendMultipleColumns() throws Exception {
        FloatColumn column = new FloatColumn("e1");
        table.addColumn(column);
        FloatColumn first = floatColumn.emptyCopy();
        FloatColumn second = column.emptyCopy();
        int firstColumnSize = populateColumn(first);
        int secondColumnSize = populateColumn(second);
        Table tableToAppend = Table.create("populated", first, second);
        table.append(tableToAppend);
        assertColumnSize(floatColumn, firstColumnSize);
        assertColumnSize(column, secondColumnSize);
    }

    @Test(expected = NullPointerException.class)
    public void testAppendNull() throws Exception {
        table.append(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testAppendTableWithNonExistingColumns() throws Exception {
        Table tableToAppend = Table.create("wrong", doubleColumn);
        table.append(tableToAppend);
    }

    @Test(expected = IllegalStateException.class)
    public void testAppendTableWithAnotherColumnName() throws Exception {
        FloatColumn column = new FloatColumn("42");
        Table tableToAppend = Table.create("wrong", column);
        table.append(tableToAppend);
    }

    @Test(expected = IllegalStateException.class)
    public void testAppendTableWithDifferentShape() throws Exception {
        FloatColumn column = new FloatColumn("e1");
        table.addColumn(column);
        Table tableToAppend = Table.create("different", column);
        assumeTrue(table.columns().size() == 2);
        assumeTrue(tableToAppend.columns().size() == 1);
        table.append(tableToAppend);
    }

    private void appendTableWithSingleColumn(final int rowsToAppend) {
        FloatColumn column = floatColumn.emptyCopy();
        populateColumn(column, rowsToAppend);
        Table tableToAppend = Table.create("populated", column);
        int initialSize = table.rowCount();
        table.append(tableToAppend);
        assertColumnSize(floatColumn, initialSize + rowsToAppend);
    }

    private void assertColumnSize(Column column, int expected) {
        int actual = table.column(column.name()).size();
        assertEquals(expected, actual);
    }

    private int populateColumn(final FloatColumn floatColumn) {
        int rowsCount = random.nextInt(ROWS_BOUNDARY);
        return populateColumn(floatColumn, rowsCount);
    }

    private int populateColumn(final FloatColumn floatColumn, final int rowsCount) {
        for (int i = 0; i < rowsCount; i++) {
            floatColumn.append(random.nextFloat());
        }
        assumeTrue(floatColumn.size() == rowsCount);
        return rowsCount;
    }
}