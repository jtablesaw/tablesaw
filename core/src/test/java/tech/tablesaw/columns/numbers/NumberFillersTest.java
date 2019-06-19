package tech.tablesaw.columns.numbers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.tablesaw.api.DoubleColumn.create;
import static tech.tablesaw.columns.numbers.fillers.DoubleRangeIterable.range;

import org.junit.jupiter.api.Test;

public class NumberFillersTest {

    protected void assertContentEquals(Iterable<Double> doubles, double... expected) {
        int num = 0;
        for (double value : doubles) {
            assertEquals(expected[num], value, 0.0000001);
            num++;
        }
        assertEquals(expected.length, num);
    }

    @Test
    public void fillWithDouble() {
        assertContentEquals(create("doubles", new double[5]).fillWith(1.0), 1.0, 1.0, 1.0, 1.0, 1.0);
    }
    
    @Test
    public void testFromToBy() {
        assertContentEquals(create("doubles", new double[5]).fillWith(range(1.0, 12.0, 2.5)), 1.0, 3.5, 6.0, 8.5, 11.0);
        assertContentEquals(create("doubles", new double[5]).fillWith(range(1.0, 7.0, 2.5)), 1.0, 3.5, 6.0, 1.0, 3.5);
    }

    @Test
    public void testFromTo() {
        assertContentEquals(create("doubles", new double[5]).fillWith(range(1.0, 6.0)), 1.0, 2.0, 3.0, 4.0, 5.0);
        assertContentEquals(create("doubles", new double[5]).fillWith(range(1.0, 4.0)), 1.0, 2.0, 3.0, 1.0, 2.0);
    }

    @Test
    public void testFromByCount() {
        assertContentEquals(create("doubles", new double[5]).fillWith(range(1.0, 2.5, 5)), 1.0, 3.5, 6.0, 8.5, 11.0);
        assertContentEquals(create("doubles", new double[5]).fillWith(range(1.0, 2.5, 3)), 1.0, 3.5, 6.0, 1.0, 3.5);
    }

    @Test
    public void testFromCount() {
        assertContentEquals(create("doubles", new double[5]).fillWith(range(1.0, 5)), 1.0, 2.0, 3.0, 4.0, 5.0);
        assertContentEquals(create("doubles", new double[5]).fillWith(range(1.0, 3)), 1.0, 2.0, 3.0, 1.0, 2.0);
    }
}
