package tech.tablesaw.columns.booleans;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import tech.tablesaw.api.BooleanColumn;

public class BooleanMapUtilsTest {
    private BooleanColumn singleFalse = BooleanColumn.create("", new boolean[]{false});
    private BooleanColumn singleTrue = BooleanColumn.create("", new boolean[]{true});

    @Test
    public void testAnd() {
        BooleanColumn actual = singleTrue.and(singleFalse);
        assertEquals(singleFalse, actual);
    }

    @Test
    public void testOr() {
        BooleanColumn actual = singleFalse.or(singleTrue);
        assertEquals(singleTrue, actual);
    }

    @Test
    public void testAndNot() {
        BooleanColumn actual = singleTrue.andNot(singleFalse);
        assertEquals(singleTrue, actual);
    }

    @Test
    public void testAndNot2() {
        BooleanColumn actual = singleFalse.andNot(singleTrue);
        assertEquals(singleFalse, actual);
    }
}
