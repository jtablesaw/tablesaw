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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.filtering.IntPredicate;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static tech.tablesaw.api.IntColumn.MISSING_VALUE;
import static tech.tablesaw.api.QueryHelper.column;

/**
 * Tests for int columns
 */
public class IntColumnTest {

    private IntColumn intColumn;

    @Before
    public void setUp() {
        intColumn = new IntColumn("t1");
    }

    @Test
    public void testSum() {
        for (int i = 0; i < 100; i++) {
            intColumn.append(1);
        }
        assertEquals(100, intColumn.sum());
    }

    @Test
    public void testMin() {
        for (int i = 0; i < 100; i++) {
            intColumn.append(i);
        }
        assertEquals(0.0, intColumn.min(), .001);
    }

    @Test
    public void testMax() {
        for (int i = 0; i < 100; i++) {
            intColumn.append(i);
        }
        assertEquals(99, intColumn.max(), .001);
    }

    @Test
    public void testIsLessThan() {
        for (int i = 0; i < 100; i++) {
            intColumn.append(i);
        }
        assertEquals(50, intColumn.isLessThan(50).size());
    }

    @Test
    public void testIsGreaterThan() {
        for (int i = 0; i < 100; i++) {
            intColumn.append(i);
        }
        assertEquals(49, intColumn.isGreaterThan(50).size());
    }

    @Test
    public void testIsGreaterThanOrEqualTo() {
        for (int i = 0; i < 100; i++) {
            intColumn.append(i);
        }
        assertEquals(50, intColumn.isGreaterThanOrEqualTo(50).size());
        assertEquals(50, intColumn.isGreaterThanOrEqualTo(50).get(0));
    }

    @Test
    public void testIsLessThanOrEqualTo() {
        for (int i = 0; i < 100; i++) {
            intColumn.append(i);
        }
        assertEquals(51, intColumn.isLessThanOrEqualTo(50).size());
        assertEquals(49, intColumn.isLessThanOrEqualTo(50).get(49));
    }

    @Test
    public void testIsEqualTo() {
        for (int i = 0; i < 100; i++) {
            intColumn.append(i);
        }
        assertEquals(1, intColumn.isEqualTo(10).size());
    }

    @Test
    public void testPercents() {
        for (int i = 0; i < 100; i++) {
            intColumn.append(i);
        }
        FloatColumn floatColumn = intColumn.asRatio();
        assertEquals(1.0, floatColumn.sum(), 0.1);
    }

    @Test
    public void testSelectIf() {

        for (int i = 0; i < 100; i++) {
            intColumn.append(i);
        }

        IntPredicate predicate = value -> value < 10;
        IntColumn column1 = intColumn.selectIf(predicate);
        assertEquals(10, column1.size());
        for (int i = 0; i < 10; i++) {
            assertTrue(column1.get(i) < 10);
        }
    }

    @Test
    public void testSelect() {

        for (int i = 0; i < 100; i++) {
            intColumn.append(i);
        }

        IntPredicate predicate = value -> value < 10;
        IntColumn column1 = intColumn.selectIf(predicate);
        assertEquals(10, column1.size());

        IntColumn column2 = intColumn.select(intColumn.select(predicate));
        assertEquals(10, column2.size());
        for (int i = 0; i < 10; i++) {
            assertTrue(column1.get(i) < 10);
        }
        for (int i = 0; i < 10; i++) {
            assertTrue(column2.get(i) < 10);
        }
    }

    @Test
    public void testDifference() {
        int[] originalValues = new int[]{32, 42, 40, 57, 52};
        int[] expectedValues = new int[]{MISSING_VALUE, 10, -2, 17, -5};
        computeAndValidateDifference(originalValues, expectedValues);
    }

    @Test
    public void testMissingValuesInColumn() {
        int[] originalValues = new int[]{32, 42, MISSING_VALUE, 57, 52};
        int[] expectedValues = new int[]{MISSING_VALUE, 10, MISSING_VALUE, MISSING_VALUE, -5};
        computeAndValidateDifference(originalValues, expectedValues);
    }

    private void computeAndValidateDifference(int[] originalValues, int[] expectedValues) {
        IntColumn initial = new IntColumn("Test", originalValues.length);
        for (int value : originalValues) {
            initial.append(value);
        }
        IntColumn difference = initial.difference();
        assertEquals("Both sets of data should be the same size.", expectedValues.length, difference.size());
        for (int index = 0; index < difference.size(); index++) {
            int actual = difference.get(index);
            assertEquals("difference operation at index:" + index + " failed", expectedValues[index], actual);
        }
    }

    @Test
    public void testDifferenceEmptyColumn() {
        IntColumn initial = new IntColumn("Test");
        IntColumn difference = initial.difference();
        assertEquals("Expecting empty data set.", 0, difference.size());
    }

    @Test
    public void testIntIsIn() {
        int[] originalValues = new int[]{32, 42, 40, 57, 52, -2};
        int[] inValues = new int[]{10, -2, 57, -5};
        IntColumn inColumn = new IntColumn("In", new IntArrayList(inValues));

        IntColumn initial = new IntColumn("Test", originalValues.length);
        Table t = Table.create("t", initial);

        for (int value : originalValues) {
            initial.append(value);
        }

        Filter filter = column("Test").isIn(inColumn);
        Table result = t.selectWhere(filter);
        assertNotNull(result);
    }

    @Test
    public void testDivide() {
        int[] originalValues = new int[]{32, 42, 40, 57, 52, -2};
        IntColumn originals = new IntColumn("Originals", new IntArrayList(originalValues));
        FloatColumn divided = originals.divide(3);
        assertEquals(originals.size(), divided.size());
    }

    @Test
    public void testDivide2() {
        int[] originalValues = new int[]{32, 42, 40, 57, 52, -2};
        IntColumn originals = new IntColumn("Originals", new IntArrayList(originalValues));
        FloatColumn divided = originals.divide(3.3);
        assertEquals(originals.size(), divided.size());
    }
}