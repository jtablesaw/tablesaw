package tech.tablesaw.api;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class QueryHelperTest {

    private Table t;

    @Before
    public void setUp() throws Exception {
        t = Table.read().csv("../data/bush.csv");
    }

    @Test
    public void test1() {
        Table result = t.where(
                        t.stringColumn("who").startsWith("f")
                                .and(t.dateColumn("date").isInYear(2002)
                                        .and(t.numberColumn("approval").isLessThan(75))
                )
        );
        assertTrue(result.getString(0, "who").startsWith("f"));
    }

    @Test
    public void test3() {
        Table result = t.where(t.stringColumn("who").isIn("fox"));
        assertEquals("fox", result.getString(0, "who"));

        result = t.where(t.stringColumn("who").isNotIn("fox", "zogby"));
        assertFalse(result.getString(0, "who").startsWith("f"));
    }

    @Test
    public void test2() {
        Table result = t.where(
                t.stringColumn("who").startsWith("f"));

        assertTrue(result.getString(0, "who").startsWith("f"));
    }
}
