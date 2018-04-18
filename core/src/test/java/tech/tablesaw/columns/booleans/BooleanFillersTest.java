package tech.tablesaw.columns.booleans;

import static org.junit.Assert.assertEquals;
import static tech.tablesaw.api.BooleanColumn.create;
import static tech.tablesaw.columns.booleans.fillers.BooleanIterable.bits;

import org.junit.Test;

import it.unimi.dsi.fastutil.booleans.BooleanIterable;

public class BooleanFillersTest {

	protected void testValues(BooleanIterable booleans, boolean... expected) {
		int num = 0;
		for (boolean value : booleans) {
			assertEquals(expected[num], value);
			num++;
		}
		assertEquals(expected.length, num);
	}

    @Test
    public void test() {
        testValues(create("booleans", new boolean[5]).fillWith(bits(0b110, 3)), true, true, false, true, true);
    }
}
