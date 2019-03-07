package tech.tablesaw.columns.booleans;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.BooleanColumn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.tablesaw.api.BooleanColumn.create;
import static tech.tablesaw.columns.booleans.fillers.BooleanIterable.bits;

public class BooleanFillersTest {

    protected void assertContentEquals(Iterable<Boolean> booleans, boolean... expected) {
        int num = 0;
        for (boolean value : booleans) {
            assertEquals(expected[num], value);
            num++;
        }
        assertEquals(expected.length, num);
    }

    @Test
    public void test() {
        BooleanColumn booleanColumn = create("booleans", new boolean[5]);
        assertContentEquals(
                booleanColumn.fillWith(bits(0b110, 3)),
                true, true, false, true, true);
    }
}
