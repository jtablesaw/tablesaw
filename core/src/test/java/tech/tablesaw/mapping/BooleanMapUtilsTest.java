package tech.tablesaw.mapping;

import org.junit.Test;
import tech.tablesaw.api.BooleanColumn;

import static org.junit.Assert.*;

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
}
