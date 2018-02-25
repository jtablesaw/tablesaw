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

import com.google.common.base.Stopwatch;
import io.codearte.jfairy.Fairy;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import tech.tablesaw.columns.Column;
import tech.tablesaw.util.Selection;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.StatUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static tech.tablesaw.api.DoubleColumn.MISSING_VALUE;

/**
 * Unit tests for the DoubleColumn class
 */
public class DoubleColumnTest {

    @Ignore
    @Test
    public void testApplyFilter() {

        Fairy fairy = Fairy.create();
        fairy.baseProducer().trueOrFalse();

        Table table = Table.create("t");
        DoubleColumn doubleColumn = new DoubleColumn("test", 1_000_000_000);
        BooleanColumn booleanColumn = new BooleanColumn("bools", 1_000_000_000);
        table.addColumn(doubleColumn);
        table.addColumn(booleanColumn);
        for (int i = 0; i < 1_000_000_000; i++) {
            doubleColumn.append(Math.random());
            booleanColumn.append(fairy.baseProducer().trueOrFalse());
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        table.sortOn("test");
        System.out.println("Sort time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
        stopwatch.reset().start();
        System.out.println(doubleColumn.summary());
        stopwatch.reset().start();
        doubleColumn.isLessThan(.5f);
        System.out.println("Search time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Ignore
    @Test
    public void testSortAndApplyFilter1() {

        DoubleColumn doubleColumn = new DoubleColumn("test", 1_000_000_000);
        for (int i = 0; i < 1_000_000_000; i++) {
            doubleColumn.append(Math.random());
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        System.out.println(doubleColumn.sum());
        System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));
        stopwatch.reset().start();
        doubleColumn.sortAscending();
        System.out.println("Sort time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

        stopwatch.reset().start();
        doubleColumn.isLessThan(.5f);
        System.out.println("Search time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Ignore
    @Test
    public void testSort1() throws Exception {
        DoubleColumn doubleColumn = new DoubleColumn("test", 1_000_000_000);
        System.out.println("Adding doubles to column");
        for (int i = 0; i < 1_000_000_000; i++) {
            doubleColumn.append(Math.random());
        }
        System.out.println("Sorting");
        Stopwatch stopwatch = Stopwatch.createStarted();
        doubleColumn.sortAscending();
        System.out.println("Sort time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

    }

    @Test
    public void testIsLessThan() {
        int size = 1_000_000;
        Table table = Table.create("t");
        DoubleColumn doubleColumn = new DoubleColumn("test", size);
        table.addColumn(doubleColumn);
        for (int i = 0; i < size; i++) {
            doubleColumn.append(Math.random());
        }
        Selection results = doubleColumn.isLessThan(.5f);
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (results.contains(i)) {
                count++;
            }
        }
        // Probabilistic answer.
        assertTrue(count < 575_000);
        assertTrue(count > 425_000);
    }

    @Test
    public void testIsGreaterThan() {
        int size = 1_000_000;
        Table table = Table.create("t");
        DoubleColumn doubleColumn = new DoubleColumn("test", size);
        table.addColumn(doubleColumn);
        for (int i = 0; i < size; i++) {
            doubleColumn.append(Math.random());
        }
        Selection results = doubleColumn.isGreaterThan(.5f);

        int count = 0;
        for (int i = 0; i < size; i++) {
            if (results.contains(i)) {
                count++;
            }
        }
        // Probabilistic answer.
        assertTrue(count < 575_000);
        assertTrue(count > 425_000);
    }

    @Test
    public void testSort() {
        int records = 1_000_000;
        DoubleColumn doubleColumn = new DoubleColumn("test", records);
        for (int i = 0; i < records; i++) {
            doubleColumn.append(Math.random());
        }
        doubleColumn.sortAscending();
        double last = Double.NEGATIVE_INFINITY;
        for (double n : doubleColumn) {
            assertTrue(n >= last);
            last = n;
        }
        doubleColumn.sortDescending();
        last = Double.POSITIVE_INFINITY;
        for (double n : doubleColumn) {
            assertTrue(n <= last);
            last = n;
        }
        records = 10;
        doubleColumn = new DoubleColumn("test", records);
        for (int i = 0; i < records; i++) {
            doubleColumn.append(Math.random());
        }
        doubleColumn.sortDescending();
        last = Double.POSITIVE_INFINITY;
        for (double n : doubleColumn) {
            assertTrue(n <= last);
            last = n;
        }
    }

    @Test
    public void testIsEqualTo() {
        Table table = Table.create("t");
        DoubleColumn doubleColumn = new DoubleColumn("test", 1_000_000);
        double[] doubles = new double[1_000_000];
        table.addColumn(doubleColumn);
        for (int i = 0; i < 1_000_000; i++) {
            double d = Math.random();
            doubleColumn.append(d);
            doubles[i] = d;
        }
        Selection results;
        RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
        for (int i = 0; i < 100; i++) { // pick a hundred values at random and see if we can find them
            double aDouble = doubles[randomDataGenerator.nextInt(0, 999_999)];
            results = doubleColumn.isEqualTo(aDouble);
            assertEquals(aDouble, doubleColumn.get(results.iterator().nextInt()), .001);
        }
    }

    @Test
    public void testMaxAndMin() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        DoubleArrayList doubles1 = doubles.top(50);
        DoubleArrayList doubles2 = doubles.bottom(50);
        double[] doublesA = new double[50];
        double[] doublesB = new double[50];
        for (int i = 0; i < doubles1.size(); i++) {
            doublesA[i] = doubles1.getDouble(i);
        }
        for (int i = 0; i < doubles2.size(); i++) {
            doublesB[i] = doubles2.getDouble(i);
        }
        // the smallest item in the max set is >= the largest in the min set
        assertTrue(StatUtils.min(doublesA) >= StatUtils.max(doublesB));
    }

    @Test
    public void testRound() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        Column newDoubles = doubles.round();
        assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testLogN() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        Column newDoubles = doubles.logN();
        assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testLog10() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        Column newDoubles = doubles.log10();
        assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testLog1p() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        Column newDoubles = doubles.log1p();
        assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testAbs() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        Column newDoubles = doubles.abs();
        assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testClear() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        assertFalse(doubles.isEmpty());
        doubles.clear();
        assertTrue(doubles.isEmpty());
    }

    @Test
    public void testCountMissing() {
        DoubleColumn doubles = new DoubleColumn("doubles", 10);
        for (int i = 0; i < 10; i++) {
            doubles.append(RandomUtils.nextDouble(0, 1_000));
        }
        assertEquals(0, doubles.countMissing());
        doubles.clear();
        for (int i = 0; i < 10; i++) {
            doubles.append(MISSING_VALUE);
        }
        assertEquals(10, doubles.countMissing());
    }


    @Test
    public void testCountUnique() {
        DoubleColumn doubles = new DoubleColumn("doubles", 10);
        double[] uniques = {0.0f, 0.00000001f, -0.000001f, 92923.29340f, 24252, 23442f, 2252, 2342f};
        for (double unique : uniques) {
            doubles.append(unique);
        }
        assertEquals(uniques.length, doubles.countUnique());

        doubles.clear();
        double[] notUniques = {0.0f, 0.00000001f, -0.000001f, 92923.29340f, 24252, 23442f, 2252, 2342f, 0f};

        for (double notUnique : notUniques) {
            doubles.append(notUnique);
        }
        assertEquals(notUniques.length - 1, doubles.countUnique());
    }

    @Test
    public void testUnique() {
        DoubleColumn doubles = new DoubleColumn("doubles", 10);
        double[] uniques = {0.0f, 0.00000001f, -0.000001f, 92923.29340f, 24252, 23442f, 2252, 2342f};
        for (double unique : uniques) {
            doubles.append(unique);
        }
        assertEquals(uniques.length, doubles.unique().size());

        doubles.clear();
        double[] notUniques = {0.0f, 0.00000001f, -0.000001f, 92923.29340f, 24252, 23442f, 2252, 2342f, 0f};

        for (double notUnique : notUniques) {
            doubles.append(notUnique);
        }
        assertEquals(notUniques.length - 1, doubles.unique().size());
    }

    @Test
    public void testIsMissingAndIsNotMissing() {
        DoubleColumn doubles = new DoubleColumn("doubles", 10);
        for (int i = 0; i < 10; i++) {
            doubles.append(RandomUtils.nextDouble(0, 1_000));
        }
        assertEquals(0, doubles.isMissing().size());
        assertEquals(10, doubles.isNotMissing().size());
        doubles.clear();
        for (int i = 0; i < 10; i++) {
            doubles.append(MISSING_VALUE);
        }
        assertEquals(10, doubles.isMissing().size());
        assertEquals(0, doubles.isNotMissing().size());
    }

    @Test
    public void testEmptyCopy() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        String comment = "This is a comment";
        doubles.setComment(comment);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        DoubleColumn empty = doubles.emptyCopy();
        assertTrue(empty.isEmpty());
        assertEquals(doubles.name(), empty.name());

        //TODO(lwhite): Decide what gets copied in an empty copy
        //assertEquals(doubles.comment(), empty.comment());
    }

    @Test
    public void testSize() {
        int size = 100;
        DoubleColumn doubles = new DoubleColumn("doubles", size);
        assertEquals(0, doubles.size());
        for (int i = 0; i < size; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        assertEquals(size, doubles.size());
        doubles.clear();
        assertEquals(0, doubles.size());
    }

    @Test
    public void testNeg() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        Column newDoubles = doubles.neg();
        assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void tesMod() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        DoubleColumn otherDoubles = new DoubleColumn("otherDoubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
            otherDoubles.append(doubles.get(i) - 1.0f);
        }
        Column newDoubles = doubles.remainder(otherDoubles);
        assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testSquareAndSqrt() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }

        DoubleColumn newDoubles = doubles.square();
        DoubleColumn revert = newDoubles.sqrt();
        for (int i = 0; i < doubles.size(); i++) {
            assertEquals(doubles.get(i), revert.get(i), 0.01);
        }
    }

    @Test
    public void testType() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        assertEquals(ColumnType.DOUBLE, doubles.type());
    }

    @Test
    public void testCubeAndCbrt() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        DoubleColumn newDoubles = doubles.cube();
        DoubleColumn revert = newDoubles.cubeRoot();
        for (int i = 0; i < doubles.size(); i++) {
            assertEquals(doubles.get(i), revert.get(i), 0.01);
        }
    }

    @Test
    public void testDifference() {
        double[] originalValues = new double[]{32, 42, 40, 57, 52};
        double[] expectedValues = new double[]{MISSING_VALUE, 10, -2, 17, -5};
        assertTrue(computeAndValidateDifference(originalValues, expectedValues));
    }

    @Test
    public void testDifferenceMissingValuesInColumn() {
        double[] originalValues = new double[]{32, 42, MISSING_VALUE, 57, 52};
        double[] expectedValues = new double[]{MISSING_VALUE, 10, MISSING_VALUE, MISSING_VALUE, -5};
        assertTrue(computeAndValidateDifference(originalValues, expectedValues));
    }

    private boolean computeAndValidateDifference(double[] originalValues, double[] expectedValues) {
        DoubleColumn initial = new DoubleColumn("Test", originalValues);
        DoubleColumn difference = initial.difference();
        return validateEquality(expectedValues, difference);
    }

    @Test
    public void testDifferenceEmptyColumn() {
        DoubleColumn initial = new DoubleColumn("Test");
        DoubleColumn difference = initial.difference();
        assertEquals("Expecting empty data set.", 0, difference.size());
    }

    @Test
    public void testCumSum() {
        double[] originalValues = new double[]{32, 42, MISSING_VALUE, 57, 52, -10, 0};
        double[] expectedValues = new double[]{32, 74, MISSING_VALUE, 131, 183, 173, 173};
        DoubleColumn initial = new DoubleColumn("Test", originalValues);
        DoubleColumn csum = initial.cumSum();
        
        assertEquals("Both sets of data should be the same size.", expectedValues.length, csum.size());
        
        for (int index = 0; index < csum.size(); index++) {
            double actual = csum.get(index);
            assertEquals("cumSum() operation at index:" + index + " failed", expectedValues[index], actual, 0);
        }
    }

    @Test
    public void testCumProd() {
        double[] originalValues = new double[]{1, 2, MISSING_VALUE, 3, 4};
        double[] expectedValues = new double[]{1, 2, MISSING_VALUE, 6, 24};
        DoubleColumn initial = new DoubleColumn("Test", originalValues);
        DoubleColumn cprod = initial.cumProd();

        assertEquals("Both sets of data should be the same size.", expectedValues.length, cprod.size());

        for (int index = 0; index < cprod.size(); index++) {
            double actual = cprod.get(index);
            assertEquals("cumProd() operation at index:" + index + " failed", expectedValues[index], actual, 0);
        }
    }

    @Test
    public void testSubtractLongColumn() {
        long[] col1Values = new long[]{32, LongColumn.MISSING_VALUE, 42, 57, 52};
        double[] col2Values = new double[]{31.5, 42, 38.67, MISSING_VALUE, 52.01};
        double[] expected = new double[]{-0.5, MISSING_VALUE, -3.33, MISSING_VALUE, .01};

        LongColumn col1 = new LongColumn("1", col1Values.length);
        Arrays.stream(col1Values).forEach(col1::append);
        DoubleColumn col2 = new DoubleColumn("2", col2Values);

        NumericColumn difference = col2.subtract(col1);
        assertTrue("Expecting DoubleColumn type result", difference instanceof DoubleColumn);
        DoubleColumn diffDoubleCol = (DoubleColumn) difference;
        assertTrue(validateEquality(expected, diffDoubleCol));
    }

    @Test
    public void testSubtract2Columns() {
        double[] col1Values = new double[]{32.5, MISSING_VALUE, 42, 57, 52};
        double[] col2Values = new double[]{32, 42, 38.67, MISSING_VALUE, 52.01};
        double[] expected = new double[]{0.5, MISSING_VALUE, 3.33, MISSING_VALUE, -.01};

        DoubleColumn col1 = new DoubleColumn("1", col1Values);
        DoubleColumn col2 = new DoubleColumn("2", col2Values);

        DoubleColumn difference = (DoubleColumn) NumericColumn.subtractColumns(col1, col2);
        assertTrue(validateEquality(expected, difference));

        // change order to verify size of returned column
        difference = (DoubleColumn) NumericColumn.subtractColumns(col2, col1);
        expected = new double[]{-0.5, MISSING_VALUE, -3.33, MISSING_VALUE, .01};
        assertTrue(validateEquality(expected, difference));
    }

    @Test
    public void testPctChange() {
        double[] originalValues = new double[]{ 10, 12, 13 };
        double[] expectedValues = new double[]{ MISSING_VALUE, 0.2, 0.083333 };
        DoubleColumn initial = new DoubleColumn("Test", originalValues);
        DoubleColumn pctChange = initial.pctChange();

        assertEquals("Both sets of data should be the same size.", expectedValues.length, pctChange.size());

        for (int index = 0; index < pctChange.size(); index++) {
            double actual = pctChange.get(index);
            assertEquals("pctChange() operation at index:" + index + " failed", expectedValues[index], actual, 0.0001);
        }
    }

    private boolean validateEquality(double[] expectedValues, DoubleColumn col) {
        assertEquals("Both sets of data should be the same size.", expectedValues.length, col.size());
        for (int index = 0; index < col.size(); index++) {
            double actual = col.get(index);
            assertEquals("value mismatch at index:" + index, expectedValues[index], actual, 0.01);
        }
        return true;
    }
}

