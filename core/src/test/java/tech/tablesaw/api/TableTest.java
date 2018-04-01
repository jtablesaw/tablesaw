/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.api;

import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.columns.Column;

import java.util.Random;

import static org.junit.Assert.*;
import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.stdDev;

/**
 * Tests for Table
 */
public class TableTest {

    private static final int ROWS_BOUNDARY = 1000;
    private static final Random RANDOM = new Random();

    private Table table;
    private FloatColumn floatColumn = new FloatColumn("f1");
    private NumberColumn numberColumn = new NumberColumn("d1");

    @Before
    public void setUp() throws Exception {
        table = Table.create("t");
        table.addColumn(floatColumn);
    }

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

    @Test
    public void testSummarize() throws Exception {
        Table table = Table.read().csv("../data/tornadoes_1950-2014.csv");
        Table result = table.summarize("Injuries", mean, stdDev).by("State");
        assertEquals(49, result.rowCount());
        assertEquals(3, result.columnCount());
        assertEquals("4.580805569368455", result.column(1).getString(0));
    }

    @Test
    public void testColumn() throws Exception {
        Column column1 = table.column(0);
        assertNotNull(column1);
    }

    @Test
    public void testFullCopy() throws Exception {
        numberColumn.append(2.23424);
        Table t = Table.create("test");
        t.addColumn(numberColumn);
        Table c = t.fullCopy();
        NumberColumn doubles = c.doubleColumn(0);
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
        int appendedRows = appendRandomlyGeneratedColumn(table);
        assertTableColumnSize(table, floatColumn, appendedRows);
    }

    @Test
    public void testAppendEmptyTable() throws Exception {
        appendEmptyColumn(table);
        assertTrue(table.isEmpty());
    }

    @Test
    public void testAppendToNonEmptyTable() throws Exception {
        populateColumn(floatColumn);
        assertFalse(table.isEmpty());
        int initialSize = table.rowCount();
        int appendedRows = appendRandomlyGeneratedColumn(table);
        assertTableColumnSize(table, floatColumn, initialSize + appendedRows);
    }

    @Test
    public void testAppendEmptyTableToNonEmptyTable() throws Exception {
        populateColumn(floatColumn);
        assertFalse(table.isEmpty());
        int initialSize = table.rowCount();
        appendEmptyColumn(table);
        assertTableColumnSize(table, floatColumn, initialSize);
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
        assertTableColumnSize(table, floatColumn, firstColumnSize);
        assertTableColumnSize(table, column, secondColumnSize);
    }

    @Test(expected = NullPointerException.class)
    public void testAppendNull() throws Exception {
        table.append(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testAppendTableWithNonExistingColumns() throws Exception {
        Table tableToAppend = Table.create("wrong", numberColumn);
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
        assertTrue(table.columns().size() == 2);
        assertTrue(tableToAppend.columns().size() == 1);
        table.append(tableToAppend);
    }

    @Test
    public void testReplaceColumn() throws Exception {
        FloatColumn first = new FloatColumn("c1", new float[]{1, 2, 3, 4, 5});
        FloatColumn second = new FloatColumn("c2", new float[]{6, 7, 8, 9, 10});
        FloatColumn replacement = new FloatColumn("c2", new float[]{10, 20, 30, 40, 50});

        Table t = Table.create("populated", first, second);

        int colIndex = t.columnIndex(second);
        assertTrue(t.column("c2") == second);
        t.replaceColumn("c2", replacement);
        assertTrue(t.column("c1") == first);
        assertTrue(t.column("c2") == replacement);
        assertTrue(t.columnIndex(replacement) == colIndex);
    }

    private int appendRandomlyGeneratedColumn(Table table) {
        FloatColumn column = floatColumn.emptyCopy();
        populateColumn(column);
        return appendColumn(table, column);
    }

    private void appendEmptyColumn(Table table) {
        FloatColumn column = floatColumn.emptyCopy();
        appendColumn(table, column);
    }

    private int appendColumn(Table table, Column column) {
        Table tableToAppend = Table.create("populated", column);
        table.append(tableToAppend);
        return column.size();
    }

    private void assertTableColumnSize(Table table, Column column, int expected) {
        int actual = table.column(column.name()).size();
        assertEquals(expected, actual);
    }

    private int populateColumn(FloatColumn floatColumn) {
        int rowsCount = RANDOM.nextInt(ROWS_BOUNDARY);
        for (int i = 0; i < rowsCount; i++) {
            floatColumn.append(RANDOM.nextFloat());
        }
        assertTrue(floatColumn.size() == rowsCount);
        return rowsCount;
    }

    @Test
    public void testAsMatrix() throws Exception {
        NumberColumn first = new NumberColumn("c1", new double[]{1l, 2l, 3l, 4l, 5l});
        NumberColumn second = new NumberColumn("c2", new double[]{6.0f, 7.0f, 8.0f, 9.0f, 10.0f});
        NumberColumn third = new NumberColumn("c3", new double[]{10.0, 20.0, 30.0, 40.0, 50.0});

        Table t = Table.create("table", first, second, third);
        double[][] matrix = t.asMatrix();
        assertEquals(5, matrix.length);
        assertArrayEquals(new double[]{1.0, 6.0, 10.0}, matrix[0], 0.0000001);
        assertArrayEquals(new double[]{2.0, 7.0, 20.0}, matrix[1], 0.0000001);
        assertArrayEquals(new double[]{3.0, 8.0, 30.0}, matrix[2], 0.0000001);
        assertArrayEquals(new double[]{4.0, 9.0, 40.0}, matrix[3], 0.0000001);
        assertArrayEquals(new double[]{5.0, 10.0, 50.0}, matrix[4], 0.0000001);
    }

}
