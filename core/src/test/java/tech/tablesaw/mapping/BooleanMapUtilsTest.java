package tech.tablesaw.mapping;

import org.junit.Test;
import tech.tablesaw.api.BooleanColumn;

import static org.junit.Assert.assertEquals;

public class BooleanMapUtilsTest {
    @Test
    public void testAdd() {
        BooleanColumn booleanColumn = new BooleanColumn("");
        booleanColumn.append(true);

        BooleanColumn booleanColumn2 = new BooleanColumn("");
        booleanColumn2.append(false);

        BooleanColumn actual = booleanColumn.and(booleanColumn2);

        BooleanColumn expected = new BooleanColumn("");
        expected.append(false);
        assertEquals(expected, actual);
    }

    @Test
    public void testOr() {
        BooleanColumn booleanColumn = new BooleanColumn("");
        booleanColumn.append(false);

        BooleanColumn booleanColumn2 = new BooleanColumn("");
        booleanColumn2.append(true);

        BooleanColumn actual = booleanColumn.or(booleanColumn2);

        BooleanColumn expected = new BooleanColumn("");
        expected.append(true);
        assertEquals(expected, actual);
    }
}
