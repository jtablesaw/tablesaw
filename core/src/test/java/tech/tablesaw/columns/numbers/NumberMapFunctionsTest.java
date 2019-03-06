package tech.tablesaw.columns.numbers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;

public class NumberMapFunctionsTest {

    private final static String LINE_END = System.lineSeparator();

    @Test
    public void testNormalize() {
        double[] values = {4, 12, 9, 7, 8, 1, 3, 8, 9, 11};
        DoubleColumn test =  DoubleColumn.create("test", values);
        DoubleColumn result = test.normalize();
        assertEquals(0, result.mean(), 0.01);
        assertEquals(1, result.standardDeviation(), 0.01);
    }

    @Test
    public void testAsRatio() {
        double[] values = {4, 1, 1, 2, 2};  // sums to 10
        DoubleColumn test =  DoubleColumn.create("test", values);
        DoubleColumn result = test.asRatio();
        assertEquals(.4, result.get(0), 0.01);
        assertEquals(.1, result.get(1), 0.01);
        assertEquals(.2, result.get(3), 0.01);
    }

    @Test
    public void testAsPercent() {
        double[] values = {4, 1, 1, 2, 2};  // sums to 10
        DoubleColumn test =  DoubleColumn.create("test", values);
        DoubleColumn result = test.asPercent();
        assertEquals(40, result.get(0), 0.01);
        assertEquals(10, result.get(1), 0.01);
        assertEquals(20, result.get(3), 0.01);
    }

    @Test
    public void testAdd() {
        double[] values = {4, 1, 1, 2, 2};
        DoubleColumn test =  DoubleColumn.create("test", values);
        DoubleColumn result = test.add(4);
        assertEquals(8, result.get(0), 0.01);
        assertEquals(5, result.get(1), 0.01);
        assertEquals(6, result.get(3), 0.01);
    }

    @Test
    public void testAdd2() {
        double[] values = {4, 1, 1, 2, 2};
        double[] values2 = {4, 1, 1, 2, 2};
        DoubleColumn test =  DoubleColumn.create("test", values);
        DoubleColumn test2 =  DoubleColumn.create("test2", values2);
        DoubleColumn result = test.add(test2);
        assertEquals(8, result.get(0), 0.01);
        assertEquals(2, result.get(1), 0.01);
        assertEquals(4, result.get(3), 0.01);
    }

    @Test
    public void testSubtract() {
        double[] values = {4, 1, 1, 2, 2};
        DoubleColumn test =  DoubleColumn.create("test", values);
        DoubleColumn result = test.subtract(4);
        assertEquals(0, result.get(0), 0.01);
        assertEquals(-3, result.get(1), 0.01);
        assertEquals(-2, result.get(3), 0.01);
    }

    @Test
    public void testSubtract2() {
        double[] values = {4, 1, 1, 2, 2};
        double[] values2 = {4, 1, 1, 2, 2};
        DoubleColumn test =  DoubleColumn.create("test", values);
        DoubleColumn test2 =  DoubleColumn.create("test2", values2);
        DoubleColumn result = test.subtract(test2);
        assertEquals(0, result.get(0), 0.01);
        assertEquals(0, result.get(1), 0.01);
        assertEquals(0, result.get(3), 0.01);
    }

    @Test
    public void testMultiply() {
        double[] values = {4, 1, 1, 2, 2};
        DoubleColumn test =  DoubleColumn.create("test", values);
        DoubleColumn result = test.multiply(4);
        assertEquals(16, result.get(0), 0.01);
        assertEquals(4, result.get(1), 0.01);
        assertEquals(8, result.get(3), 0.01);
    }

    @Test
    public void testMultiply2() {
        double[] values = {4, 1, 1, 2, 2};
        double[] values2 = {4, 1, 1, 2, 2};
        DoubleColumn test =  DoubleColumn.create("test", values);
        DoubleColumn test2 =  DoubleColumn.create("test2", values2);
        DoubleColumn result = test.multiply(test2);
        assertEquals(16, result.get(0), 0.01);
        assertEquals(1, result.get(1), 0.01);
        assertEquals(4, result.get(3), 0.01);
    }

    @Test
    public void testDivide() {
        double[] values = {4, 1, 1, 2, 2};
        DoubleColumn test =  DoubleColumn.create("test", values);
        DoubleColumn result = test.divide(2);
        assertEquals(2, result.get(0), 0.01);
        assertEquals(0.5, result.get(1), 0.01);
        assertEquals(1.0, result.get(3), 0.01);
    }

    @Test
    public void testDivide2() {
        double[] values = {4, 1, 1, 2, 2};
        double[] values2 = {4, 1, 1, 2, 2};
        DoubleColumn test =  DoubleColumn.create("test", values);
        DoubleColumn test2 =  DoubleColumn.create("test2", values2);
        DoubleColumn result = test.divide(test2);
        assertEquals(1, result.get(0), 0.01);
        assertEquals(1, result.get(1), 0.01);
        assertEquals(1, result.get(3), 0.01);
    }

    @Test
    public void lag() {
	IntColumn n1 = IntColumn.indexColumn("index", 4, 0);
        Table t = Table.create("tst");
        t.addColumns(n1, n1.lag(-2));
        assertEquals("            tst            " + LINE_END +
                " index  |  index lag(-2)  |" + LINE_END +
                "---------------------------" + LINE_END +
                "     0  |              2  |" + LINE_END +
                "     1  |              3  |" + LINE_END +
                "     2  |                 |" + LINE_END +
                "     3  |                 |", t.print());
    }

    @Test
    public void lead() {
	IntColumn n1 = IntColumn.indexColumn("index", 4, 0);
        Table t = Table.create("tst");
        t.addColumns(n1, n1.lead(1));
        assertEquals("            tst            " + LINE_END +
                " index  |  index lead(1)  |" + LINE_END +
                "---------------------------" + LINE_END +
                "     0  |              1  |" + LINE_END +
                "     1  |              2  |" + LINE_END +
                "     2  |              3  |" + LINE_END +
                "     3  |                 |", t.print());
    }

    @Test
    public void testNeg() {
        DoubleColumn doubles =  DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        DoubleColumn newDoubles = doubles.neg();
        assertFalse(newDoubles.isEmpty());
        assertEquals(0 - doubles.get(0), newDoubles.get(0), 0.0001);
    }

    @Test
    public void testRoundInt() {
        double[] values = {4.4, 1.9, 1.5, 2.3, 2.0};
        DoubleColumn doubles =  DoubleColumn.create("doubles", values);
        DoubleColumn newDoubles = doubles.roundInt();
        assertEquals(4, newDoubles.get(0), 0.0001);
        assertEquals(2, newDoubles.get(1), 0.0001);
        assertEquals(2, newDoubles.get(2), 0.0001);
        assertEquals(2, newDoubles.get(3), 0.0001);
        assertEquals(2, newDoubles.get(4), 0.0001);
    }

    @Test
    public void testMod() {
        double[] values = {4, 1, 1, 2, 2};
        double[] values2 = {4, 1, 1, 2, 2};
        DoubleColumn doubles =  DoubleColumn.create("doubles", values);
        DoubleColumn otherDoubles =  DoubleColumn.create("otherDoubles", values2);

        DoubleColumn newDoubles = doubles.remainder(otherDoubles);
        assertEquals(0, newDoubles.get(0), 0.001);
    }

    @Test
    public void testSquareAndSqrt() {
        DoubleColumn doubles =  DoubleColumn.create("doubles", 100);
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
    public void testCubeAndCbrt() {
        DoubleColumn doubles =  DoubleColumn.create("doubles", 100);
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
    public void testLog1p() {
        DoubleColumn doubles =  DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        DoubleColumn newDoubles = doubles.log1p();
        assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testAbs() {
        double[] values = {4.4, -1.9, -1.5, 2.3, 0.0};
        DoubleColumn doubles =  DoubleColumn.create("doubles", values);
        DoubleColumn newDoubles = doubles.abs();
        assertEquals(4.4, newDoubles.get(0), 0.0001);
        assertEquals(1.9, newDoubles.get(1), 0.0001);
        assertEquals(1.5, newDoubles.get(2), 0.0001);
        assertEquals(2.3, newDoubles.get(3), 0.0001);
        assertEquals(0, newDoubles.get(4), 0.0001);

    }


    @Test
    public void testRound() {
        DoubleColumn doubles =  DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        DoubleColumn newDoubles = doubles.round();
        assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testLogN() {
        DoubleColumn doubles =  DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        DoubleColumn newDoubles = doubles.logN();
        assertFalse(newDoubles.isEmpty());
    }

    @Test
    public void testLog10() {
        DoubleColumn doubles =  DoubleColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        DoubleColumn newDoubles = doubles.log10();
        assertFalse(newDoubles.isEmpty());
    }
}
