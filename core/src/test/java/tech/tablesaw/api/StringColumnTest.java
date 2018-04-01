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

import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.TestDataUtil;
import tech.tablesaw.util.selection.Selection;

import java.util.List;

import static org.junit.Assert.*;

public class StringColumnTest {

    private StringColumn column = new StringColumn("testing");

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
    public void testDefaultReturnValue() {
        assertEquals(-1, column.dictionaryMap().get("test"));
    }

    @Test
    public void testType() {
        assertEquals(ColumnType.CATEGORY, column.type());
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
        assertEquals("Category column: testing", column.toString());
    }

    @Test
    public void testMax() {
        StringColumn stringColumn = new StringColumn("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        assertTrue("Wyoming".equals(stringColumn.top(5).get(0)));
    }

    @Test
    public void testMin() {
        StringColumn stringColumn = new StringColumn("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        assertTrue("Alabama".equals(stringColumn.bottom(5).get(0)));
    }

    @Test
    public void testStartsWith() {
        StringColumn stringColumn = new StringColumn("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        Selection selection = stringColumn.startsWith("A");
        assertEquals("Alabama", stringColumn.get(selection.get(0)));
        assertEquals("Alaska", stringColumn.get(selection.get(1)));
        assertEquals("Arizona", stringColumn.get(selection.get(2)));
        assertEquals("Arkansas", stringColumn.get(selection.get(3)));

        selection = stringColumn.startsWith("T");
        assertEquals("Tennessee", stringColumn.get(selection.get(0)));
        assertEquals("Texas", stringColumn.get(selection.get(1)));
    }

    @Test
    public void testIsNotEqualTo() {
        StringColumn stringColumn = new StringColumn("US States");
        stringColumn.addAll(TestDataUtil.usStates());

        Selection selection = stringColumn.isNotEqualTo("Alabama");
        StringColumn result = (StringColumn) stringColumn.subset(selection);
        assertEquals(result.size(), stringColumn.size() - 1);
        assertFalse(result.contains("Alabama"));
        assertEquals(stringColumn.size(), 51);
    }

    @Test
    public void testIsNotEqualTo2() {
        StringColumn stringColumn = new StringColumn("US States");
        stringColumn.addAll(TestDataUtil.usStates());

        Selection selection2 = stringColumn.isNotEqualTo("Yugoslavia");
        assertEquals(selection2.size(), 51);
        StringColumn result2 = (StringColumn) stringColumn.subset(selection2);
        assertEquals(result2.size(), stringColumn.size());
    }

    @Test
    public void testIsIn() {
        StringColumn stringColumn = new StringColumn("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        Selection selection = stringColumn.isIn("Alabama", "Texas");
        assertEquals("Alabama", stringColumn.get(selection.get(0)));
        assertEquals("Texas", stringColumn.get(selection.get(1)));
        assertEquals(2, selection.size());
    }

    @Test
    public void testToList() {
        StringColumn stringColumn = new StringColumn("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        List<String> states = stringColumn.asList();
        assertEquals(51, states.size()); //includes Wash. DC
    }
}