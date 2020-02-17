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

    TextColumn selection =
        stringColumn.where(stringColumn.startsWith("A").and(stringColumn.containsString("kan")));

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
    assertEquals(51, stringColumn.size());
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
    stringColumn.append("Alabama"); // so we have two entries
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
    assertEquals(51, selection2.size());
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
    assertEquals(51, states.size()); // includes Wash. DC
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
    assertEquals(3, distance.get(0), 0.0001);
    assertEquals(7, distance.get(3), 0.0001);
  }

  @Test
  public void testCommonSuffix() {
    String[] words = {"running", "icecube", "regular", "reggae"};
    String[] words2 = {"rowing", "cube", "premium", "place"};
    TextColumn wordColumn = TextColumn.create("words", words);
    TextColumn word2Column = TextColumn.create("words2", words2);
    StringColumn suffix = wordColumn.commonSuffix(word2Column);
    assertEquals("ing", suffix.get(0));
    assertEquals("cube", suffix.get(1));
    assertEquals("e", suffix.get(3));
  }

  @Test
  public void testCommonPrefix() {
    String[] words = {"running", "icecube", "back"};
    String[] words2 = {"rowing", "iceland", "backup"};
    TextColumn wordColumn = TextColumn.create("words", words);
    TextColumn word2Column = TextColumn.create("words2", words2);
    StringColumn result = wordColumn.commonPrefix(word2Column);
    assertEquals("r", result.get(0));
    assertEquals("ice", result.get(1));
    assertEquals("back", result.get(2));
  }

  @Test
  public void testPadStart() {
    String[] words = {"running", "icecube", "back"};
    TextColumn wordColumn = TextColumn.create("words", words);
    StringColumn result = wordColumn.padStart(8, ' ');
    assertEquals(" running", result.get(0));
    assertEquals(" icecube", result.get(1));
    assertEquals("    back", result.get(2));
  }

  @Test
  public void testPadEnd() {
    String[] words = {"running", "icecube", "back"};
    TextColumn wordColumn = TextColumn.create("words", words);
    StringColumn result = wordColumn.padEnd(8, 'X');
    assertEquals("runningX", result.get(0));
    assertEquals("icecubeX", result.get(1));
    assertEquals("backXXXX", result.get(2));
  }

  @Test
  public void testSubstring() {
    String[] words = {"running", "icecube", "back"};
    TextColumn wordColumn = TextColumn.create("words", words);
    StringColumn result = wordColumn.substring(3);
    assertEquals("ning", result.get(0));
    assertEquals("cube", result.get(1));
    assertEquals("k", result.get(2));
  }

  @Test
  public void testSubstring2() {
    String[] words = {"running", "icecube", "back"};
    TextColumn wordColumn = TextColumn.create("words", words);
    StringColumn result = wordColumn.substring(1, 3);
    assertEquals("un", result.get(0));
    assertEquals("ce", result.get(1));
    assertEquals("ac", result.get(2));
  }

  @Test
  public void testReplaceFirst() {
    String[] words = {"running", "run run run"};
    TextColumn wordColumn = TextColumn.create("words", words);
    StringColumn result = wordColumn.replaceFirst("run", "walk");
    assertEquals("walkning", result.get(0));
    assertEquals("walk run run", result.get(1));
  }

  @Test
  public void testReplaceAll() {
    String[] words = {"running", "run run run"};
    TextColumn wordColumn = TextColumn.create("words", words);
    StringColumn result = wordColumn.replaceAll("run", "walk");
    assertEquals("walkning", result.get(0));
    assertEquals("walk walk walk", result.get(1));
  }

  @Test
  public void testReplaceAll2() {
    String[] words = {"running", "run run run"};
    String[] regex = {"n", "g"};
    TextColumn wordColumn = TextColumn.create("words", words);
    StringColumn result = wordColumn.replaceAll(regex, "XX");
    assertEquals("ruXXXXiXXXX", result.get(0));
    assertEquals("ruXX ruXX ruXX", result.get(1));
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
    assertEquals("running--walking--swimming", result.get(0));
    assertEquals("run--walk--swim", result.get(1));
  }

  @Test
  public void testAsStringColumn() throws Exception {
    Table table = Table.read().csv("../data/first_names.csv");
    StringColumn name = table.stringColumn("emma");
    TextColumn name2 = name.asTextColumn();
    StringColumn name3 = name2.asStringColumn();
    for (int i = 0; i < table.rowCount(); i++) {
      assertEquals(name.get(i), name3.get(i));
    }
  }

  @Test
  public void testTrim() {
    String[] words = {" running ", " run run run "};
    TextColumn wordColumn = TextColumn.create("words", words);
    StringColumn result = wordColumn.trim();
    assertEquals("running", result.get(0));
    assertEquals("run run run", result.get(1));
  }

  @Test
  public void testUpperCase() {
    String[] words = {"running", "run run run"};
    TextColumn wordColumn = TextColumn.create("words", words);
    StringColumn result = wordColumn.upperCase();
    assertEquals("RUNNING", result.get(0));
    assertEquals("RUN RUN RUN", result.get(1));
  }

  @Test
  public void testLowerCase() {
    String[] words = {"RUNNING", "RUN RUN RUN"};
    TextColumn wordColumn = TextColumn.create("words", words);
    StringColumn result = wordColumn.lowerCase();
    assertEquals("running", result.get(0));
    assertEquals("run run run", result.get(1));
  }

  @Test
  public void testAbbreviate() {
    String[] words = {"running", "Stop Breaking Down", "Backwards Writing"};
    TextColumn wordColumn = TextColumn.create("words", words);
    StringColumn result = wordColumn.abbreviate(10);
    assertEquals("running", result.get(0));
    assertEquals("Stop Br...", result.get(1));
    assertEquals("Backwar...", result.get(2));
  }

  @Test
  public void tokenizeAndSort() {
    String[] words = {"Stop Breaking Down", "Backwards Writing"};
    TextColumn wordColumn = TextColumn.create("words", words);
    StringColumn result = wordColumn.tokenizeAndSort();
    assertEquals("Breaking Down Stop", result.get(0));
    assertEquals("Backwards Writing", result.get(1));
  }

  @Test
  void testSort() throws Exception {
    Table t = Table.read().csv("../data/bush.csv");
    TextColumn whoText = t.stringColumn("who").asTextColumn();
    whoText.setName("who text");
    t.addColumns(whoText);
    Table t2 = t.copy();
    t.sortAscendingOn("who text");
    t2.sortAscendingOn("who");
    for (int i = 0; i < t.rowCount(); i++) {
      assertEquals(t.row(i).getString("who text"), t2.row(i).getString("who"));
    }
  }

  @Test
  public void tokenizeAndSort1() {
    String[] words = {"Stop,Breaking,Down", "Writing Backwards"};
    TextColumn wordColumn = TextColumn.create("words", words);
    StringColumn result = wordColumn.tokenizeAndSort(",");
    assertEquals("Breaking,Down,Stop", result.get(0));
    assertEquals("Writing Backwards", result.get(1));
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

  @Test
  public void testCountUnique() {
    TextColumn col1 = TextColumn.create("col1");
    col1.append("1");
    col1.append("1");
    col1.append("2");
    col1.appendMissing();

    assertEquals(3, col1.countUnique());
    assertEquals(3, col1.unique().size());
  }
}
