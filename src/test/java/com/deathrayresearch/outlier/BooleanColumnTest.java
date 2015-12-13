package com.deathrayresearch.outlier;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for BooleanColumn
 */
public class BooleanColumnTest {

    private final BooleanColumn column = BooleanColumn.create("Test");

    @Before
    public void setup() {
        column.add(false);
        column.add(false);
        column.add(false);
        column.add(false);
        column.add(true);
        column.add(true);
        column.add(false);
    }

    @Test
    public void testGetElements() throws Exception {
        assertEquals(7, column.size());
    }

    @Test
    public void testAddCell() throws Exception {
        column.add(true);
        assertEquals(8, column.size());

        // Add some other types and ensure that they're correctly truthy
        column.addCell("true");
        assertTrue(lastEntry());
        column.addCell("false");
        assertFalse(lastEntry());
        column.addCell("TRUE");
        assertTrue(lastEntry());
        column.addCell("FALSE");
        assertFalse(lastEntry());
        column.addCell("T");
        assertTrue(lastEntry());
        column.addCell("F");
        assertFalse(lastEntry());
        column.addCell("Y");
        assertTrue(lastEntry());
        column.addCell("N");
        assertFalse(lastEntry());
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals("Boolean".toUpperCase(), column.type().name());
    }

    /**
     * Returns true if the last item added to the column is true and false otherwise
     */
    private boolean lastEntry() {
        return column.get(column.size() - 1);
    }
}
