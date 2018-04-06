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
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.junit.Ignore;
import org.junit.Test;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.selection.Selection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Double.NaN;
import static org.junit.Assert.*;

/**
 * Unit tests for the NumberColumn class
 */
public class NumberColumnTest {

    @Ignore
    @Test
    public void testApplyFilter() {

        Fairy fairy = Fairy.create();
        fairy.baseProducer().trueOrFalse();

        Table table = Table.create("t");
        NumberColumn numberColumn = NumberColumn.create("test", 100_000_000);
        //BooleanColumn booleanColumn = BooleanColumn.create("bools", 1_000_000_000);
        table.addColumn(numberColumn);
        //table.addColumn(booleanColumn);
        for (int i = 0; i < 100_000_000; i++) {
            numberColumn.append(Math.random());
          //  booleanColumn.append(fairy.baseProducer().trueOrFalse());
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        table.sortOn("test");
        System.out.println("Sort time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
        stopwatch.reset().start();
        System.out.println(numberColumn.summary());
        stopwatch.reset().start();
        numberColumn.isLessThan(.5f);
        System.out.println("Search time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Ignore
    @Test
    public void testSortAndApplyFilter1() {

        NumberColumn numberColumn = NumberColumn.create("test", 1_000_000_000);
        for (int i = 0; i < 1_000_000_000; i++) {
            numberColumn.append(Math.random());
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        System.out.println(numberColumn.sum());
        System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));
        stopwatch.reset().start();
        numberColumn.sortAscending();
        System.out.println("Sort time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

        stopwatch.reset().start();
        numberColumn.isLessThan(.5f);
        System.out.println("Search time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void createFromNumbers() {
        List<Number> numberList = new ArrayList<>();
        numberList.add(4);
        NumberColumn column = NumberColumn.create("test", numberList);
        assertEquals(4.0, column.get(0), 0.001);

        NumberColumn column1 = NumberColumn.create("T", numberList.toArray(new Number[numberList.size()]));
        assertEquals(4.0, column1.get(0), 0.001);

        float[] floats = new float[1];
        floats[0] = 4.0f;
        NumberColumn column2 = NumberColumn.create("T", floats);
        assertEquals(4.0, column2.get(0), 0.001);

        int[] ints = new int[1];
        ints[0] = 4;
        NumberColumn column3 = NumberColumn.create("T", ints);
        assertEquals(4.0, column3.get(0), 0.001);

        long[] longs = new long[1];
        longs[0] = 4_000_000_000L;
        NumberColumn column4 = NumberColumn.create("T", longs);
        assertEquals(4_000_000_000.0, column4.get(0), 0.001);
    }

    @Test
    public void testDoubleIsIn() {
        int[] originalValues = new int[]{32, 42, 40, 57, 52, -2};
        double[] inValues = new double[]{10, -2, 57, -5};

        NumberColumn initial = NumberColumn.create("Test", originalValues.length);
        Table t = Table.create("t", initial);

        for (int value : originalValues) {
            initial.append(value);
        }

        Filter filter = QueryHelper.numberColumn("Test").isIn(inValues);
        Table result = t.selectWhere(filter);
        assertNotNull(result);
    }

    @Test
    public void testCorrelation() {
        double[] x = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] y = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        NumberColumn xCol = NumberColumn.create("x", x);
        NumberColumn yCol = NumberColumn.create("y", y);

        double resultP = xCol.pearsons(yCol);
        double resultS = xCol.spearmans(yCol);
        double resultK = xCol.kendalls(yCol);
        assertEquals(new PearsonsCorrelation().correlation(x, y), resultP, 0.0001);
        assertEquals(new SpearmansCorrelation().correlation(x, y), resultS, 0.0001);
        assertEquals(new KendallsCorrelation().correlation(x, y), resultK, 0.0001);
    }

    @Test
    public void testCorrelation2() {
        double[] x = new double[]{1, 2, 3, 4, 5, 6, 7, NaN, 9, 10};
        double[] y = new double[]{1, 2, 3, NaN, 5, 6, 7, 8, 9, 10};

        NumberColumn xCol = NumberColumn.create("x", x);
        NumberColumn yCol = NumberColumn.create("y", y);

        double resultP = xCol.pearsons(yCol);
        double resultK = xCol.kendalls(yCol);
        assertEquals(new PearsonsCorrelation().correlation(x, y), resultP, 0.0001);
        assertEquals(new KendallsCorrelation().correlation(x, y), resultK, 0.0001);
    }

    @Test
    public void testBetweenExclusive() {
        int[] originalValues = new int[]{32, 42, 40, 57, 52, -2};

        NumberColumn initial = NumberColumn.create("Test", originalValues);
        Table t = Table.create("t", initial);

        Filter filter = QueryHelper.numberColumn("Test").isBetweenExclusive(42, 57);
        Table result = t.selectWhere(filter);
        assertEquals(1, result.rowCount());
        assertEquals("52.0", result.get(0, "Test"));
    }

    @Ignore
    @Test
    public void testSort1() {
        NumberColumn numberColumn = NumberColumn.create("test", 1_000_000_000);
        System.out.println("Adding doubles to column");
        for (int i = 0; i < 100_000_000; i++) {
            numberColumn.append(Math.random());
        }
        System.out.println("Sorting");
        Stopwatch stopwatch = Stopwatch.createStarted();
        numberColumn.sortAscending();
        System.out.println("Sort time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Test
    public void testIsLessThan() {
        int size = 1_000_000;
        Table table = Table.create("t");
        NumberColumn numberColumn = NumberColumn.create("test", size);
        table.addColumn(numberColumn);
        for (int i = 0; i < size; i++) {
            numberColumn.append(Math.random());
        }
        Selection results = numberColumn.isLessThan(.5f);
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
    public void testNumberFormat1() {
        NumberColumn numberColumn = NumberColumn.create("test");
        numberColumn.append(48392.2932);
        numberColumn.setPrintFormatter(NumberColumnFormatter.currency("en", "US"));
        assertEquals("$48,392.29", numberColumn.getString(0));
    }


    @Test
    public void testNumberFormat2() {
        NumberColumn numberColumn = NumberColumn.create("test");
        numberColumn.append(48392.2932);
        numberColumn.setPrintFormatter(NumberColumnFormatter.intsWithGrouping());
        assertEquals("48,392", numberColumn.getString(0));
    }

    @Test
    public void testNumberFormat3() {
        NumberColumn numberColumn = NumberColumn.create("test");
        numberColumn.append(48392.2932);
        numberColumn.setPrintFormatter(NumberColumnFormatter.ints());
        assertEquals("48392", numberColumn.getString(0));
    }


    @Test
    public void testNumberFormat4() {
        NumberColumn numberColumn = NumberColumn.create("test");
        numberColumn.append(48392.2932);
        numberColumn.setPrintFormatter(NumberColumnFormatter.fixedWithGrouping(3));
        assertEquals("48,392.293", numberColumn.getString(0));
    }

    @Test
    public void testNumberFormat5() {
        NumberColumn numberColumn = NumberColumn.create("test");
        numberColumn.append(0.2932);
        numberColumn.setPrintFormatter(NumberColumnFormatter.percent(1));
        assertEquals("29.3%", numberColumn.getString(0));
    }

    @Test
    public void testIndexColumn() {
        NumberColumn numberColumn = NumberColumn.indexColumn("index", 12424, 0);
        assertEquals("12423", numberColumn.getString(numberColumn.size() - 1));
    }

    @Test
    public void testIsGreaterThan() {
        int size = 1_000_000;
        Table table = Table.create("t");
        NumberColumn numberColumn = NumberColumn.create("test", size);
        table.addColumn(numberColumn);
        for (int i = 0; i < size; i++) {
            numberColumn.append(Math.random());
        }
        Selection results = numberColumn.isGreaterThan(.5f);

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
        NumberColumn numberColumn = NumberColumn.create("test", records);
        for (int i = 0; i < records; i++) {
            numberColumn.append(Math.random());
        }
        numberColumn.sortAscending();
        double last = Double.NEGATIVE_INFINITY;
        for (double n : numberColumn) {
            assertTrue(n >= last);
            last = n;
        }
        numberColumn.sortDescending();
        last = Double.POSITIVE_INFINITY;
        for (double n : numberColumn) {
            assertTrue(n <= last);
            last = n;
        }
        records = 10;
        numberColumn = NumberColumn.create("test", records);
        for (int i = 0; i < records; i++) {
            numberColumn.append(Math.random());
        }
        numberColumn.sortDescending();
        last = Double.POSITIVE_INFINITY;
        for (double n : numberColumn) {
            assertTrue(n <= last);
            last = n;
        }
    }

    @Test
    public void testMaxAndMin() {
        NumberColumn doubles = NumberColumn.create("doubles", 100);
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
    public void testClear() {
        NumberColumn doubles = NumberColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        assertFalse(doubles.isEmpty());
        doubles.clear();
        assertTrue(doubles.isEmpty());
    }

    @Test
    public void testCountMissing() {
        NumberColumn doubles = NumberColumn.create("doubles", 10);
        for (int i = 0; i < 10; i++) {
            doubles.append(RandomUtils.nextDouble(0, 1_000));
        }
        assertEquals(0, doubles.countMissing());
        doubles.clear();
        for (int i = 0; i < 10; i++) {
            doubles.append(NumberColumn.MISSING_VALUE);
        }
        assertEquals(10, doubles.countMissing());
    }


    @Test
    public void testCountUnique() {
        NumberColumn doubles = NumberColumn.create("doubles", 10);
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
        NumberColumn doubles = NumberColumn.create("doubles", 10);
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
        NumberColumn doubles = NumberColumn.create("doubles", 10);
        for (int i = 0; i < 10; i++) {
            doubles.append(RandomUtils.nextDouble(0, 1_000));
        }
        assertEquals(0, doubles.isMissing().size());
        assertEquals(10, doubles.isNotMissing().size());
        doubles.clear();
        for (int i = 0; i < 10; i++) {
            doubles.append(NumberColumn.MISSING_VALUE);
        }
        assertEquals(10, doubles.isMissing().size());
        assertEquals(0, doubles.isNotMissing().size());
    }

    @Test
    public void testEmptyCopy() {
        NumberColumn doubles = NumberColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        NumberColumn empty = doubles.emptyCopy();
        assertTrue(empty.isEmpty());
        assertEquals(doubles.name(), empty.name());
    }

    @Test
    public void testSize() {
        int size = 100;
        NumberColumn doubles = NumberColumn.create("doubles", size);
        assertEquals(0, doubles.size());
        for (int i = 0; i < size; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        assertEquals(size, doubles.size());
        doubles.clear();
        assertEquals(0, doubles.size());
    }

    @Test
    public void testType() {
        NumberColumn doubles = NumberColumn.create("doubles", 100);
        assertEquals(ColumnType.NUMBER, doubles.type());
    }

    @Test
    public void testDifference() {
        double[] originalValues = new double[]{32, 42, 40, 57, 52};
        double[] expectedValues = new double[]{NumberColumn.MISSING_VALUE, 10, -2, 17, -5};
        assertTrue(computeAndValidateDifference(originalValues, expectedValues));
    }

    @Test
    public void testDifferenceMissingValuesInColumn() {
        double[] originalValues = new double[]{32, 42, NumberColumn.MISSING_VALUE, 57, 52};
        double[] expectedValues = new double[]{NumberColumn.MISSING_VALUE, 10, NumberColumn.MISSING_VALUE, NumberColumn.MISSING_VALUE, -5};
        assertTrue(computeAndValidateDifference(originalValues, expectedValues));
    }

    private boolean computeAndValidateDifference(double[] originalValues, double[] expectedValues) {
        NumberColumn initial = NumberColumn.create("Test", originalValues);
        NumberColumn difference = initial.difference();
        return validateEquality(expectedValues, difference);
    }

    @Test
    public void testDifferenceEmptyColumn() {
        NumberColumn initial = NumberColumn.create("Test");
        NumberColumn difference = initial.difference();
        assertEquals("Expecting empty data set.", 0, difference.size());
    }

    @Test
    public void testCumSum() {
        double[] originalValues = new double[]{32, 42, NumberColumn.MISSING_VALUE, 57, 52, -10, 0};
        double[] expectedValues = new double[]{32, 74, NumberColumn.MISSING_VALUE, 131, 183, 173, 173};
        NumberColumn initial = NumberColumn.create("Test", originalValues);
        NumberColumn csum = initial.cumSum();

        assertEquals("Both sets of data should be the same size.", expectedValues.length, csum.size());

        for (int index = 0; index < csum.size(); index++) {
            double actual = csum.get(index);
            assertEquals("cumSum() operation at index:" + index + " failed", expectedValues[index], actual, 0);
        }
    }

    @Test
    public void testCumProd() {
        double[] originalValues = new double[]{1, 2, NumberColumn.MISSING_VALUE, 3, 4};
        double[] expectedValues = new double[]{1, 2, NumberColumn.MISSING_VALUE, 6, 24};
        NumberColumn initial = NumberColumn.create("Test", originalValues);
        NumberColumn cprod = initial.cumProd();

        assertEquals("Both sets of data should be the same size.", expectedValues.length, cprod.size());

        for (int index = 0; index < cprod.size(); index++) {
            double actual = cprod.get(index);
            assertEquals("cumProd() operation at index:" + index + " failed", expectedValues[index], actual, 0);
        }
    }

    @Test
    public void testSubtract2Columns() {
        double[] col1Values = new double[]{32.5, NumberColumn.MISSING_VALUE, 42, 57, 52};
        double[] col2Values = new double[]{32, 42, 38.67, NumberColumn.MISSING_VALUE, 52.01};
        double[] expected = new double[]{0.5, NumberColumn.MISSING_VALUE, 3.33, NumberColumn.MISSING_VALUE, -.01};

        NumberColumn col1 = NumberColumn.create("1", col1Values);
        NumberColumn col2 = NumberColumn.create("2", col2Values);

        NumberColumn difference = col1.subtract(col2);
        assertTrue(validateEquality(expected, difference));

        // change order to verify size of returned column
        difference = col2.subtract(col1);
        expected = new double[]{-0.5, NumberColumn.MISSING_VALUE, -3.33, NumberColumn.MISSING_VALUE, .01};
        assertTrue(validateEquality(expected, difference));
    }

    @Test
    public void testPctChange() {
        double[] originalValues = new double[]{10, 12, 13};
        double[] expectedValues = new double[]{NumberColumn.MISSING_VALUE, 0.2, 0.083333};
        NumberColumn initial = NumberColumn.create("Test", originalValues);
        NumberColumn pctChange = initial.pctChange();

        assertEquals("Both sets of data should be the same size.", expectedValues.length, pctChange.size());

        for (int index = 0; index < pctChange.size(); index++) {
            double actual = pctChange.get(index);
            assertEquals("pctChange() operation at index:" + index + " failed", expectedValues[index], actual, 0.0001);
        }
    }

    private boolean validateEquality(double[] expectedValues, NumberColumn col) {
        assertEquals("Both sets of data should be the same size.", expectedValues.length, col.size());
        for (int index = 0; index < col.size(); index++) {
            double actual = col.get(index);
            assertEquals("value mismatch at index:" + index, expectedValues[index], actual, 0.01);
        }
        return true;
    }
}

