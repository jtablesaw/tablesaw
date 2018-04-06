package tech.tablesaw.mapping;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class NumberMapUtilsTest {

    @Test
    public void testNormalize() {
        double[] values = {4, 12, 9, 7, 8, 1, 3, 8, 9, 11};
        NumberColumn test = NumberColumn.create("test", values);
        NumberColumn result = test.normalize();
        assertEquals(0, result.mean(), 0.01);
        assertEquals(1, result.standardDeviation(), 0.01);
    }

    @Test
    public void testAsRatio() {
        double[] values = {4, 1, 1, 2, 2};  // sums to 10
        NumberColumn test = NumberColumn.create("test", values);
        NumberColumn result = test.asRatio();
        assertEquals(.4, result.get(0), 0.01);
        assertEquals(.1, result.get(1), 0.01);
        assertEquals(.2, result.get(3), 0.01);
    }

    @Test
    public void testAsPercent() {
        double[] values = {4, 1, 1, 2, 2};  // sums to 10
        NumberColumn test = NumberColumn.create("test", values);
        NumberColumn result = test.asPercent();
        assertEquals(40, result.get(0), 0.01);
        assertEquals(10, result.get(1), 0.01);
        assertEquals(20, result.get(3), 0.01);
    }

    @Test
    public void testAdd() {
        double[] values = {4, 1, 1, 2, 2};
        NumberColumn test = NumberColumn.create("test", values);
        NumberColumn result = test.add(4);
        assertEquals(8, result.get(0), 0.01);
        assertEquals(5, result.get(1), 0.01);
        assertEquals(6, result.get(3), 0.01);
    }

    @Test
    public void testAdd2() {
        double[] values = {4, 1, 1, 2, 2};
        double[] values2 = {4, 1, 1, 2, 2};
        NumberColumn test = NumberColumn.create("test", values);
        NumberColumn test2 = NumberColumn.create("test2", values2);
        NumberColumn result = test.add(test2);
        assertEquals(8, result.get(0), 0.01);
        assertEquals(2, result.get(1), 0.01);
        assertEquals(4, result.get(3), 0.01);
    }

    @Test
    public void testSubtract() {
        double[] values = {4, 1, 1, 2, 2};
        NumberColumn test = NumberColumn.create("test", values);
        NumberColumn result = test.subtract(4);
        assertEquals(0, result.get(0), 0.01);
        assertEquals(-3, result.get(1), 0.01);
        assertEquals(-2, result.get(3), 0.01);
    }

    @Test
    public void testSubtract2() {
        double[] values = {4, 1, 1, 2, 2};
        double[] values2 = {4, 1, 1, 2, 2};
        NumberColumn test = NumberColumn.create("test", values);
        NumberColumn test2 = NumberColumn.create("test2", values2);
        NumberColumn result = test.subtract(test2);
        assertEquals(0, result.get(0), 0.01);
        assertEquals(0, result.get(1), 0.01);
        assertEquals(0, result.get(3), 0.01);
    }

    @Test
    public void testMultiply() {
        double[] values = {4, 1, 1, 2, 2};
        NumberColumn test = NumberColumn.create("test", values);
        NumberColumn result = test.multiply(4);
        assertEquals(16, result.get(0), 0.01);
        assertEquals(4, result.get(1), 0.01);
        assertEquals(8, result.get(3), 0.01);
    }

    @Test
    public void testMultiply2() {
        double[] values = {4, 1, 1, 2, 2};
        double[] values2 = {4, 1, 1, 2, 2};
        NumberColumn test = NumberColumn.create("test", values);
        NumberColumn test2 = NumberColumn.create("test2", values2);
        NumberColumn result = test.multiply(test2);
        assertEquals(16, result.get(0), 0.01);
        assertEquals(1, result.get(1), 0.01);
        assertEquals(4, result.get(3), 0.01);
    }

    @Test
    public void testDivide() {
        double[] values = {4, 1, 1, 2, 2};
        NumberColumn test = NumberColumn.create("test", values);
        NumberColumn result = test.divide(2);
        assertEquals(2, result.get(0), 0.01);
        assertEquals(0.5, result.get(1), 0.01);
        assertEquals(1.0, result.get(3), 0.01);
    }

    @Test
    public void testDivide2() {
        double[] values = {4, 1, 1, 2, 2};
        double[] values2 = {4, 1, 1, 2, 2};
        NumberColumn test = NumberColumn.create("test", values);
        NumberColumn test2 = NumberColumn.create("test2", values2);
        NumberColumn result = test.divide(test2);
        assertEquals(1, result.get(0), 0.01);
        assertEquals(1, result.get(1), 0.01);
        assertEquals(1, result.get(3), 0.01);
    }

    @Test
    public void lag() {
        NumberColumn n1 = NumberColumn.indexColumn("index", 4, 0);
        NumberColumn n2 = n1.lag(-2);
        Table t = Table.create("tst");
        t.addColumn(n1, n2);
        assertEquals("            tst            \n" +
                " index  |  index lag(-2)  |\n" +
                "---------------------------\n" +
                "     0  |              2  |\n" +
                "     1  |              3  |\n" +
                "     2  |                 |\n" +
                "     3  |                 |", t.print());
    }

    @Test
    public void lead() {
        NumberColumn n1 = NumberColumn.indexColumn("index", 4, 0);
        NumberColumn n2 = n1.lead(1);
        Table t = Table.create("tst");
        t.addColumn(n1, n2);
        assertEquals("            tst            \n" +
                " index  |  index lead(1)  |\n" +
                "---------------------------\n" +
                "     0  |              1  |\n" +
                "     1  |              2  |\n" +
                "     2  |              3  |\n" +
                "     3  |                 |", t.print());
    }

    @Test
    public void testNeg() {
        NumberColumn doubles = NumberColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        NumberColumn newDoubles = doubles.neg();
        assertFalse(newDoubles.isEmpty());
        assertEquals(0 - doubles.get(0), newDoubles.get(0), 0.0001);
    }

    @Test
    public void testRoundInt() {
        double[] values = {4.4, 1.9, 1.5, 2.3, 2.0};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumn newDoubles = doubles.roundInt();
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
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumn otherDoubles = NumberColumn.create("otherDoubles", values2);

        NumberColumn newDoubles = doubles.remainder(otherDoubles);
        assertEquals(0, newDoubles.get(0), 0.001);
    }

    @Test
    public void testSquareAndSqrt() {
        NumberColumn doubles = NumberColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }

        NumberColumn newDoubles = doubles.square();
        NumberColumn revert = newDoubles.sqrt();
        for (int i = 0; i < doubles.size(); i++) {
            assertEquals(doubles.get(i), revert.get(i), 0.01);
        }
    }

    @Test
    public void testCubeAndCbrt() {
        NumberColumn doubles = NumberColumn.create("doubles", 100);
        for (int i = 0; i < 100; i++) {
            doubles.append(RandomUtils.nextDouble(0, 10_000));
        }
        NumberColumn newDoubles = doubles.cube();
        NumberColumn revert = newDoubles.cubeRoot();
        for (int i = 0; i < doubles.size(); i++) {
            assertEquals(doubles.get(i), revert.get(i), 0.01);
        }
    }
}
