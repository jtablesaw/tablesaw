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

import static java.lang.Double.NaN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.tablesaw.aggregate.AggregateFunctions.median;
import static tech.tablesaw.aggregate.AggregateFunctions.percentile;
import static tech.tablesaw.aggregate.AggregateFunctions.percentile90;
import static tech.tablesaw.aggregate.AggregateFunctions.percentile95;
import static tech.tablesaw.aggregate.AggregateFunctions.percentile99;
import static tech.tablesaw.aggregate.AggregateFunctions.quartile1;
import static tech.tablesaw.aggregate.AggregateFunctions.quartile3;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.devskiller.jfairy.Fairy;
import com.google.common.base.Stopwatch;

import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;
import tech.tablesaw.selection.Selection;

/**
 * Unit tests for the NumberColumn class
 */
public class NumberColumnTest {

    private static final double MISSING = DoubleColumnType.missingValueIndicator();
    private static final DoublePredicate isPositiveOrZeroD = d -> d >= 0, isNegativeD = d -> d < 0;
    private static final DoubleFunction<String> toStringD = d -> String.valueOf(d);
    private static final DoubleBinaryOperator sumD = (d1, d2) -> d1 + d2;

    @Disabled
    @Test
    public void testApplyFilter() {

        Fairy fairy = Fairy.create();
        fairy.baseProducer().trueOrFalse();

        Table table = Table.create("t");
        DoubleColumn numberColumn = DoubleColumn.create("test", 100_000_000);
        //BooleanColumn booleanColumn = BooleanColumn.create("bools", 1_000_000_000);
        table.addColumns(numberColumn);
        //table.addColumns(booleanColumn);
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

    @Test
    public void testPercentiles() {
	IntColumn c = IntColumn.indexColumn("t", 99, 1);
	IntColumn c2 = c.copy();
        c2.appendCell("");
        assertEquals(50, c.median(), 0.00001);
        assertEquals(50, c2.median(), 0.00001);
        assertEquals(50, median.summarize(c), 0.00001);

        assertEquals(25, c.quartile1(), 0.00001);
        assertEquals(25, c2.quartile1(), 0.00001);
        assertEquals(25, quartile1.summarize(c), 0.00001);

        assertEquals(75, c.quartile3(), 0.00001);
        assertEquals(75, c2.quartile3(), 0.00001);
        assertEquals(75, quartile3.summarize(c), 0.00001);

        assertEquals(90, percentile90.summarize(c), 0.00001);

        assertEquals(5, c2.percentile(5), 0.00001);
        assertEquals(5, c.percentile(5), 0.00001);
        assertEquals(5, percentile(c, 5.0), 0.00001);

        assertEquals(95, percentile95.summarize(c), 0.00001);
        assertEquals(99, percentile99.summarize(c), 0.00001);
    }

    @Test
    public void testSummarize() {
	IntColumn c = IntColumn.indexColumn("t", 99, 1);
	IntColumn c2 = c.copy();
        c2.appendCell("");
        double c2Variance = c2.variance();
        double cVariance = StatUtils.variance(c.asDoubleArray());
        assertEquals(cVariance, c2Variance, 0.00001);
        assertEquals(StatUtils.sumLog(c.asDoubleArray()), c2.sumOfLogs(), 0.00001);
        assertEquals(StatUtils.sumSq(c.asDoubleArray()), c2.sumOfSquares(), 0.00001);
        assertEquals(StatUtils.geometricMean(c.asDoubleArray()), c2.geometricMean(), 0.00001);
        assertEquals(StatUtils.product(c.asDoubleArray()), c2.product(), 0.00001);
        assertEquals(StatUtils.populationVariance(c.asDoubleArray()), c2.populationVariance(), 0.00001);
        assertEquals(new DescriptiveStatistics(c.asDoubleArray()).getQuadraticMean(), c2.quadraticMean(), 0.00001);
        assertEquals(new DescriptiveStatistics(c.asDoubleArray()).getStandardDeviation(), c2.standardDeviation(), 0.00001);
        assertEquals(new DescriptiveStatistics(c.asDoubleArray()).getKurtosis(), c2.kurtosis(), 0.00001);
        assertEquals(new DescriptiveStatistics(c.asDoubleArray()).getSkewness(), c2.skewness(), 0.00001);

        assertEquals(StatUtils.variance(c.asDoubleArray()), c.variance(), 0.00001);
        assertEquals(StatUtils.sumLog(c.asDoubleArray()), c.sumOfLogs(), 0.00001);
        assertEquals(StatUtils.sumSq(c.asDoubleArray()), c.sumOfSquares(), 0.00001);
        assertEquals(StatUtils.geometricMean(c.asDoubleArray()), c.geometricMean(), 0.00001);
        assertEquals(StatUtils.product(c.asDoubleArray()), c.product(), 0.00001);
        assertEquals(StatUtils.populationVariance(c.asDoubleArray()), c.populationVariance(), 0.00001);
        assertEquals(new DescriptiveStatistics(c.asDoubleArray()).getQuadraticMean(), c.quadraticMean(), 0.00001);
        assertEquals(new DescriptiveStatistics(c.asDoubleArray()).getStandardDeviation(), c.standardDeviation(), 0.00001);
        assertEquals(new DescriptiveStatistics(c.asDoubleArray()).getKurtosis(), c.kurtosis(), 0.00001);
        assertEquals(new DescriptiveStatistics(c.asDoubleArray()).getSkewness(), c.skewness(), 0.00001);
    }

    @Disabled
    @Test
    public void testSortAndApplyFilter1() {

        DoubleColumn numberColumn =  DoubleColumn.create("test", 1_000_000_000);
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
        DoubleColumn column =  DoubleColumn.create("test", numberList);
        assertEquals(4.0, column.get(0), 0.001);

        DoubleColumn column1 =  DoubleColumn.create("T", numberList.toArray(new Number[numberList.size()]));
        assertEquals(4.0, column1.get(0), 0.001);

        float[] floats = new float[1];
        floats[0] = 4.0f;
        DoubleColumn column2 =  DoubleColumn.create("T", floats);
        assertEquals(4.0, column2.get(0), 0.001);

        int[] ints = new int[1];
        ints[0] = 4;
        DoubleColumn column3 =  DoubleColumn.create("T", ints);
        assertEquals(4.0, column3.get(0), 0.001);

        long[] longs = new long[1];
        longs[0] = 4_000_000_000L;
        DoubleColumn column4 =  DoubleColumn.create("T", longs);
        assertEquals(4_000_000_000.0, column4.get(0), 0.001);
    }

    @Test
    public void testDoubleIsIn() {
        int[] originalValues = new int[]{32, 42, 40, 57, 52, -2};
        double[] inValues = new double[]{10, -2, 57, -5};

        DoubleColumn initial =  DoubleColumn.create("Test", originalValues.length);
        Table t = Table.create("t", initial);

        for (int value : originalValues) {
            initial.append(value);
        }

        Selection filter = t.numberColumn("Test").isIn(inValues);
        Table result = t.where(filter);
        assertNotNull(result);
    }

    @Test
    public void testCorrelation() {
        double[] x = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        double[] y = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        DoubleColumn xCol =  DoubleColumn.create("x", x);
        DoubleColumn yCol =  DoubleColumn.create("y", y);

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

        DoubleColumn xCol =  DoubleColumn.create("x", x);
        DoubleColumn yCol =  DoubleColumn.create("y", y);

        double resultP = xCol.pearsons(yCol);
        double resultK = xCol.kendalls(yCol);
        assertEquals(new PearsonsCorrelation().correlation(x, y), resultP, 0.0001);
        assertEquals(new KendallsCorrelation().correlation(x, y), resultK, 0.0001);
    }

    @Test
    public void testBetweenExclusive() {
        int[] originalValues = new int[]{32, 42, 40, 57, 52, -2};

        IntColumn initial =  IntColumn.create("Test", originalValues);
        Table t = Table.create("t", initial);

        Selection filter = t.numberColumn("Test").isBetweenExclusive(42, 57);
        Table result = t.where(filter);
        assertEquals(1, result.rowCount());
        assertEquals("52", result.getString(0, "Test"));
    }

    @Disabled
    @Test
    public void testSort1() {
        DoubleColumn numberColumn =  DoubleColumn.create("test", 1_000_000_000);
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
        DoubleColumn numberColumn =  DoubleColumn.create("test", size);
        table.addColumns(numberColumn);
        for (int i = 0; i < size; i++) {
            numberColumn.set(i, Math.random());
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
        DoubleColumn numberColumn =  DoubleColumn.create("test");
        numberColumn.append(48392.2932);
        numberColumn.setPrintFormatter(NumberColumnFormatter.currency("en", "US"));
        assertEquals("$48,392.29", numberColumn.getString(0));
    }


    @Test
    public void testNumberFormat2() {
        DoubleColumn numberColumn =  DoubleColumn.create("test");
        numberColumn.append(48392.2932);
        numberColumn.setPrintFormatter(NumberColumnFormatter.intsWithGrouping());
        assertEquals("48,392", numberColumn.getString(0));
    }

    @Test
    public void testAsString() {
        DoubleColumn numberColumn =  DoubleColumn.create("test");
        numberColumn.append(48392.2932);
        StringColumn sc = numberColumn.asStringColumn();
        assertEquals("test strings", sc.name());
        assertEquals("48392.2932", sc.get(0));
    }

    @Test
    public void testNumberFormat3() {
        DoubleColumn numberColumn =  DoubleColumn.create("test");
        numberColumn.append(48392.2932);
        numberColumn.setPrintFormatter(NumberColumnFormatter.ints());
        assertEquals("48392", numberColumn.getString(0));
    }


    @Test
    public void testNumberFormat4() {
        DoubleColumn numberColumn =  DoubleColumn.create("test");
        numberColumn.append(48392.2932);
        numberColumn.setPrintFormatter(NumberColumnFormatter.fixedWithGrouping(3));
        assertEquals("48,392.293", numberColumn.getString(0));
    }

    @Test
    public void testNumberFormat5() {
        DoubleColumn numberColumn =  DoubleColumn.create("test");
        numberColumn.append(0.2932);
        numberColumn.setPrintFormatter(NumberColumnFormatter.percent(1));
        assertEquals("29.3%", numberColumn.getString(0));
    }

    @Test
    public void testIndexColumn() {
	IntColumn numberColumn = IntColumn.indexColumn("index", 12424, 0);
        assertEquals("12423", numberColumn.getString(numberColumn.size() - 1));
    }

    @Test
    public void testIsGreaterThan() {
        int size = 1_000_000;
        Table table = Table.create("t");
        DoubleColumn numberColumn =  DoubleColumn.create("test", size);
        table.addColumns(numberColumn);
        for (int i = 0; i < size; i++) {
            numberColumn.set(i, Math.random());
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
        DoubleColumn numberColumn =  DoubleColumn.create("test", records);
        for (int i = 0; i < records; i++) {
            numberColumn.set(i, Math.random());
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
        numberColumn =  DoubleColumn.create("test", records);
        for (int i = 0; i < records; i++) {
            numberColumn.set(i, Math.random());
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
        DoubleColumn doubles =  DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.set(i, RandomUtils.nextDouble(0, 10_000));
        }
        NumericColumn<?> doubles1 = doubles.top(50);
        NumericColumn<?> doubles2 = doubles.bottom(50);
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
        DoubleColumn doubles =  DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.set(i, RandomUtils.nextDouble(0, 10_000));
        }
        assertFalse(doubles.isEmpty());
        doubles.clear();
        assertTrue(doubles.isEmpty());
    }

    @Test
    public void testCountMissing() {
        DoubleColumn doubles =  DoubleColumn.create("doubles");
        for (int i = 0; i < 10; i++) {
            doubles.append(RandomUtils.nextDouble(0, 1_000));
        }
        assertEquals(0, doubles.countMissing());
        doubles.clear();
        for (int i = 0; i < 10; i++) {
            doubles.append(MISSING);
        }
        assertEquals(10, doubles.countMissing());
    }


    @Test
    public void testCountUnique() {
        DoubleColumn doubles =  DoubleColumn.create("doubles", 10);
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
        DoubleColumn doubles =  DoubleColumn.create("doubles");
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
        DoubleColumn doubles =  DoubleColumn.create("doubles", 10);
        for (int i = 0; i < 10; i++) {
            doubles.set(i, RandomUtils.nextDouble(0, 1_000));
        }
        assertEquals(0, doubles.isMissing().size());
        assertEquals(10, doubles.isNotMissing().size());
        doubles.clear();
        for (int i = 0; i < 10; i++) {
            doubles.append(MISSING);
        }
        assertEquals(10, doubles.isMissing().size());
        assertEquals(0, doubles.isNotMissing().size());
    }

    @Test
    public void testEmptyCopy() {
        DoubleColumn doubles =  DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        DoubleColumn empty = doubles.emptyCopy();
        assertTrue(empty.isEmpty());
        assertEquals(doubles.name(), empty.name());
    }

    @Test
    public void appendObject() {
        DoubleColumn doubles =  DoubleColumn.create("doubles");
        doubles.appendObj(BigDecimal.valueOf(1));
        assertEquals(1.0, doubles.get(0), 0.00001);
    }

    @Test
    public void testSize() {
        DoubleColumn doubles =  DoubleColumn.create("doubles");
        assertEquals(0, doubles.size());
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        assertEquals(100, doubles.size());
        doubles.clear();
        assertEquals(0, doubles.size());
    }

    @Test
    public void testType() {
        DoubleColumn doubles =  DoubleColumn.create("doubles", 100);
        assertEquals(ColumnType.DOUBLE, doubles.type());
    }

    @Test
    public void testDifference() {
        double[] originalValues = new double[]{32, 42, 40, 57, 52};
        double[] expectedValues = new double[]{MISSING, 10, -2, 17, -5};
        assertTrue(computeAndValidateDifference(originalValues, expectedValues));
    }

    @Test
    public void testDifferenceMissingValuesInColumn() {
        double[] originalValues = new double[]{32, 42, MISSING, 57, 52};
        double[] expectedValues =
                new double[]{MISSING, 10, MISSING, MISSING, -5};
        assertTrue(computeAndValidateDifference(originalValues, expectedValues));
    }

    private boolean computeAndValidateDifference(double[] originalValues, double[] expectedValues) {
        DoubleColumn initial =  DoubleColumn.create("Test", originalValues);
        DoubleColumn difference = initial.difference();
        return validateEquality(expectedValues, difference);
    }

    @Test
    public void testDifferenceEmptyColumn() {
        DoubleColumn initial =  DoubleColumn.create("Test");
        DoubleColumn difference = initial.difference();
        assertEquals(0, difference.size(), "Expecting empty data set.");
    }

    @Test
    public void testCumSum() {
        double[] originalValues = new double[]{32, 42, MISSING, 57, 52, -10, 0};
        double[] expectedValues = new double[]{32, 74, 74, 131, 183, 173, 173};
        DoubleColumn initial =  DoubleColumn.create("Test", originalValues);
        DoubleColumn csum = initial.cumSum();

        assertEquals(expectedValues.length, csum.size(), "Both sets of data should be the same size.");

        for (int index = 0; index < csum.size(); index++) {
            double actual = csum.get(index);
            assertEquals(expectedValues[index], actual, 0, "cumSum() operation at index:" + index + " failed");
        }
    }

    @Test
    public void testCumProd() {
        double[] originalValues = new double[]{1, 2, MISSING, 3, 4};
        double[] expectedValues = new double[]{1, 2, 2, 6, 24};
        DoubleColumn initial =  DoubleColumn.create("Test", originalValues);
        DoubleColumn cprod = initial.cumProd();

        assertEquals(expectedValues.length, cprod.size(), "Both sets of data should be the same size.");

        for (int index = 0; index < cprod.size(); index++) {
            double actual = cprod.get(index);
            assertEquals(expectedValues[index], actual, 0, "cumProd() operation at index:" + index + " failed");
        }
    }

    @Test
    public void testSubtract2Columns() {
        double[] col1Values = new double[]{32.5, MISSING, 42, 57, 52};
        double[] col2Values = new double[]{32, 42, 38.67, MISSING, 52.01};
        double[] expected = new double[]{0.5, MISSING, 3.33, MISSING, -.01};

        DoubleColumn col1 =  DoubleColumn.create("1", col1Values);
        DoubleColumn col2 =  DoubleColumn.create("2", col2Values);

        DoubleColumn difference = col1.subtract(col2);
        assertTrue(validateEquality(expected, difference));

        // change order to verify size of returned column
        difference = col2.subtract(col1);
        expected = new double[]{-0.5, MISSING, -3.33, MISSING, .01};
        assertTrue(validateEquality(expected, difference));
    }

    @Test
    public void testPctChange() {
        double[] originalValues = new double[]{10, 12, 13};
        double[] expectedValues = new double[]{MISSING, 0.2, 0.083333};
        DoubleColumn initial =  DoubleColumn.create("Test", originalValues);
        DoubleColumn pctChange = initial.pctChange();

        assertEquals(expectedValues.length, pctChange.size(), "Both sets of data should be the same size.");

        for (int index = 0; index < pctChange.size(); index++) {
            double actual = pctChange.get(index);
            assertEquals(expectedValues[index], actual, 0.0001, "pctChange() operation at index:" + index + " failed");
        }
    }

    private boolean validateEquality(double[] expectedValues, DoubleColumn col) {
        assertEquals(expectedValues.length, col.size(), "Both sets of data should be the same size.");
        for (int index = 0; index < col.size(); index++) {
            double actual = col.get(index);
            assertEquals(expectedValues[index], actual, 0.01, "value mismatch at index:" + index);
        }
        return true;
    }
    
    // Functional methods

    @Test
    public void testCountAtLeast() {
        assertEquals(2, DoubleColumn.create("t1", new double[] {0, 1, 2}).count(isPositiveOrZeroD, 2));
        assertEquals(0, DoubleColumn.create("t1", new double[] {0, 1, 2}).count(isNegativeD, 2));
    }

    @Test
    public void testCount() {
        assertEquals(3, DoubleColumn.create("t1", new double[] {0, 1, 2}).count(isPositiveOrZeroD));
        assertEquals(0, DoubleColumn.create("t1", new double[] {0, 1, 2}).count(isNegativeD));
    }

    @Test
    public void testAllMatch() {
        assertTrue(DoubleColumn.create("t1", new double[] {0, 1, 2}).allMatch(isPositiveOrZeroD));
        assertFalse(DoubleColumn.create("t1", new double[] {-1, 0, 1}).allMatch(isPositiveOrZeroD));
        assertFalse(DoubleColumn.create("t1", new double[] {1, 0, -1}).allMatch(isPositiveOrZeroD));
    }

    @Test
    public void testAnyMatch() {
        assertTrue(DoubleColumn.create("t1", new double[] {0, 1, 2}).anyMatch(isPositiveOrZeroD));
        assertTrue(DoubleColumn.create("t1", new double[] {-1, 0, -1}).anyMatch(isPositiveOrZeroD));
        assertFalse(DoubleColumn.create("t1", new double[] {0, 1, 2}).anyMatch(isNegativeD));
    }

    @Test
    public void noneMatch() {
        assertTrue(DoubleColumn.create("t1", new double[] {0, 1, 2}).noneMatch(isNegativeD));
        assertFalse(DoubleColumn.create("t1", new double[] {-1, 0, 1}).noneMatch(isNegativeD));
        assertFalse(DoubleColumn.create("t1", new double[] {1, 0, -1}).noneMatch(isNegativeD));
    }

    private <T> void check(Column<T> column, @SuppressWarnings("unchecked") T... ts) {
        assertEquals(ts.length, column.size());
        for (int i = 0; i < ts.length; i++) {
            assertEquals(ts[i], column.get(i));
        }
    }

    @Test
    public void testFilter() {
        Column<Double> filtered = DoubleColumn.create("t1", new double[] {-1, 0, 1}).filter(isPositiveOrZeroD);
        check(filtered, 0.0, 1.0);
    }

    @Test
    public void testMapInto() {
        check(DoubleColumn.create("t1", new double[] {-1, 0, 1}).mapInto(toStringD, StringColumn.create("result")), "-1.0", "0.0", "1.0");
    }

    @Test
    public void testMaxDoubleComparator() {
        assertEquals(Double.valueOf(1.0), DoubleColumn.create("t1", new double[] {-1, 0, 1}).max(Double::compare).get());
        assertFalse(DoubleColumn.create("t1").max((d1, d2) -> (int) (d1 - d2)).isPresent());
    }

    @Test
    public void testMinDoubleComparator() {
        assertEquals(Double.valueOf(-1.0), DoubleColumn.create("t1", new double[] {-1, 0, 1}).min(Double::compare).get());
        assertFalse(DoubleColumn.create("t1").min((d1, d2) -> (int) (d1 - d2)).isPresent());
    }

    @Test
    public void testReduceTDoubleBinaryOperator() {
        assertEquals(1.0, DoubleColumn.create("t1", new double[] {-1, 0, 1}).reduce(1.0, sumD), 0.0);
    }

    @Test
    public void testReduceDoubleBinaryOperator() {
        assertEquals(Double.valueOf(0.0), DoubleColumn.create("t1", new double[] {-1, 0, 1}).reduce(sumD).get());
        assertFalse(DoubleColumn.create("t1", new double[] {}).reduce(sumD).isPresent());
    }

}
