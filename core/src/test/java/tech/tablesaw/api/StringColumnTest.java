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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.tablesaw.columns.strings.StringPredicates.isEqualToIgnoringCase;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.tablesaw.TestDataUtil;
import tech.tablesaw.columns.strings.StringColumnFormatter;
import tech.tablesaw.selection.Selection;

public class StringColumnTest {

    private final StringColumn column = StringColumn.create("testing");

    @BeforeEach
    public void setUp() {
        column.append("Value 1");
        column.append("Value 2");
        column.append("Value 3");
        column.append("Value 4");
    }

/*
TODO: fix
    @Test
    public void testSummarizeIf() {
        double result = column.summarizeIf(
                column.endsWith("3").or(column.endsWith("4")),
                count);
        assertEquals(2, result, 0.0);

        double result2 = column.summarizeIf(column.endsWith("3"), count);
        assertEquals(1, result2, 0.0);
    }
*/

    @Test
    public void testAppendObj2() {
        final StringColumn sc = StringColumn.create("sc", Arrays.asList("a", "b", "c", "a"));
        assertArrayEquals(sc.asList().toArray(), sc.asObjectArray());
    }

    @Test
    public void testForNulls() {
        String[] array1 = {"1", "2", "3", "4", null};
        Table table1 = Table.create("table1", StringColumn.create("id", array1));
        assertEquals("", table1.stringColumn("id").get(4));

        String[] array2 = {"1", "2", null, "", "5"};
        Table table2 = Table.create("table2", StringColumn.create("id", array2));
        assertEquals("", table2.stringColumn("id").get(3));
    }

    @Test
    public void testAppendObj() {
        StringColumn column = StringColumn.create("testing");
        column.appendObj("Value 1");
        column.appendObj(null);
        column.appendObj("Value 2");
        assertEquals(3, column.size());
    }
    
    @Test
    public void testConditionalSet() {
        column.set(column.isEqualTo("Value 4"), "no Value");
        assertTrue(column.contains("no Value"));
        assertFalse(column.contains("Value 4"));
    }

    @Test
    public void lag() {
        StringColumn c1 = column.lag(1);
        Table t = Table.create("Test");
        t.addColumns(column, c1);
        assertEquals("", c1.get(0));
        assertEquals("Value 1", c1.get(1));
        assertEquals("Value 2", c1.get(2));
    }

    @Test
    public void lag2() {
        StringColumn c1 = column.lag(-1);
        Table t = Table.create("Test");
        t.addColumns(column, c1);
        assertEquals("Value 2", c1.get(0));
        assertEquals("Value 3", c1.get(1));
        assertEquals("", c1.get(3));
    }

    @Test
    public void lead() {
        StringColumn c1 = column.lead(1);
        Table t = Table.create("Test");
        t.addColumns(column, c1);
        assertEquals("Value 2", c1.get(0));
        assertEquals("Value 3", c1.get(1));
        assertEquals("", c1.get(3));
    }

    @Test
    public void testSelectWhere() {
        StringColumn result = column.where(column.equalsIgnoreCase("VALUE 1"));
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
        assertEquals("Wyoming", stringColumn.top(5).get(0));
    }

    @Test
    public void testMin() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        assertEquals("Alabama", stringColumn.bottom(5).get(0));
    }

    @Test
    public void testStartsWith() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());

        StringColumn selection = stringColumn.where(stringColumn.startsWith("A"));
        assertEquals("Alabama", selection.get(0));
        assertEquals("Alaska", selection.get(1));
        assertEquals("Arizona", selection.get(2));
        assertEquals("Arkansas", selection.get(3));

        selection = stringColumn.where(stringColumn.startsWith("T"));
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

        StringColumn selection = stringColumn.where(
                stringColumn.startsWith("A")
                    .and(stringColumn.containsString("kan")));

        assertEquals(1, selection.size());
        assertEquals("Arkansas", selection.getString(0));
    }

    @Test
    public void testIsNotEqualTo() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());

        Selection selection = stringColumn.isNotEqualTo("Alabama");
        StringColumn result = stringColumn.where(selection);
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
        assertEquals(4, other.size());
        StringColumn result = column.where(column.eval(isEqualToIgnoringCase, other));
        assertEquals(3, result.size());
    }

    @Test
    public void testIsEqualTo() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        stringColumn.append("Alabama");  // so we have two entries
        Selection selection = stringColumn.isEqualTo("Alabama");
        StringColumn result = stringColumn.where(selection);

        assertEquals(2, result.size());
        assertTrue(result.contains("Alabama"));

        Selection result2 = stringColumn.isEqualTo("Alabama");
        assertEquals(2, result2.size());
        stringColumn = stringColumn.where(result2);
        assertTrue(stringColumn.contains("Alabama"));
    }

    @Test
    public void testIsNotEqualTo2() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());

        Selection selection2 = stringColumn.isNotEqualTo("Yugoslavia");
        assertEquals(selection2.size(), 51);
        StringColumn result2 = stringColumn.where(selection2);
        assertEquals(result2.size(), stringColumn.size());
    }

    @Test
    public void testIsIn() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        StringColumn selection = stringColumn.where(stringColumn.isIn("Alabama", "Texas"));
        assertEquals("Alabama", selection.get(0));
        assertEquals("Texas", selection.get(1));
        assertEquals(2, selection.size());
    }

    @Test
    public void testIsNotIn() {
        StringColumn stringColumn = StringColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        StringColumn selection = stringColumn.where(stringColumn.isNotIn("Alabama", "Texas"));
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

    @Test
    public void testFormatting() {
        String[] names = {"John White", "George Victor"};
        StringColumn nameColumn = StringColumn.create("names", names);
        StringColumn formatted = nameColumn.format("Name: %s");
        assertEquals("Name: John White", formatted.get(0));
    }

    @Test
    public void testDistance() {
        String[] words = {"canary", "banana", "island", "reggae"};
        String[] words2 = {"cancel", "bananas", "islander", "calypso"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn word2Column = StringColumn.create("words2", words2);
        DoubleColumn distance = wordColumn.distance(word2Column);
        assertEquals(distance.get(0), 3, 0.0001);
        assertEquals(distance.get(3), 7, 0.0001);
    }

    @Test
    public void testCommonSuffix() {
        String[] words = {"running", "icecube", "regular", "reggae"};
        String[] words2 = {"rowing", "cube", "premium", "place"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn word2Column = StringColumn.create("words2", words2);
        StringColumn suffix = wordColumn.commonSuffix(word2Column);
        assertEquals(suffix.get(0), "ing");
        assertEquals(suffix.get(1), "cube");
        assertEquals(suffix.get(3), "e");
    }

    @Test
    public void testCommonPrefix() {
        String[] words = {"running", "icecube", "back"};
        String[] words2 = {"rowing", "iceland", "backup"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn word2Column = StringColumn.create("words2", words2);
        StringColumn result = wordColumn.commonPrefix(word2Column);
        assertEquals(result.get(0), "r");
        assertEquals(result.get(1), "ice");
        assertEquals(result.get(2), "back");
    }

    @Test
    public void testPadStart() {
        String[] words = {"running", "icecube", "back"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.padStart(8, ' ');
        assertEquals(result.get(0), " running");
        assertEquals(result.get(1), " icecube");
        assertEquals(result.get(2), "    back");
    }

    @Test
    public void testPadEnd() {
        String[] words = {"running", "icecube", "back"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.padEnd(8, 'X');
        assertEquals(result.get(0), "runningX");
        assertEquals(result.get(1), "icecubeX");
        assertEquals(result.get(2), "backXXXX");
    }

    @Test
    public void testSubstring() {
        String[] words = {"running", "icecube", "back"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.substring(3);
        assertEquals(result.get(0), "ning");
        assertEquals(result.get(1), "cube");
        assertEquals(result.get(2), "k");
    }

    @Test
    public void testSubstring2() {
        String[] words = {"running", "icecube", "back"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.substring(1,3);
        assertEquals(result.get(0), "un");
        assertEquals(result.get(1), "ce");
        assertEquals(result.get(2), "ac");
    }

    @Test
    public void testReplaceFirst() {
        String[] words = {"running", "run run run"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.replaceFirst("run","walk");
        assertEquals(result.get(0), "walkning");
        assertEquals(result.get(1), "walk run run");
    }

    @Test
    public void testReplaceAll() {
        String[] words = {"running", "run run run"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.replaceAll("run","walk");
        assertEquals(result.get(0), "walkning");
        assertEquals(result.get(1), "walk walk walk");
    }

    @Test
    public void testReplaceAll2() {
        String[] words = {"running", "run run run"};
        String[] regex = {"n", "g"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.replaceAll(regex,"XX");
        assertEquals(result.get(0), "ruXXXXiXXXX");
        assertEquals(result.get(1), "ruXX ruXX ruXX");
    }

    @Test
    public void testJoin() {
        String[] words = {"running", "run"};
        String[] words2 = {"walking", "walk"};
        String[] words3 = {"swimming", "swim"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn wordColumn2 = StringColumn.create("words2", words2);
        StringColumn wordColumn3 = StringColumn.create("words3", words3);
        StringColumn result = wordColumn.join("--", wordColumn2, wordColumn3);
        assertEquals(result.get(0), "running--walking--swimming");
        assertEquals(result.get(1), "run--walk--swim");
    }

    @Test
    public void testTrim() {
        String[] words = {" running ", " run run run "};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.trim();
        assertEquals(result.get(0), "running");
        assertEquals(result.get(1), "run run run");
    }

    @Test
    public void testUpperCase() {
        String[] words = {"running", "run run run"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.upperCase();
        assertEquals(result.get(0), "RUNNING");
        assertEquals(result.get(1), "RUN RUN RUN");
    }

    @Test
    public void testLowerCase() {
        String[] words = {"RUNNING", "RUN RUN RUN"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.lowerCase();
        assertEquals(result.get(0), "running");
        assertEquals(result.get(1), "run run run");
    }

    @Test
    public void testAbbreviate() {
        String[] words = {"running", "Stop Breaking Down", "Backwards Writing"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.abbreviate(10);
        assertEquals(result.get(0), "running");
        assertEquals(result.get(1), "Stop Br...");
        assertEquals(result.get(2), "Backwar...");
    }

    @Test
    public void tokenizeAndSort() {
        String[] words = {"Stop Breaking Down", "Backwards Writing"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.tokenizeAndSort();
        assertEquals(result.get(0), "Breaking Down Stop");
        assertEquals(result.get(1), "Backwards Writing");
    }

    @Test
    public void tokenizeAndSort1() {
        String[] words = {"Stop,Breaking,Down", "Writing Backwards"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.tokenizeAndSort(",");
        assertEquals(result.get(0), "Breaking,Down,Stop");
        assertEquals(result.get(1), "Writing Backwards");
    }

    @Test
    public void tokenizeAndRemoveDuplicates() {
        String[] words = {"Stop Breaking Stop Down", "walk run run"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.tokenizeAndRemoveDuplicates(" ");
        assertEquals("Stop Breaking Down", result.get(0));
        assertEquals("walk run", result.get(1));
    }

    @Test
    public void chainMaps() {
        String[] words = {"Stop Breaking Stop Down", "walk run run"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.tokenizeAndRemoveDuplicates(" ").tokenizeAndSort();
        assertEquals("Breaking Down Stop", result.get(0));
        assertEquals("run walk", result.get(1));
    }

    @Test
    public void chainMaps1() {
        String[] words = {"foo", "bar"};
        StringColumn wordColumn = StringColumn.create("words", words);
        StringColumn result = wordColumn.concatenate(" bam");
        assertEquals("foo bam", result.get(0));
        assertEquals("bar bam", result.get(1));
    }

    @Test
    public void asDoubleColumn() {
        String[] words = {"foo", "bar", "larry", "foo", "lion", "ben", "tiger", "bar"};
        StringColumn wordColumn = StringColumn.create("words", words);
        DoubleColumn result = wordColumn.asDoubleColumn();
        assertArrayEquals(new double[] { 0.0, 1.0, 2.0, 0.0, 3.0, 4.0, 5.0, 1.0 }, result.asDoubleArray(), 0.000_000_1);
    }

    @Test
    public void asDoubleArray() {
        String[] words = {"foo", "bar", "larry", "foo", "lion", null, "ben", "tiger", "bar"};
        StringColumn wordColumn = StringColumn.create("words", words);
        double[] result = wordColumn.asDoubleArray();
        assertArrayEquals(new double[] { 0.0, 1.0, 2.0, 0.0, 3.0, 4.0, 5.0, 6.0, 1.0 }, result, 0.000_000_1);
    }

    @Test
    public void getDouble() {
        String[] words = {"foo", "bar", "larry", "foo", "lion", null, "ben", "tiger", "bar"};
        StringColumn wordColumn = StringColumn.create("words", words);
        double[] expected = new double[] { 0.0, 1.0, 2.0, 0.0, 3.0, 4.0, 5.0, 6.0, 1.0 };
        double[] result = new double[words.length];
        for (int i = 0; i < words.length; i++) {
            result[i] = wordColumn.getDouble(i);
        }
        assertArrayEquals(expected, result, 0.000_000_1);
    }
}
