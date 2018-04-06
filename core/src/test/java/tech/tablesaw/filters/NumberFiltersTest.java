package tech.tablesaw.filters;

import org.junit.Test;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.selection.Selection;

import static org.junit.Assert.assertEquals;

public class NumberFiltersTest {

    @Test
    public void testIsEqualTo() {
        double[] values = {4, 1, 1, 2, 2};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        Selection selection = doubles.isEqualTo(1.0);
        assertEquals(1, selection.get(0));
        assertEquals(2, selection.get(1));
        assertEquals(2, selection.size());
    }

    @Test
    public void testIsNotEqualTo() {
        double[] values = {4, 1, 1, 2, 2};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        Selection selection = doubles.isNotEqualTo(1.0);
        assertEquals(0, selection.get(0));
        assertEquals(3, selection.get(1));
        assertEquals(4, selection.get(2));
        assertEquals(3, selection.size());
    }

    @Test
    public void testIsZero() {
        double[] values = {4, 0, -1};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        Selection selection = doubles.isZero();
        assertEquals(1, selection.get(0));
        assertEquals(1, selection.size());
    }

    @Test
    public void testIsPositive() {
        double[] values = {4, 0, -1};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        Selection selection = doubles.isPositive();
        assertEquals(0, selection.get(0));
        assertEquals(1, selection.size());
    }

    @Test
    public void testIsNegative() {
        double[] values = {4, 0, -0.00001};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        Selection selection = doubles.isNegative();
        assertEquals(2, selection.get(0));
        assertEquals(1, selection.size());
    }

    @Test
    public void testIsNonNegative() {
        double[] values = {4, 0, -0.00001};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        Selection selection = doubles.isNonNegative();
        assertEquals(0, selection.get(0));
        assertEquals(1, selection.get(1));
        assertEquals(2, selection.size());
    }

    @Test
    public void testIsGreaterThanOrEqualTo() {
        double[] values = {4, 0, -0.00001};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        Selection selection = doubles.isGreaterThanOrEqualTo(0.0);
        assertEquals(0, selection.get(0));
        assertEquals(1, selection.get(1));
        assertEquals(2, selection.size());
    }

    @Test
    public void testIsLessThanOrEqualTo() {
        double[] values = {4, 0, -0.00001};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        Selection selection = doubles.isLessThanOrEqualTo(0.0);
        assertEquals(1, selection.get(0));
        assertEquals(2, selection.get(1));
        assertEquals(2, selection.size());
    }

    @Test
    public void testIsLessThan() {
        double[] values = {4, 0, -0.00001, 5.0};
        double[] values2 = {4, 11, -3.00001, 5.1};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumn doubles2 = NumberColumn.create("doubles2", values2);
        Selection selection = doubles.isLessThan(doubles2);
        assertEquals(1, selection.get(0));
        assertEquals(3, selection.get(1));
        assertEquals(2, selection.size());
    }

    @Test
    public void testIsLessThanOrEqualTo1() {
        double[] values = {4, 0, -0.00001, 5.0};
        double[] values2 = {4, 11, -3.00001, 5.1};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumn doubles2 = NumberColumn.create("doubles2", values2);
        Selection selection = doubles.isLessThanOrEqualTo(doubles2);
        assertEquals(0, selection.get(0));
        assertEquals(1, selection.get(1));
        assertEquals(3, selection.get(2));
        assertEquals(3, selection.size());
    }

    @Test
    public void testIsGreaterThan() {
        double[] values = {4, 0, -0.00001, 5.0};
        double[] values2 = {4, 11, -3.00001, 5.1};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumn doubles2 = NumberColumn.create("doubles2", values2);
        Selection selection = doubles.isGreaterThan(doubles2);
        assertEquals(2, selection.get(0));
        assertEquals(1, selection.size());
    }

    @Test
    public void testIsGreaterThanOrEqualTo1() {
        double[] values = {4, 0, -0.00001, 5.0, 4.44443};
        double[] values2 = {4, 11, -3.00001, 5.1, 4.44443};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumn doubles2 = NumberColumn.create("doubles2", values2);
        Selection selection = doubles.isGreaterThanOrEqualTo(doubles2);
        assertEquals(0, selection.get(0));
        assertEquals(2, selection.get(1));
        assertEquals(4, selection.get(2));
        assertEquals(3, selection.size());
    }

    @Test
    public void testIsEqualTo1() {
        double[] values = {4, 0, -0.00001, 5.0, 4.44443};
        double[] values2 = {4, 11, -3.00001, 5.1, 4.44443};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumn doubles2 = NumberColumn.create("doubles2", values2);
        Selection selection = doubles.isEqualTo(doubles2);
        assertEquals(0, selection.get(0));
        assertEquals(4, selection.get(1));
        assertEquals(2, selection.size());
    }

    @Test
    public void testIsNotEqualTo1() {
        double[] values = {4, 0, -0.00001, 5.0, 4.44443};
        double[] values2 = {4, 11, -3.00001, 5.1, 4.44443};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumn doubles2 = NumberColumn.create("doubles2", values2);
        Selection selection = doubles.isNotEqualTo(doubles2);
        assertEquals(1, selection.get(0));
        assertEquals(2, selection.get(1));
        assertEquals(3, selection.get(2));
        assertEquals(3, selection.size());
    }
}
