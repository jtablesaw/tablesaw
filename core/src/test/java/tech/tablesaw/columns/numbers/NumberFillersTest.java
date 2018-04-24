package tech.tablesaw.columns.numbers;

import static org.junit.Assert.assertEquals;
import static tech.tablesaw.api.DoubleColumn.create;
import static tech.tablesaw.columns.numbers.fillers.DoubleRangeIterable.range;

import org.junit.Test;

public class NumberFillersTest {

    protected void testValues(Iterable<Double> doubles, double... expected) {
        int num = 0;
        for (double value : doubles) {
            assertEquals(expected[num], value, 0.0000001);
            num++;
        }
        assertEquals(expected.length, num);
    }

    @Test
    public void testFromToBy() {
        testValues(create("doubles", new double[5]).fillWith(range(1.0, 12.0, 2.5)), 1.0, 3.5, 6.0, 8.5, 11.0);
        testValues(create("doubles", new double[5]).fillWith(range(1.0, 7.0, 2.5)), 1.0, 3.5, 6.0, 1.0, 3.5);
    }

    @Test
    public void testFromTo() {
        testValues(create("doubles", new double[5]).fillWith(range(1.0, 6.0)), 1.0, 2.0, 3.0, 4.0, 5.0);
        testValues(create("doubles", new double[5]).fillWith(range(1.0, 4.0)), 1.0, 2.0, 3.0, 1.0, 2.0);
    }

    @Test
    public void testFromByCount() {
        testValues(create("doubles", new double[5]).fillWith(range(1.0, 2.5, 5)), 1.0, 3.5, 6.0, 8.5, 11.0);
        testValues(create("doubles", new double[5]).fillWith(range(1.0, 2.5, 3)), 1.0, 3.5, 6.0, 1.0, 3.5);
    }

    @Test
    public void testFromCount() {
        testValues(create("doubles", new double[5]).fillWith(range(1.0, 5)), 1.0, 2.0, 3.0, 4.0, 5.0);
        testValues(create("doubles", new double[5]).fillWith(range(1.0, 3)), 1.0, 2.0, 3.0, 1.0, 2.0);
    }
}
