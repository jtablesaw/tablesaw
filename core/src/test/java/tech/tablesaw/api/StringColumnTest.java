/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.api;

import tech.tablesaw.columns.strings.StringColumnFormatter;
import tech.tablesaw.selection.Selection;

import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.TestDataUtil;

import java.util.List;
import java.util.function.Function;

import static tech.tablesaw.api.QueryHelper.both;
import static tech.tablesaw.columns.strings.StringPredicates.isEqualToIgnoringCase;
import static org.junit.Assert.*;

public class StringColumnTest {

    private final StringColumn column = StringColumn.create("testing");

    @Before
    public void setUp() {
        column.append("Value 1");
        column.append("Value 2");
        column.append("Value 3");
        column.append("Value 4");
    }

    @Test
    public void testConditionalSet() {
        column.set("no Value", column.isEqualTo("Value 4"));
        assertTrue(column.contains("no Value"));
        assertFalse(column.contains("Value 4"));
    }

    @Test
    public void lag() {
        StringColumn c1 = column.lag(1);
        Table t = Table.create("Test");
        t.addColumn(column, c1);
        assertEquals("", c1.get(0));
        assertEquals("Value 1", c1.get(1));
        assertEquals("Value 2", c1.get(2));
    }

    @Test
    public void lag2() {
        StringColumn c1 = column.lag(-1);
        Table t = Table.create("Test");
        t.addColumn(column, c1);
        assertEquals("Value 2", c1.get(0));
        assertEquals("Value 3", c1.get(1));
        assertEquals("", c1.get(3));
    }

    @Test
    public void lead() {
        StringColumn c1 = column.lead(1);
        Table t = Table.create("Test");
        t.addColumn(column, c1);
        assertEquals("Value 2", c1.get(0));
        assertEquals("Value 3", c1.get(1));
        assertEquals("", c1.get(3));
    }

    @Test
    public void testSelectWhere() {
        StringColumn result = column.select(column.equalsIgnoreCase("VALUE 1"));
        assertEquals(1, result.size());
    }

    @Test
    public void testDefaultReturnValue() {
        assertEquals(-1, column.firstIndexOf("test"));
    }

    @Test
    public void testType() {
        assertEquals(ColumnType.STRING, column.type());
    }

    @Test
    public void testGetString() {
        assertEquals("Value 2", column.getString(1));
    }

    @Test
    public void testSize() {
        assertEquals(4, column.size());
    }

    @Test
    public void testGetDummies() {
        List<BooleanColumn> dummies = column.getDummies();
        assertEquals(4, dummies.size());
    }

    @Test
    public void testToString() {
        assertEquals("String column: testing", column.toString());
    }

    @Test
    public void testMax() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        assertTrue("Wyoming".equals(stringColumn.top(5).get(0)));
    }

    @Test
    public void testMin() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        assertTrue("Alabama".equals(stringColumn.bottom(5).get(0)));
    }

    @Test
    public void testStartsWith() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());

        StringColumn selection = stringColumn.select(stringColumn.startsWith("A"));
        assertEquals("Alabama", selection.get(0));
        assertEquals("Alaska", selection.get(1));
        assertEquals("Arizona", selection.get(2));
        assertEquals("Arkansas", selection.get(3));

        selection = stringColumn.select(stringColumn.startsWith("T"));
        assertEquals("Tennessee", selection.get(0));
        assertEquals("Texas", selection.get(1));
    }

    @Test
    public void testFormattedPrinting() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());

        Function<String, String> formatter = s -> String.format("[[%s]]", s);

        stringColumn.setPrintFormatter(new StringColumnFormatter(formatter));
        assertEquals("[[Alabama]]", stringColumn.getString(0));
    }

    @Test
    public void testSelectWithFilter() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());

        StringColumn selection = stringColumn.select(
                both(stringColumn.startsWith("A"),
                    stringColumn.containsString("kan")));

        assertEquals(1, selection.size());
        assertEquals("Arkansas", selection.getString(0));
    }

    @Test
    public void testIsNotEqualTo() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());

        Selection selection = stringColumn.isNotEqualTo("Alabama");
        StringColumn result = (StringColumn) stringColumn.subset(selection);
        assertEquals(result.size(), stringColumn.size() - 1);
        assertFalse(result.contains("Alabama"));
        assertEquals(stringColumn.size(), 51);
    }

    @Test
    public void testColumnEqualIgnoringCase() {
        StringColumn other = column.copy();
        other.set(1, "Some other thing");
        other.set(2, other.get(2).toUpperCase());
        assertFalse(other.contains("Value 3"));
        assertTrue(other.contains("Value 1"));
        assertFalse(other.contains("Value 2"));
        assertTrue(other.contains("Some other thing"));
        assertTrue(other.contains("VALUE 3"));
        assertTrue(other.contains("Value 4"));
        assertTrue("Value 3".equalsIgnoreCase("VALUE 3"));
        assertEquals(4, other.size());
        StringColumn result = (StringColumn) column.subset(column.eval(isEqualToIgnoringCase, other));
        assertEquals(3, result.size());
        System.out.println(result);
    }

    @Test
    public void testIsEqualTo() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        stringColumn.append("Alabama");  // so we have two entries
        Selection selection = stringColumn.isEqualTo("Alabama");
        StringColumn result = (StringColumn) stringColumn.subset(selection);

        assertEquals(2, result.size());
        assertTrue(result.contains("Alabama"));

        Selection result2 = stringColumn.isEqualTo("Alabama");
        assertEquals(2, result2.size());
        stringColumn = stringColumn.select(result2);
        assertTrue(stringColumn.contains("Alabama"));
    }

    @Test
    public void testIsNotEqualTo2() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());

        Selection selection2 = stringColumn.isNotEqualTo("Yugoslavia");
        assertEquals(selection2.size(), 51);
        StringColumn result2 = (StringColumn) stringColumn.subset(selection2);
        assertEquals(result2.size(), stringColumn.size());
    }

    @Test
    public void testIsIn() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        StringColumn selection = stringColumn.select(stringColumn.isIn("Alabama", "Texas"));
        assertEquals("Alabama", selection.get(0));
        assertEquals("Texas", selection.get(1));
        assertEquals(2, selection.size());
    }

    @Test
    public void testIsNotIn() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        StringColumn selection = stringColumn.select(stringColumn.isNotIn("Alabama", "Texas"));
        assertEquals("Alaska", selection.get(0));
        assertEquals("Arizona", selection.get(1));
        assertEquals("Arkansas", selection.get(2));
        assertEquals(49, selection.size());
    }

    @Test
    public void testToList() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        List<String> states = stringColumn.asList();
        assertEquals(51, states.size()); //includes Wash. DC
    }
}