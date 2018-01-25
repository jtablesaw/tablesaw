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
    public void testGetDouble() {
        LongColumn column = createLongColumn(new long[]{20L, 32452345, MISSING_VALUE, 234});
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

    @Test
    public void testSubtractDoubleColumn() {
        long[] col1Values = new long[]{32, LongColumn.MISSING_VALUE, 42, 57, 52};
        double[] col2Values = new double[]{31.5, 42, 38.67, DoubleColumn.MISSING_VALUE, 52.01, 102};
        double[] expected = new double[]{0.5, DoubleColumn.MISSING_VALUE, 3.33, DoubleColumn.MISSING_VALUE, -.01};

        LongColumn col1 = createLongColumn(col1Values);
        DoubleColumn col2 = new DoubleColumn("Test", col2Values.length);
        Arrays.stream(col2Values).forEach(col2::append);

        NumericColumn difference = col1.subtract(col2);
        assertTrue("Expecting DoubleColumn type result", difference instanceof DoubleColumn);

        DoubleColumn diffDoubleCol = (DoubleColumn) difference;
        assertEquals("Both sets of data should be the same size.", expected.length, diffDoubleCol.size());
        for (int index = 0; index < expected.length; index++) {
            double actual = diffDoubleCol.get(index);
            assertEquals("value mismatch at index:" + index, expected[index], actual, 0.01);
        }
    }

    @Test
    public void testSubtractIntColumn() {
        long[] col1Values = new long[]{32, MISSING_VALUE, 38, 57, 52};
        int[] col2Values = new int[]{31, 42, 42, IntColumn.MISSING_VALUE, 51, 102};
        long[] expected = new long[]{1, MISSING_VALUE, -4, MISSING_VALUE, 1};

        LongColumn col1 = createLongColumn(col1Values);
        IntColumn col2 = new IntColumn("Test", col1Values.length);
        Arrays.stream(col2Values).forEach(col2::append);

        NumericColumn difference = col1.subtract(col2);
        assertTrue("Expecting LongColumn type result", difference instanceof LongColumn);
        LongColumn diffLongCol = (LongColumn) difference;
        validateDifferenceColumn(expected, diffLongCol);
    }

    @Test
    public void testSubtract2Columns() {
        long[] col1Values = new long[]{32, MISSING_VALUE, 42, 57, 52};
        long[] col2Values = new long[]{32, 42, 38, MISSING_VALUE, 52, 102};
        long[] expected = new long[]{0, MISSING_VALUE, 4, MISSING_VALUE, 0};

        LongColumn col1 = createLongColumn(col1Values);
        LongColumn col2 = createLongColumn(col2Values);

        LongColumn difference = LongColumn.subtractLong(col1, col2);
        validateDifferenceColumn(expected, difference);

        // change order to verify size of returned column
        difference = LongColumn.subtractLong(col2, col1);
        expected = new long[]{0, MISSING_VALUE, -4, MISSING_VALUE, 0};
        validateDifferenceColumn(expected, difference);
    }

    private LongColumn createLongColumn(long[] values) {
        LongColumn column = new LongColumn("Test", values.length);
        Arrays.stream(values).forEach(column::append);
        return column;
    }
}
