package tech.tablesaw.filters;

import org.junit.Test;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.columns.numbers.NumberColumnReference;
import tech.tablesaw.selection.Selection;

import static org.junit.Assert.assertEquals;

public class NumberTableFiltersTest {

    @Test
    public void testIsEqualTo() {
        double[] values = {4, 1, 1, 2, 2};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumnReference reference = new NumberColumnReference(doubles.name());
        Selection selection = reference.isEqualTo(1.0).apply(doubles);
        assertEquals(1, selection.get(0));
        assertEquals(2, selection.get(1));
        assertEquals(2, selection.size());
    }

    @Test
    public void testIsMissing() {
        double[] values = {4, 1, Double.NaN, 2, 2};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumnReference reference = new NumberColumnReference(doubles.name());
        Selection selection = reference.isMissing().apply(doubles);
        assertEquals(2, selection.get(0));
        assertEquals(1, selection.size());
    }

    @Test
    public void testNotIn() {
        double[] values = {4, 1, Double.NaN, 2, 2};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        double[] comparison = {1, 2};
        NumberColumnReference reference = new NumberColumnReference(doubles.name());
        Selection selection = reference.isNotIn(comparison).apply(doubles);
        assertEquals(0, selection.get(0));
        assertEquals(2, selection.get(1));
        assertEquals(2, selection.size());
    }

    @Test
    public void testIsNotMissing() {
        double[] values = {4, 1, Double.NaN, 2, 2};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumnReference reference = new NumberColumnReference(doubles.name());
        Selection selection = reference.isNotMissing().apply(doubles);
        assertEquals(0, selection.get(0));
        assertEquals(1, selection.get(1));
        assertEquals(4, selection.size());
    }

    @Test
    public void testIsNotEqualTo() {
        double[] values = {4, 1, 1, 2, 2};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumnReference reference = new NumberColumnReference(doubles.name());
        Selection selection = reference.isNotEqualTo(1.0).apply(doubles);
        assertEquals(0, selection.get(0));
        assertEquals(3, selection.get(1));
        assertEquals(4, selection.get(2));
        assertEquals(3, selection.size());
    }

    @Test
    public void testIsZero() {
        double[] values = {4, 0, -1};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumnReference reference = new NumberColumnReference(doubles.name());
        Selection selection = reference.isZero().apply(doubles);
        assertEquals(1, selection.get(0));
        assertEquals(1, selection.size());
    }

    @Test
    public void testIsPositive() {
        double[] values = {4, 0, -1};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumnReference reference = new NumberColumnReference(doubles.name());
        Selection selection = reference.isPositive().apply(doubles);
        assertEquals(0, selection.get(0));
        assertEquals(1, selection.size());
    }

    @Test
    public void testIsNegative() {
        double[] values = {4, 0, -0.00001};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumnReference reference = new NumberColumnReference(doubles.name());
        Selection selection = reference.isNegative().apply(doubles);
        assertEquals(2, selection.get(0));
        assertEquals(1, selection.size());
    }

    @Test
    public void testIsNonNegative() {
        double[] values = {4, 0, -0.00001};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumnReference reference = new NumberColumnReference(doubles.name());
        Selection selection = reference.isNonNegative().apply(doubles);
        assertEquals(0, selection.get(0));
        assertEquals(1, selection.get(1));
        assertEquals(2, selection.size());
    }

    @Test
    public void testIsGreaterThanOrEqualTo() {
        double[] values = {4, 0, -0.00001};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumnReference reference = new NumberColumnReference(doubles.name());
        Selection selection = reference.isGreaterThanOrEqualTo(0.0).apply(doubles);
        assertEquals(0, selection.get(0));
        assertEquals(1, selection.get(1));
        assertEquals(2, selection.size());
    }

    @Test
    public void testIsLessThanOrEqualTo() {
        double[] values = {4, 0, -0.00001};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumnReference reference = new NumberColumnReference(doubles.name());
        Selection selection = reference.isLessThanOrEqualTo(0.0).apply(doubles);
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
        NumberColumnReference reference = new NumberColumnReference(doubles.name());
        Selection selection = reference.isLessThan(doubles2).apply(doubles);
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
        NumberColumnReference reference = new NumberColumnReference(doubles.name());
        Selection selection = reference.isLessThanOrEqualTo(doubles2).apply(doubles);
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
        NumberColumnReference reference = new NumberColumnReference(doubles.name());
        Selection selection = reference.isGreaterThan(doubles2).apply(doubles);
        assertEquals(2, selection.get(0));
        assertEquals(1, selection.size());
    }


    @Test
    public void testIsGreaterThanOrEqualTo1() {
        double[] values = {4, 0, -0.00001, 5.0, 4.44443};
        double[] values2 = {4, 11, -3.00001, 5.1, 4.44443};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumn doubles2 = NumberColumn.create("doubles2", values2);
        NumberColumnReference reference = new NumberColumnReference(doubles.name());
        Selection selection = reference.isGreaterThanOrEqualTo(doubles2).apply(doubles);
        assertEquals(0, selection.get(0));
        assertEquals(2, selection.get(1));
        assertEquals(4, selection.get(2));
        assertEquals(3, selection.size());
    }

    @Test
    public void testColumnEqualTo() {
        double[] values = {4, 0, -0.00001, 5.0, 4.44443};
        double[] values2 = {4, 11, -3.00001, 5.1, 4.44443};
        NumberColumn doubles = NumberColumn.create("doubles", values);
        NumberColumn doubles2 = NumberColumn.create("doubles2", values2);
        NumberColumnReference reference = new NumberColumnReference("doubles");
        Selection selection = reference.isEqualTo(doubles2).apply(doubles);
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
