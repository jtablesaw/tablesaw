package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QueryHelperTest {

    private Table t;

    @BeforeEach
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
