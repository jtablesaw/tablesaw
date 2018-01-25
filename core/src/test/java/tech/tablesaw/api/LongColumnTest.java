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

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static tech.tablesaw.api.LongColumn.MISSING_VALUE;

/**
 * Tests for Long columns
 */
public class LongColumnTest {
    @Test
    public void testDifference() {
        long[] originalValues = new long[]{32, 42, 40, 57, 52};
        long[] expectedValues = new long[]{MISSING_VALUE, 10, -2, 17, -5};
        assertTrue(computeAndValidateDifference(originalValues, expectedValues));
    }

    @Test
    public void testMissingValuesInColumn() {
        long[] originalValues = new long[]{32, 42, MISSING_VALUE, 57, 52};
        long[] expectedValues = new long[]{MISSING_VALUE, 10, MISSING_VALUE, MISSING_VALUE, -5};
        assertTrue(computeAndValidateDifference(originalValues, expectedValues));
    }

    private boolean computeAndValidateDifference(long[] originalValues, long[] expectedValues) {
        LongColumn initial = createLongColumn(originalValues);
        LongColumn difference = initial.difference();

        assertEquals("Both sets of data should be the same size.", expectedValues.length, difference.size());
        validateDifferenceColumn(expectedValues, difference);
        return true;
    }

    @Test
    public void testDifferenceEmptyColumn() {
        LongColumn initial = new LongColumn("Test");
        LongColumn difference = initial.difference();
        assertEquals("Expecting empty data set.", 0, difference.size());
    }

    @Test
    public void testGetInt() {
        LongColumn column = createLongColumn(new long[]{20L, 32452345, LongColumn.MISSING_VALUE, 234});
        assertEquals("Primitive type conversion error", 20, column.getInt(0));
        assertEquals("Primitive type conversion error", 32452345, column.getInt(1));
        assertEquals("Primitive type conversion error", IntColumn.MISSING_VALUE, column.getInt(2));
        assertEquals("Primitive type conversion error", 234, column.getInt(3));
    }

    @Test
    public void testGetFloat() {
        LongColumn column = createLongColumn(new long[]{20L, 32452345, LongColumn.MISSING_VALUE, 234});
        assertEquals("Primitive type conversion error", 20.0, column.getFloat(0), 0.1);
        assertEquals("Primitive type conversion error", 32452345.0, column.getFloat(1), 1);
        assertTrue("Primitive type conversion error", Float.isNaN(column.getFloat(2)));
        assertEquals("Primitive type conversion error", 234.0, column.getFloat(3), 0.1);
    }

    @Test
    public void testGetDouble() {
        LongColumn column = createLongColumn(new long[]{20L, 32452345, LongColumn.MISSING_VALUE, 234});
        assertEquals("Primitive type conversion error", 20.0, column.getDouble(0), 0.1);
        assertEquals("Primitive type conversion error", 32452345.0, column.getDouble(1), 1);
        assertTrue("Primitive type conversion error", Double.isNaN(column.getDouble(2)));
        assertEquals("Primitive type conversion error", 234.0, column.getDouble(3), 0.1);
    }

    private void validateDifferenceColumn(long[] expectedValues, LongColumn difference) {
        for (int index = 0; index < difference.size(); index++) {
            long actual = difference.get(index);
            assertEquals("difference operation at index:" + index + " failed", expectedValues[index], actual);
        }
    }

    private LongColumn createLongColumn(long[] values) {
        LongColumn column = new LongColumn("Test", values.length);
        Arrays.stream(values).forEach(column::append);
        return column;
    }
}
