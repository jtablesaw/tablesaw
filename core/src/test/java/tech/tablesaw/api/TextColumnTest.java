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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.tablesaw.columns.strings.StringPredicates.isEqualToIgnoringCase;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.tablesaw.TestDataUtil;
import tech.tablesaw.columns.strings.StringColumnFormatter;
import tech.tablesaw.selection.Selection;

public class TextColumnTest {

    private final TextColumn column = TextColumn.create("testing");

    @BeforeEach
    public void setUp() {
        column.append("Value 1");
        column.append("Value 2");
        column.append("Value 3");
        column.append("Value 4");
    }

    @Test
    public void testAppendObj() {
        TextColumn column = TextColumn.create("testing");
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
        TextColumn c1 = column.lag(1);
        Table t = Table.create("Test");
        t.addColumns(column, c1);
        assertEquals("", c1.get(0));
        assertEquals("Value 1", c1.get(1));
        assertEquals("Value 2", c1.get(2));
    }

    @Test
    public void lag2() {
        TextColumn c1 = column.lag(-1);
        Table t = Table.create("Test");
        t.addColumns(column, c1);
        assertEquals("Value 2", c1.get(0));
        assertEquals("Value 3", c1.get(1));
        assertEquals("", c1.get(3));
    }

    @Test
    public void lead() {
        TextColumn c1 = column.lead(1);
        Table t = Table.create("Test");
        t.addColumns(column, c1);
        assertEquals("Value 2", c1.get(0));
        assertEquals("Value 3", c1.get(1));
        assertEquals("", c1.get(3));
    }

    @Test
    public void testSelectWhere() {
        TextColumn result = column.where(column.equalsIgnoreCase("VALUE 1"));
        assertEquals(1, result.size());
    }

    @Test
    public void testDefaultReturnValue() {
        assertEquals(-1, column.firstIndexOf("test"));
    }

    @Test
    public void testType() {
        assertEquals(ColumnType.TEXT, column.type());
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
    public void testToString() {
        assertEquals("Text column: testing", column.toString());
    }

    @Test
    public void testMax() {
        TextColumn stringColumn = TextColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        assertEquals("Wyoming", stringColumn.top(5).get(0));
    }

    @Test
    public void testMin() {
        TextColumn stringColumn = TextColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        assertEquals("Alabama", stringColumn.bottom(5).get(0));
    }

    @Test
    public void testStartsWith() {
        TextColumn stringColumn = TextColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());

        TextColumn selection = stringColumn.where(stringColumn.startsWith("A"));
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
        TextColumn stringColumn = TextColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());

        Function<String, String> formatter = s -> String.format("[[%s]]", s);

        stringColumn.setPrintFormatter(new StringColumnFormatter(formatter));
        assertEquals("[[Alabama]]", stringColumn.getString(0));
    }

    @Test
    public void testSelectWithFilter() {
        TextColumn stringColumn = TextColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());

        TextColumn selection = stringColumn.where(
                stringColumn.startsWith("A")
                    .and(stringColumn.containsString("kan")));

        assertEquals(1, selection.size());
        assertEquals("Arkansas", selection.getString(0));
    }

    @Test
    public void testIsNotEqualTo() {
        TextColumn stringColumn = TextColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());

        Selection selection = stringColumn.isNotEqualTo("Alabama");
        TextColumn result = stringColumn.where(selection);
        assertEquals(result.size(), stringColumn.size() - 1);
        assertFalse(result.contains("Alabama"));
        assertEquals(stringColumn.size(), 51);
    }

    @Test
    public void testColumnEqualIgnoringCase() {
        TextColumn other = column.copy();
        other.set(1, "Some other thing");
        other.set(2, other.get(2).toUpperCase());
        assertFalse(other.contains("Value 3"));
        assertTrue(other.contains("Value 1"));
        assertFalse(other.contains("Value 2"));
        assertTrue(other.contains("Some other thing"));
        assertTrue(other.contains("VALUE 3"));
        assertTrue(other.contains("Value 4"));
        assertEquals(4, other.size());
        TextColumn result = column.where(column.eval(isEqualToIgnoringCase, other));
        assertEquals(3, result.size());
    }

    @Test
    public void testIsEqualTo() {
        TextColumn stringColumn = TextColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        stringColumn.append("Alabama");  // so we have two entries
        Selection selection = stringColumn.isEqualTo("Alabama");
        TextColumn result = stringColumn.where(selection);

        assertEquals(2, result.size());
        assertTrue(result.contains("Alabama"));

        Selection result2 = stringColumn.isEqualTo("Alabama");
        assertEquals(2, result2.size());
        stringColumn = stringColumn.where(result2);
        assertTrue(stringColumn.contains("Alabama"));
    }

    @Test
    public void testIsNotEqualTo2() {
        TextColumn stringColumn = TextColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());

        Selection selection2 = stringColumn.isNotEqualTo("Yugoslavia");
        assertEquals(selection2.size(), 51);
        TextColumn result2 = stringColumn.where(selection2);
        assertEquals(result2.size(), stringColumn.size());
    }

    @Test
    public void testIsIn() {
        TextColumn stringColumn = TextColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        TextColumn selection = stringColumn.where(stringColumn.isIn("Alabama", "Texas"));
        assertEquals("Alabama", selection.get(0));
        assertEquals("Texas", selection.get(1));
        assertEquals(2, selection.size());
    }

    @Test
    public void testIsNotIn() {
        TextColumn stringColumn = TextColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        TextColumn selection = stringColumn.where(stringColumn.isNotIn("Alabama", "Texas"));
        assertEquals("Alaska", selection.get(0));
        assertEquals("Arizona", selection.get(1));
        assertEquals("Arkansas", selection.get(2));
        assertEquals(49, selection.size());
    }

    @Test
    public void testToList() {
        TextColumn stringColumn = TextColumn.create("US States");
        stringColumn.addAll(TestDataUtil.usStates());
        List<String> states = stringColumn.asList();
        assertEquals(51, states.size()); //includes Wash. DC
    }

    @Test
    public void testFormatting() {
        String[] names = {"John White", "George Victor"};
        TextColumn nameColumn = TextColumn.create("names", names);
        StringColumn formatted = nameColumn.format("Name: %s");
        assertEquals("Name: John White", formatted.get(0));
    }

    @Test
    public void testDistance() {
        String[] words = {"canary", "banana", "island", "reggae"};
        String[] words2 = {"cancel", "bananas", "islander", "calypso"};
        TextColumn wordColumn = TextColumn.create("words", words);
        TextColumn word2Column = TextColumn.create("words2", words2);
        DoubleColumn distance = wordColumn.distance(word2Column);
        assertEquals(distance.get(0), 3, 0.0001);
        assertEquals(distance.get(3), 7, 0.0001);
    }

    @Test
    public void testCommonSuffix() {
        String[] words = {"running", "icecube", "regular", "reggae"};
        String[] words2 = {"rowing", "cube", "premium", "place"};
        TextColumn wordColumn = TextColumn.create("words", words);
        TextColumn word2Column = TextColumn.create("words2", words2);
        StringColumn suffix = wordColumn.commonSuffix(word2Column);
        assertEquals(suffix.get(0), "ing");
        assertEquals(suffix.get(1), "cube");
        assertEquals(suffix.get(3), "e");
    }

    @Test
    public void testCommonPrefix() {
        String[] words = {"running", "icecube", "back"};
        String[] words2 = {"rowing", "iceland", "backup"};
        TextColumn wordColumn = TextColumn.create("words", words);
        TextColumn word2Column = TextColumn.create("words2", words2);
        StringColumn result = wordColumn.commonPrefix(word2Column);
        assertEquals(result.get(0), "r");
        assertEquals(result.get(1), "ice");
        assertEquals(result.get(2), "back");
    }

    @Test
    public void testPadStart() {
        String[] words = {"running", "icecube", "back"};
        TextColumn wordColumn = TextColumn.create("words", words);
        StringColumn result = wordColumn.padStart(8, ' ');
        assertEquals(result.get(0), " running");
        assertEquals(result.get(1), " icecube");
        assertEquals(result.get(2), "    back");
    }

    @Test
    public void testPadEnd() {
        String[] words = {"running", "icecube", "back"};
        TextColumn wordColumn = TextColumn.create("words", words);
        StringColumn result = wordColumn.padEnd(8, 'X');
        assertEquals(result.get(0), "runningX");
        assertEquals(result.get(1), "icecubeX");
        assertEquals(result.get(2), "backXXXX");
    }

    @Test
    public void testSubstring() {
        String[] words = {"running", "icecube", "back"};
        TextColumn wordColumn = TextColumn.create("words", words);
        StringColumn result = wordColumn.substring(3);
        assertEquals(result.get(0), "ning");
        assertEquals(result.get(1), "cube");
        assertEquals(result.get(2), "k");
    }

    @Test
    public void testSubstring2() {
        String[] words = {"running", "icecube", "back"};
        TextColumn wordColumn = TextColumn.create("words", words);
        StringColumn result = wordColumn.substring(1,3);
        assertEquals(result.get(0), "un");
        assertEquals(result.get(1), "ce");
        assertEquals(result.get(2), "ac");
    }

    @Test
    public void testReplaceFirst() {
        String[] words = {"running", "run run run"};
        TextColumn wordColumn = TextColumn.create("words", words);
        StringColumn result = wordColumn.replaceFirst("run","walk");
        assertEquals(result.get(0), "walkning");
        assertEquals(result.get(1), "walk run run");
    }

    @Test
    public void testReplaceAll() {
        String[] words = {"running", "run run run"};
        TextColumn wordColumn = TextColumn.create("words", words);
        StringColumn result = wordColumn.replaceAll("run","walk");
        assertEquals(result.get(0), "walkning");
        assertEquals(result.get(1), "walk walk walk");
    }

    @Test
    public void testReplaceAll2() {
        String[] words = {"running", "run run run"};
        String[] regex = {"n", "g"};
        TextColumn wordColumn = TextColumn.create("words", words);
        StringColumn result = wordColumn.replaceAll(regex,"XX");
        assertEquals(result.get(0), "ruXXXXiXXXX");
        assertEquals(result.get(1), "ruXX ruXX ruXX");
    }

    @Test
    public void testJoin() {
        String[] words = {"running", "run"};
        String[] words2 = {"walking", "walk"};
        String[] words3 = {"swimming", "swim"};
        TextColumn wordColumn = TextColumn.create("words", words);
        TextColumn wordColumn2 = TextColumn.create("words2", words2);
        TextColumn wordColumn3 = TextColumn.create("words3", words3);
        StringColumn result = wordColumn.join("--", wordColumn2, wordColumn3);
        assertEquals(result.get(0), "running--walking--swimming");
        assertEquals(result.get(1), "run--walk--swim");
    }

    @Test
    public void testTrim() {
        String[] words = {" running ", " run run run "};
        TextColumn wordColumn = TextColumn.create("words", words);
        StringColumn result = wordColumn.trim();
        assertEquals(result.get(0), "running");
        assertEquals(result.get(1), "run run run");
    }

    @Test
    public void testUpperCase() {
        String[] words = {"running", "run run run"};
        TextColumn wordColumn = TextColumn.create("words", words);
        StringColumn result = wordColumn.upperCase();
        assertEquals(result.get(0), "RUNNING");
        assertEquals(result.get(1), "RUN RUN RUN");
    }

    @Test
    public void testLowerCase() {
        String[] words = {"RUNNING", "RUN RUN RUN"};
        TextColumn wordColumn = TextColumn.create("words", words);
        StringColumn result = wordColumn.lowerCase();
        assertEquals(result.get(0), "running");
        assertEquals(result.get(1), "run run run");
    }

    @Test
    public void testAbbreviate() {
        String[] words = {"running", "Stop Breaking Down", "Backwards Writing"};
        TextColumn wordColumn = TextColumn.create("words", words);
        StringColumn result = wordColumn.abbreviate(10);
        assertEquals(result.get(0), "running");
        assertEquals(result.get(1), "Stop Br...");
        assertEquals(result.get(2), "Backwar...");
    }

    @Test
    public void tokenizeAndSort() {
        String[] words = {"Stop Breaking Down", "Backwards Writing"};
        TextColumn wordColumn = TextColumn.create("words", words);
        StringColumn result = wordColumn.tokenizeAndSort();
        assertEquals(result.get(0), "Breaking Down Stop");
        assertEquals(result.get(1), "Backwards Writing");
    }

    @Test
    public void tokenizeAndSort1() {
        String[] words = {"Stop,Breaking,Down", "Writing Backwards"};
        TextColumn wordColumn = TextColumn.create("words", words);
        StringColumn result = wordColumn.tokenizeAndSort(",");
        assertEquals(result.get(0), "Breaking,Down,Stop");
        assertEquals(result.get(1), "Writing Backwards");
    }

    @Test
    public void tokenizeAndRemoveDuplicates() {
        String[] words = {"Stop Breaking Stop Down", "walk run run"};
        TextColumn wordColumn = TextColumn.create("words", words);
        StringColumn result = wordColumn.tokenizeAndRemoveDuplicates(" ");
        assertEquals("Stop Breaking Down", result.get(0));
        assertEquals("walk run", result.get(1));
    }

    @Test
    public void chainMaps() {
        String[] words = {"Stop Breaking Stop Down", "walk run run"};
        TextColumn wordColumn = TextColumn.create("words", words);
        StringColumn result = wordColumn.tokenizeAndRemoveDuplicates(" ").tokenizeAndSort();
        assertEquals("Breaking Down Stop", result.get(0));
        assertEquals("run walk", result.get(1));
    }

    @Test
    public void chainMaps1() {
        String[] words = {"foo", "bar"};
        TextColumn wordColumn = TextColumn.create("words", words);
        StringColumn result = wordColumn.concatenate(" bam");
        assertEquals("foo bam", result.get(0));
        assertEquals("bar bam", result.get(1));
    }
}