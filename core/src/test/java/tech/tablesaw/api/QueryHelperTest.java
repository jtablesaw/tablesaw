package tech.tablesaw.api;

import tech.tablesaw.columns.string.StringColumnReference;
import tech.tablesaw.filtering.Filter;
import tech.tablesaw.util.selection.Selection;
import org.junit.Before;
import org.junit.Test;

import static tech.tablesaw.api.QueryHelper.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class QueryHelperTest {

    private Table t;

    @Before
    public void setUp() throws Exception {
        t = Table.read().csv("../data/bush.csv");
    }

    @Test
    public void test1() {
        Table result = t.selectWhere(
                and(
                        stringColumn("who").startsWith("f"),
                        dateColumn("date").isInYear(2002),
                        numberColumn("approval").isLessThan(75)
                )
        );
        assertTrue(result.get(0, "who").startsWith("f"));
        System.out.println(result);
    }

    @Test
    public void test3() {
        Table result = t.selectWhere(
                        stringColumn("who").isIn("fox"));
        assertTrue(result.get(0, "who").equals("fox"));

        result = t.selectWhere(
                        stringColumn("who").isNotIn("fox", "zogby"));
        assertFalse(result.get(0, "who").startsWith("f"));

        System.out.println(result);
    }

    @Test
    public void test2() {
        Table result = t.selectWhere(
                t.stringColumn("who").startsWith("f"));

        Table result2 = result.selectWhere(
                stringColumn("who").endsWith("x"));

        assertTrue(result.get(0, "who").startsWith("f"));
        StringColumn who = t.stringColumn("who");

        // sent to a column
        Selection next = who.startsWith("fox");
        Selection s = who.endsWith("y");

        StringColumnReference ref = who.column;
        Filter f = ref.startsWith("a");


        // sent to a string
        boolean b = "foxy".startsWith("fox");

        // sent to a ColumnReference (as used in a table query)
        Table t =
                result.selectWhere(
                        stringColumn("who")
                                .startsWith("fox"));

        // used in a simple column query
        StringColumn x = who.select(ref.startsWith("fox"));

        // used in a complex column query
        StringColumn y =
                who.select(
                    and(ref.startsWith("f"),
                        ref.containsString("x"),
                            ref.endsWith("y"))
        );




        System.out.println(result);
    }
}
