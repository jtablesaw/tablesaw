package tech.tablesaw.api;

import com.google.common.base.Stopwatch;
import io.codearte.jfairy.Fairy;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.table.Relation;
import tech.tablesaw.util.Selection;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.StatUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

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
            doubleColumn.add((double) Math.random());
            booleanColumn.append(fairy.baseProducer().trueOrFalse());
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        table.sortOn("test");
        System.out.println("Sort time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
        stopwatch.reset().start();
        System.out.println(doubleColumn.summary().print());
        stopwatch.reset().start();
        doubleColumn.isLessThan(.5f);
        System.out.println("Search time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    @Ignore
    @Test
    public void testSortAndApplyFilter1() {

        DoubleColumn doubleColumn = new DoubleColumn("test", 1_000_000_000);
        for (int i = 0; i < 1_000_000_000; i++) {
            doubleColumn.add((double) Math.random());
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
            doubleColumn.add((double) Math.random());
        }
        System.out.println("Sorting");
        Stopwatch stopwatch = Stopwatch.createStarted();
        doubleColumn.sortAscending();
        System.out.println("Sort time in ms = " + stopwatch.elapsed(TimeUnit.MILLISECONDS));

    }

    @Test
    public void testIsLessThan() {
        int size = 1_000_000;
        Relation table = Table.create("t");
        DoubleColumn doubleColumn = new DoubleColumn("test", size);
        table.addColumn(doubleColumn);
        for (int i = 0; i < size; i++) {
            doubleColumn.add((double) Math.random());
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
        Relation table = Table.create("t");
        DoubleColumn doubleColumn = new DoubleColumn("test", size);
        table.addColumn(doubleColumn);
        for (int i = 0; i < size; i++) {
            doubleColumn.add((double) Math.random());
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
            doubleColumn.add((double) Math.random());
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
            doubleColumn.add((double) Math.random());
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

        Relation table = Table.create("t");
        DoubleColumn doubleColumn = new DoubleColumn("test", 1_000_000);
        double[] doubles = new double[1_000_000];
        table.addColumn(doubleColumn);
        for (int i = 0; i < 1_000_000; i++) {
            double d = Math.random();
            doubleColumn.add(d);
            doubles[i] = d;
        }
        Selection results;
        RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
        for (int i = 0; i < 100; i++) { // pick a hundred values at random and see if we can find them
            double aDouble = doubles[randomDataGenerator.nextInt(0, 999_999)];
            results = doubleColumn.isEqualTo(aDouble);
            assertEquals(aDouble, doubleColumn.get(results.iterator().next()), .001);
        }
    }

    @Test
    public void testMaxAndMin() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.add(RandomUtils.nextDouble(0, 10_000));
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
            doubles.add(RandomUtils.nextDouble(0, 10_000));
        }
        Column newDoubles = doubles.round();
        assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testLogN() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.add(RandomUtils.nextDouble(0, 10_000));
        }
        Column newDoubles = doubles.logN();
        assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testLog10() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.add(RandomUtils.nextDouble(0, 10_000));
        }
        Column newDoubles = doubles.log10();
        assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testLog1p() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.add(RandomUtils.nextDouble(0, 10_000));
        }
        Column newDoubles = doubles.log1p();
        assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testAbs() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.add(RandomUtils.nextDouble(0, 10_000));
        }
        Column newDoubles = doubles.abs();
        assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testClear() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.add(RandomUtils.nextDouble(0, 10_000));
        }
        assertFalse(doubles.isEmpty());
        doubles.clear();
        assertTrue(doubles.isEmpty());
    }

    @Test
    public void testCountMissing() {
        DoubleColumn doubles = new DoubleColumn("doubles", 10);
        for (int i = 0; i < 10; i++) {
            doubles.add(RandomUtils.nextDouble(0, 1_000));
        }
        assertEquals(0, doubles.countMissing());
        doubles.clear();
        for (int i = 0; i < 10; i++) {
            doubles.add(DoubleColumn.MISSING_VALUE);
        }
        assertEquals(10, doubles.countMissing());
    }


    @Test
    public void testCountUnique() {
        DoubleColumn doubles = new DoubleColumn("doubles", 10);
        double[] uniques = {0.0f, 0.00000001f, -0.000001f, 92923.29340f, 24252, 23442f, 2252, 2342f};
        for (double unique : uniques) {
            doubles.add(unique);
        }
        assertEquals(uniques.length, doubles.countUnique());

        doubles.clear();
        double[] notUniques = {0.0f, 0.00000001f, -0.000001f, 92923.29340f, 24252, 23442f, 2252, 2342f, 0f};

        for (double notUnique : notUniques) {
            doubles.add(notUnique);
        }
        assertEquals(notUniques.length - 1, doubles.countUnique());
    }

    @Test
    public void testUnique() {
        DoubleColumn doubles = new DoubleColumn("doubles", 10);
        double[] uniques = {0.0f, 0.00000001f, -0.000001f, 92923.29340f, 24252, 23442f, 2252, 2342f};
        for (double unique : uniques) {
            doubles.add(unique);
        }
        assertEquals(uniques.length, doubles.unique().size());

        doubles.clear();
        double[] notUniques = {0.0f, 0.00000001f, -0.000001f, 92923.29340f, 24252, 23442f, 2252, 2342f, 0f};

        for (double notUnique : notUniques) {
            doubles.add(notUnique);
        }
        assertEquals(notUniques.length - 1, doubles.unique().size());
    }

    @Test
    public void testIsMissingAndIsNotMissing() {
        DoubleColumn doubles = new DoubleColumn("doubles", 10);
        for (int i = 0; i < 10; i++) {
            doubles.add(RandomUtils.nextDouble(0, 1_000));
        }
        assertEquals(0, doubles.isMissing().size());
        assertEquals(10, doubles.isNotMissing().size());
        doubles.clear();
        for (int i = 0; i < 10; i++) {
            doubles.add(DoubleColumn.MISSING_VALUE);
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
            doubles.add(RandomUtils.nextDouble(0, 10_000));
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
            doubles.add(RandomUtils.nextDouble(0, 10_000));
        }
        assertEquals(size, doubles.size());
        doubles.clear();
        assertEquals(0, doubles.size());
    }

    @Test
    public void testNeg() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.add(RandomUtils.nextDouble(0, 10_000));
        }
        Column newDoubles = doubles.neg();
        assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void tesMod() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        DoubleColumn otherDoubles = new DoubleColumn("otherDoubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.add(RandomUtils.nextDouble(0, 10_000));
            otherDoubles.add(doubles.get(i) - 1.0f);
        }
        Column newDoubles = doubles.remainder(otherDoubles);
        assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testSquareAndSqrt() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.add(RandomUtils.nextDouble(0, 10_000));
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
            doubles.add(RandomUtils.nextDouble(0, 10_000));
        }
        DoubleColumn newDoubles = doubles.cube();
        DoubleColumn revert = newDoubles.cubeRoot();
        for (int i = 0; i < doubles.size(); i++) {
            assertEquals(doubles.get(i), revert.get(i), 0.01);
        }
    }

    // todo - question - does this test really test difference method?
    @Test
    public void testDifference() {
        DoubleColumn doubles = new DoubleColumn("doubles", 100);
        DoubleColumn otherDoubles = new DoubleColumn("otherDoubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.add(RandomUtils.nextDouble(0, 10_000));
            otherDoubles.add(doubles.get(i) - 1.0f);
        }
        DoubleColumn diff = doubles.subtract(otherDoubles);
        for (int i = 0; i < doubles.size(); i++) {
            assertEquals(doubles.get(i), otherDoubles.get(i) + 1.0, 0.01);
        }
    }

    @Test
    public void testDifferencePositive() {
        double[] originalValues = new double[]{32, 42, 40, 57, 52};
        double[] expectedValues = new double[]{Double.NaN, 10, -2, 17, -5};

        DoubleColumn initial = new DoubleColumn("Test", originalValues.length);
        for (double value : originalValues) {
            initial.add(value);
        }
        DoubleColumn difference = initial.difference();
        assertEquals("Both sets of data should be the same size.", expectedValues.length, difference.size());
        for (int index = 0; index < difference.size(); index++) {
            double actual = difference.get(index);
            if (index == 0) {
                assertTrue("difference operation at index:" + index + " failed", Double.isNaN(actual));
            } else {
                assertEquals("difference operation at index:" + index + " failed", expectedValues[index], actual, 0);
            }
        }
    }

    @Test
    public void testDifferenceNegative() {
        double[] originalValues = new double[]{32, 42, 40, 57, 52};
        double[] expectedValues = new double[]{Double.MAX_VALUE, Double.MIN_VALUE, -12, 117, 5};

        DoubleColumn initial = new DoubleColumn("Test", originalValues.length);
        for (double value : originalValues) {
            initial.add(value);
        }
        DoubleColumn difference = initial.difference();
        assertEquals("Both sets of data should be the same size.", expectedValues.length, difference.size());
        for (int index = 0; index < difference.size(); index++) {
            double actual = difference.get(index);
            if (index == 0) {
                assertTrue("difference operation at index:" + index + " failed", Double.isNaN(actual));
            } else {
                assertNotEquals("difference operation at index:" + index + " failed", expectedValues[index], actual, 0.0);
            }
        }
    }

}