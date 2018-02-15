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
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import org.apache.commons.lang3.ArrayUtils;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.TypeUtils;
import tech.tablesaw.util.Selection;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.StatUtils;
import org.junit.Ignore;
import org.junit.Test;
import tech.tablesaw.util.Stats;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static tech.tablesaw.api.FloatColumn.MISSING_VALUE;

/**
 * Unit tests for the FloatColumn class
 */
public class FloatColumnTest {

    @Test
    public void testConvert() throws Exception {
        float result = FloatColumn.convert("42.23");
        assertEquals(42.23f, result, 0.01f);
    }

    @Test
    public void testRemoveCommasOnConvert() throws Exception {
        float result = FloatColumn.convert("23,32,45.5,2");
        assertEquals(233245.52f, result, 0.01f);
    }

    @Test
    public void testConvertNull() throws Exception {
        float result = FloatColumn.convert(null);
        assertEquals(ColumnType.FLOAT.getMissingValue(), result);
    }

    @Test
    public void testConvertEmptyString() throws Exception {
        float result = FloatColumn.convert("");
        assertEquals(ColumnType.FLOAT.getMissingValue(), result);
    }

    @Test
    public void testConvertMissingIndicator() throws Exception {
        for (String indicator : TypeUtils.MISSING_INDICATORS) {
            float result = FloatColumn.convert(indicator);
            assertEquals(ColumnType.FLOAT.getMissingValue(), result);
        }
    }

    @Test(expected = NumberFormatException.class)
    public void testConvertNotANumber() throws Exception {
        FloatColumn.convert("not a number");
    }

    @Test
    public void testSummary() throws Exception {
        FloatColumn column = new FloatColumn("c");
        Table table = column.summary();
        assertEquals(2, table.columnCount());
        assertEquals(8, table.rowCount());
    }

    @Test
    public void testStats() throws Exception {
        FloatColumn column = new FloatColumn("c");
        int rowsCount = 10_000;
        for (int i = 0; i < rowsCount; i++) {
            column.append((float) Math.random());
        }
        Stats stats = Stats.create(column);
        assertEquals(rowsCount, stats.n());
    }

    @Test
    public void testTop() throws Exception {
        float[] top = {Float.POSITIVE_INFINITY, 540.34f};
        float[] bottom = {Float.NEGATIVE_INFINITY, 0.0f, 42f};
        float[] floats = ArrayUtils.addAll(top, bottom);
        FloatColumn column = new FloatColumn("floats", floats);
        assertArrayEquals(top, column.top(top.length).toFloatArray(), 0.01f);
    }

    @Test
    public void testTopMoreThanColumnSize() throws Exception {
        float[] floats = {42f, 23f, 11f};
        int uniques = floats.length;
        FloatColumn column = new FloatColumn("floats", floats);
        assertEquals(uniques, column.top(uniques + 10).size());
    }

    @Test
    public void testTopEmptyColumn() throws Exception {
        FloatColumn column = new FloatColumn("empty");
        assertTrue(column.top(10).isEmpty());
    }

    @Test
    public void testTopColumnWithRepeatedValues() throws Exception {
        float[] top = {42f, 42f, 42f};
        float[] bottom = {23f, 23f, 11f};
        float[] floats = ArrayUtils.addAll(top, bottom);
        FloatColumn column = new FloatColumn("floats", floats);
        assertArrayEquals(top, column.top(top.length).toFloatArray(), 0.01f);
    }

    @Test
    public void testTopZero() throws Exception {
        FloatColumn column = new FloatColumn("c");
        int rowsCount = 100;
        for (int i = 0; i < rowsCount; i++) {
            column.append((float) Math.random());
        }
        assertTrue(column.top(0).isEmpty());
    }

    @Test
    public void testBottom() throws Exception {
        float[] top = {Float.POSITIVE_INFINITY, 540.34f};
        float[] bottom = {Float.NEGATIVE_INFINITY, 0.0f, 42f};
        float[] floats = ArrayUtils.addAll(top, bottom);
        FloatColumn column = new FloatColumn("floats", floats);
        assertArrayEquals(bottom, column.bottom(bottom.length).toFloatArray(), 0.01f);
    }

    @Test
    public void testBottomMoreThanColumnSize() throws Exception {
        float[] floats = {42f, 23f, 11f};
        int uniques = floats.length;
        FloatColumn column = new FloatColumn("floats", floats);
        assertEquals(uniques, column.bottom(uniques + 10).size());
    }

    @Test
    public void testBottomEmptyColumn() throws Exception {
        FloatColumn column = new FloatColumn("empty");
        assertTrue(column.bottom(10).isEmpty());
    }

    @Test
    public void testBottomColumnWithRepeatedValues() throws Exception {
        float[] top = {Float.POSITIVE_INFINITY, 42f, 42f};
        float[] bottom = {23f, 23f, 23f};
        float[] floats = ArrayUtils.addAll(top, bottom);
        FloatColumn column = new FloatColumn("floats", floats);
        assertArrayEquals(bottom, column.bottom(bottom.length).toFloatArray(), 0.01f);
    }

    @Test
    public void testBottomZero() throws Exception {
        FloatColumn column = new FloatColumn("c");
        int rowsCount = 100;
        for (int i = 0; i < rowsCount; i++) {
            column.append((float) Math.random());
        }
        assertTrue(column.bottom(0).isEmpty());
    }

    @Ignore
    @Test
    public void testApplyFilter() {

        Fairy fairy = Fairy.create();
        fairy.baseProducer().trueOrFalse();

        Table table = Table.create("t");
        FloatColumn floatColumn = new FloatColumn("test", 1_000_000_000);
        BooleanColumn booleanColumn = new BooleanColumn("bools", 1_000_000_000);
        table.addColumn(floatColumn);
        table.addColumn(booleanColumn);
        for (int i = 0; i < 1_000_000_000; i++) {
            floatColumn.append((float) Math.random());
            booleanColumn.append(fairy.baseProducer().trueOrFalse());
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        table.sortOn("test");
        System.out.println("Sort time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
        stopwatch.reset().start();
        System.out.println(floatColumn.summary());
        stopwatch.reset().start();
        floatColumn.isLessThan(.5f);
        System.out.println("Search time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Ignore
    @Test
    public void testSortAndApplyFilter1() {

        FloatColumn floatColumn = new FloatColumn("test", 1_000_000_000);
        for (int i = 0; i < 1_000_000_000; i++) {
            floatColumn.append((float) Math.random());
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        System.out.println(floatColumn.sum());
        System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));
        stopwatch.reset().start();
        floatColumn.sortAscending();
        System.out.println("Sort time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

        stopwatch.reset().start();
        floatColumn.isLessThan(.5f);
        System.out.println("Search time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Ignore
    @Test
    public void testSort1() throws Exception {
        FloatColumn floatColumn = new FloatColumn("test", 1_000_000_000);
        System.out.println("Adding floats to column");
        for (int i = 0; i < 1_000_000_000; i++) {
            floatColumn.append((float) Math.random());
        }
        System.out.println("Sorting");
        Stopwatch stopwatch = Stopwatch.createStarted();
        floatColumn.sortAscending();
        System.out.println("Sort time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

    }

    @Test
    public void testIsLessThan() {
        int size = 1_000_000;
        Table table = Table.create("t");
        FloatColumn floatColumn = new FloatColumn("test", size);
        table.addColumn(floatColumn);
        for (int i = 0; i < size; i++) {
            floatColumn.append((float) Math.random());
        }
        Selection results = floatColumn.isLessThan(.5f);
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
        FloatColumn floatColumn = new FloatColumn("test", size);
        table.addColumn(floatColumn);
        for (int i = 0; i < size; i++) {
            floatColumn.append((float) Math.random());
        }
        Selection results = floatColumn.isGreaterThan(.5f);

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
    public void testIsPositive() {
        int positiveRecords = 987_000;
        int negativeRecords = 654_000;
        FloatColumn floatColumn = new FloatColumn("test", positiveRecords+negativeRecords);
        for (int i = 0; i < positiveRecords; i++) {
            floatColumn.append((float) Math.random()*100);
        }

        for (int i = 0; i < negativeRecords; i++) {
            floatColumn.append((float) Math.random() * -100);
        }

        Selection results = floatColumn.isPositive();

        assertTrue(results.size() == positiveRecords);
    }
    
    @Test
    public void testIsNonNegative() {
        int positiveRecords = 980_000;
        int negativeRecords = 654_000;
        int zeroRecords = 20_000;
        FloatColumn floatColumn = new FloatColumn("test", positiveRecords+negativeRecords+zeroRecords);
        for (int i = 0; i < positiveRecords; i++) {
            floatColumn.append((float) Math.random()*100);
        }

        for (int i = 0; i < negativeRecords; i++) {
            floatColumn.append((float) Math.random() * -100);
        }

        for (int i = 0; i < zeroRecords; i++) {
            floatColumn.append(0);
        }

        Selection results = floatColumn.isNonNegative();

        assertTrue(results.size() == positiveRecords+zeroRecords);
    }

    @Test
    public void testSort() {
        int records = 1_000_000;
        FloatColumn floatColumn = new FloatColumn("test", records);
        for (int i = 0; i < records; i++) {
            floatColumn.append((float) Math.random());
        }
        floatColumn.sortAscending();
        float last = Float.NEGATIVE_INFINITY;
        for (float n : floatColumn) {
            assertTrue(n >= last);
            last = n;
        }
        floatColumn.sortDescending();
        last = Float.POSITIVE_INFINITY;
        for (float n : floatColumn) {
            assertTrue(n <= last);
            last = n;
        }
        records = 10;
        floatColumn = new FloatColumn("test", records);
        for (int i = 0; i < records; i++) {
            floatColumn.append((float) Math.random());
        }
        floatColumn.sortDescending();
        last = Float.POSITIVE_INFINITY;
        for (float n : floatColumn) {
            assertTrue(n <= last);
            last = n;
        }
    }

    @Test
    public void testIsEqualTo() {
        Table table = Table.create("t");
        FloatColumn floatColumn = new FloatColumn("test", 1_000_000);
        float[] floats = new float[1_000_000];
        table.addColumn(floatColumn);
        for (int i = 0; i < 1_000_000; i++) {
            float f = (float) Math.random();
            floatColumn.append(f);
            floats[i] = f;
        }
        Selection results;
        RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
        for (int i = 0; i < 100; i++) { // pick a hundred values at random and see if we can find them
            float f = floats[randomDataGenerator.nextInt(0, 999_999)];
            results = floatColumn.isEqualTo(f);
            assertEquals(f, floatColumn.get(results.iterator().nextInt()), .001);
        }
    }

    @Test
    public void testMaxAndMin() {
        FloatColumn floats = new FloatColumn("floats", 100);
        for (int i = 0; i < 100; i++) {
            floats.append(RandomUtils.nextFloat(0, 10_000));
        }
        FloatArrayList floats1 = floats.top(50);
        FloatArrayList floats2 = floats.bottom(50);
        double[] doubles1 = new double[50];
        double[] doubles2 = new double[50];
        for (int i = 0; i < floats1.size(); i++) {
            doubles1[i] = floats1.getFloat(i);
        }
        for (int i = 0; i < floats2.size(); i++) {
            doubles2[i] = floats2.getFloat(i);
        }
        // the smallest item in the max set is >= the largest in the min set
        assertTrue(StatUtils.min(doubles1) >= StatUtils.max(doubles2));
    }

    @Test
    public void testRound() {
        FloatColumn floats = new FloatColumn("floats", 100);
        for (int i = 0; i < 100; i++) {
            floats.append(RandomUtils.nextFloat(0, 10_000));
        }
        Column newFloats = floats.round();
        assertFalse(newFloats.isEmpty());
    }

    @Test
    public void testLogN() {
        FloatColumn floats = new FloatColumn("floats", 100);
        for (int i = 0; i < 100; i++) {
            floats.append(RandomUtils.nextFloat(0, 10_000));
        }
        Column newFloats = floats.logN();
        assertFalse(newFloats.isEmpty());
    }

    @Test
    public void testLog10() {
        FloatColumn floats = new FloatColumn("floats", 100);
        for (int i = 0; i < 100; i++) {
            floats.append(RandomUtils.nextFloat(0, 10_000));
        }
        Column newFloats = floats.log10();
        assertFalse(newFloats.isEmpty());
    }

    @Test
    public void testLog1p() {
        FloatColumn floats = new FloatColumn("floats", 100);
        for (int i = 0; i < 100; i++) {
            floats.append(RandomUtils.nextFloat(0, 10_000));
        }
        Column newFloats = floats.log1p();
        assertFalse(newFloats.isEmpty());
    }

    @Test
    public void testAbs() {
        FloatColumn floats = new FloatColumn("floats", 100);
        for (int i = 0; i < 100; i++) {
            floats.append(RandomUtils.nextFloat(0, 10_000));
        }
        Column newFloats = floats.abs();
        assertFalse(newFloats.isEmpty());
    }

    @Test
    public void testClear() {
        FloatColumn floats = new FloatColumn("floats", 100);
        for (int i = 0; i < 100; i++) {
            floats.append(RandomUtils.nextFloat(0, 10_000));
        }
        assertFalse(floats.isEmpty());
        floats.clear();
        assertTrue(floats.isEmpty());
    }

    @Test
    public void testCountMissing() {
        FloatColumn floats = new FloatColumn("floats", 10);
        for (int i = 0; i < 10; i++) {
            floats.append(RandomUtils.nextFloat(0, 1_000));
        }
        assertEquals(0, floats.countMissing());
        floats.clear();
        for (int i = 0; i < 10; i++) {
            floats.append(MISSING_VALUE);
        }
        assertEquals(10, floats.countMissing());
    }


    @Test
    public void testCountUnique() {
        FloatColumn floats = new FloatColumn("floats", 10);
        float[] uniques = {0.0f, 0.00000001f, -0.000001f, 92923.29340f, 24252, 23442f, 2252, 2342f};
        for (float unique : uniques) {
            floats.append(unique);
        }
        assertEquals(uniques.length, floats.countUnique());

        floats.clear();
        float[] notUniques = {0.0f, 0.00000001f, -0.000001f, 92923.29340f, 24252, 23442f, 2252, 2342f, 0f};

        for (float notUnique : notUniques) {
            floats.append(notUnique);
        }
        assertEquals(notUniques.length - 1, floats.countUnique());
    }

    @Test
    public void testUnique() {
        FloatColumn floats = new FloatColumn("floats", 10);
        float[] uniques = {0.0f, 0.00000001f, -0.000001f, 92923.29340f, 24252, 23442f, 2252, 2342f};
        for (float unique : uniques) {
            floats.append(unique);
        }
        assertEquals(uniques.length, floats.unique().size());

        floats.clear();
        float[] notUniques = {0.0f, 0.00000001f, -0.000001f, 92923.29340f, 24252, 23442f, 2252, 2342f, 0f};

        for (float notUnique : notUniques) {
            floats.append(notUnique);
        }
        assertEquals(notUniques.length - 1, floats.unique().size());
    }

    @Test
    public void testIsMissingAndIsNotMissing() {
        FloatColumn floats = new FloatColumn("floats", 10);
        for (int i = 0; i < 10; i++) {
            floats.append(RandomUtils.nextFloat(0, 1_000));
        }
        assertEquals(0, floats.isMissing().size());
        assertEquals(10, floats.isNotMissing().size());
        floats.clear();
        for (int i = 0; i < 10; i++) {
            floats.append(MISSING_VALUE);
        }
        assertEquals(10, floats.isMissing().size());
        assertEquals(0, floats.isNotMissing().size());
    }

    @Test
    public void testEmptyCopy() {
        FloatColumn floats = new FloatColumn("floats", 100);
        String comment = "This is a comment";
        floats.setComment(comment);
        for (int i = 0; i < 100; i++) {
            floats.append(RandomUtils.nextFloat(0, 10_000));
        }
        FloatColumn empty = floats.emptyCopy();
        assertTrue(empty.isEmpty());
        assertEquals(floats.name(), empty.name());

        //TODO(lwhite): Decide what gets copied in an empty copy
        //assertEquals(floats.comment(), empty.comment());
    }

    @Test
    public void testSize() {
        int size = 100;
        FloatColumn floats = new FloatColumn("floats", size);
        assertEquals(0, floats.size());
        for (int i = 0; i < size; i++) {
            floats.append(RandomUtils.nextFloat(0, 10_000));
        }
        assertEquals(size, floats.size());
        floats.clear();
        assertEquals(0, floats.size());
    }

    @Test
    public void testNeg() {
        FloatColumn floats = new FloatColumn("floats", 100);
        for (int i = 0; i < 100; i++) {
            floats.append(RandomUtils.nextFloat(0, 10_000));
        }
        Column newFloats = floats.neg();
        assertFalse(newFloats.isEmpty());
    }

    @Test
    public void tesMod() {
        FloatColumn floats = new FloatColumn("floats", 100);
        FloatColumn otherFloats = new FloatColumn("otherFloats", 100);
        for (int i = 0; i < 100; i++) {
            floats.append(RandomUtils.nextFloat(0, 10_000));
            otherFloats.append(floats.get(i) - 1.0f);
        }
        Column newFloats = floats.remainder(otherFloats);
        assertFalse(newFloats.isEmpty());
    }

    @Test
    public void testSquareAndSqrt() {
        FloatColumn floats = new FloatColumn("floats", 100);
        for (int i = 0; i < 100; i++) {
            floats.append(RandomUtils.nextFloat(0, 10_000));
        }

        FloatColumn newFloats = floats.square();
        FloatColumn revert = newFloats.sqrt();
        for (int i = 0; i < floats.size(); i++) {
            assertEquals(floats.get(i), revert.get(i), 0.01);
        }
    }

    @Test
    public void testType() {
        FloatColumn floats = new FloatColumn("floats", 100);
        assertEquals(ColumnType.FLOAT, floats.type());
    }

    @Test
    public void testCubeAndCbrt() {
        FloatColumn floats = new FloatColumn("floats", 100);
        for (int i = 0; i < 100; i++) {
            floats.append(RandomUtils.nextFloat(0, 10_000));
        }
        FloatColumn newFloats = floats.cube();
        FloatColumn revert = newFloats.cubeRoot();
        for (int i = 0; i < floats.size(); i++) {
            assertEquals(floats.get(i), revert.get(i), 0.01);
        }
    }

    @Test
    public void testDifference() {
        float[] originalValues = new float[]{32, 42, 40, 57, 52};
        float[] expectedValues = new float[]{MISSING_VALUE, 10, -2, 17, -5};
        assertTrue(computeAndValidateDifference(originalValues, expectedValues));
    }

    @Test
    public void testMissingValuesInColumn() {
        float[] originalValues = new float[]{32, 42, MISSING_VALUE, 57, 52};
        float[] expectedValues = new float[]{MISSING_VALUE, 10, MISSING_VALUE, MISSING_VALUE, -5};
        assertTrue(computeAndValidateDifference(originalValues, expectedValues));
    }

    private boolean computeAndValidateDifference(float[] originalValues, float[] expectedValues) {
        FloatColumn initial = new FloatColumn("Test", originalValues);
        FloatColumn difference = initial.difference();

        assertEquals("Both sets of data should be the same size.", expectedValues.length, difference.size());
        for (int index = 0; index < difference.size(); index++) {
            float actual = difference.get(index);
            assertEquals("difference operation at index:" + index + " failed", expectedValues[index], actual, 0);
        }

        return true;
    }

    @Test
    public void testDifferenceEmptyColumn() {
        FloatColumn initial = new FloatColumn("Test");
        FloatColumn difference = initial.difference();
        assertEquals("Expecting empty data set.", 0, difference.size());
    }

    @Test
    public void testGetDouble() {
        FloatColumn column = new FloatColumn("Test", new float[]{20.2f, 3245234.3f, MISSING_VALUE, 234});
        assertEquals("Primitive type conversion error", 20.2, column.getDouble(0), 0.1);
        assertEquals("Primitive type conversion error", 3245234.3, column.getDouble(1), 1);
        assertTrue("Primitive type conversion error", Double.isNaN(column.getDouble(2)));
        assertEquals("Primitive type conversion error", 234.0, column.getDouble(3), 0.1);
    }

    @Test
    public void testCumSum() {
        float[] originalValues = new float[]{32, 42, MISSING_VALUE, 57, 52, -10, 0};
        float[] expectedValues = new float[]{32, 74, MISSING_VALUE, 131, 183, 173, 173};
        FloatColumn initial = new FloatColumn("Test", originalValues);
        FloatColumn csum = initial.cumSum();
        
        assertEquals("Both sets of data should be the same size.", expectedValues.length, csum.size());
        
        for (int index = 0; index < csum.size(); index++) {
            float actual = csum.get(index);
            assertEquals("cumSum() operation at index:" + index + " failed", expectedValues[index], actual, 0);
        }
    }

    @Test
    public void testPctChange() {
        float[] originalValues = new float[]{ 10, 12, 13 };
        float[] expectedValues = new float[]{ MISSING_VALUE, 0.2f, 0.083333f };
        FloatColumn initial = new FloatColumn("Test", originalValues);
        FloatColumn pctChange = initial.pctChange();

        assertEquals("Both sets of data should be the same size.", expectedValues.length, pctChange.size());

        for (int index = 0; index < pctChange.size(); index++) {
            float actual = pctChange.get(index);
            assertEquals("pctChange() operation at index:" + index + " failed", expectedValues[index], actual, 0.0001);
        }
    }

}