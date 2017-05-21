package com.github.lwhite1.tablesaw.index;

import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.FloatColumnUtils;
import com.github.lwhite1.tablesaw.util.Selection;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */
public class FloatIndexTest {

    private FloatIndex index;
    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.createFromCsv("data/bus_stop_test.csv");
        index = new FloatIndex(table.floatColumn("stop_lat"));
    }

    @Test
    public void testGet() {
        Selection fromCol = table.floatColumn("stop_lat").select(FloatColumnUtils.isEqualTo, 30.330425f);
        Selection fromIdx = index.get(30.330425f);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGTE() {
        Selection fromCol = table.floatColumn("stop_lat").select(FloatColumnUtils.isGreaterThanOrEqualTo, 30.330425f);
        Selection fromIdx = index.atLeast(30.330425f);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLTE() {
        Selection fromCol = table.floatColumn("stop_lat").select(FloatColumnUtils.isLessThanOrEqualTo, 30.330425f);
        Selection fromIdx = index.atMost(30.330425f);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLT() {
        Selection fromCol = table.floatColumn("stop_lat").select(FloatColumnUtils.isLessThan, 30.330425f);
        Selection fromIdx = index.lessThan(30.330425f);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGT() {
        Selection fromCol = table.floatColumn("stop_lat").select(FloatColumnUtils.isGreaterThan, 30.330425f);
        Selection fromIdx = index.greaterThan(30.330425f);
        assertEquals(fromCol, fromIdx);
    }
}