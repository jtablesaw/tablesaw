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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.tablesaw.columns.strings.StringPredicates.isEqualToIgnoringCase;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.TestDataUtil;
import tech.tablesaw.columns.strings.StringColumnFormatter;
import tech.tablesaw.columns.strings.StringParser;
import tech.tablesaw.selection.Selection;

class StringColumnTest {

  private final StringColumn column = StringColumn.create("testing");

  @BeforeEach
  void setUp() {
    column.append("Value 1");
    column.append("Value 2");
    column.append("Value 3");
    column.append("Value 4");
  }

  /*
  TODO: fix
      @Test
      void testSummarizeIf() {
          double result = column.summarizeIf(
                  column.endsWith("3").or(column.endsWith("4")),
                  count);
          assertEquals(2, result, 0.0);

          double result2 = column.summarizeIf(column.endsWith("3"), count);
          assertEquals(1, result2, 0.0);
      }
  */

  @Test
  void testAppendObj2() {
    final StringColumn sc = StringColumn.create("sc", Arrays.asList("a", "b", "c", "a"));
    assertArrayEquals(sc.asList().toArray(), sc.asObjectArray());
  }

  @Test
  void appendAsterisk() {
    final StringColumn sc = StringColumn.create("sc");
    sc.append("*");
    assertEquals(0, sc.countMissing());
    StringParser parser = new StringParser(ColumnType.STRING);
    parser.setMissingValueStrings(new ArrayList<>());
    sc.appendCell("*", parser);
    assertEquals(0, sc.countMissing());
    StringParser parser2 = new StringParser(ColumnType.STRING);
    parser2.setMissingValueStrings(Lists.newArrayList("45"));
    sc.appendCell("45", parser2);
    assertEquals(1, sc.countMissing());
  }

  @Test
  public void testCustomParser() {
    // Just do enough to ensure the parser is wired up correctly
    final StringColumn sc = StringColumn.create("sc");
    StringParser customParser = new StringParser(ColumnType.STRING);
    customParser.setMissingValueStrings(List.of("not here"));
    sc.setParser(customParser);

    sc.appendCell("not here");
    assertTrue(sc.isMissing(sc.size() - 1));
    sc.appendCell("*");
    assertFalse(sc.isMissing(sc.size() - 1));
  }

  @Test
  void testForNulls() {
    String[] array1 = {"1", "2", "3", "4", null};
    Table table1 = Table.create("table1", StringColumn.create("id", array1));
    assertEquals("", table1.stringColumn("id").get(4));

    String[] array2 = {"1", "2", null, "", "5"};
    Table table2 = Table.create("table2", StringColumn.create("id", array2));
    assertEquals("", table2.stringColumn("id").get(3));
  }

  /** Test that asSet returns a different set each time. */
  @Test
  void asSet() {
    Set<String> set1 = column.asSet();
    Set<String> set2 = column.asSet();
    set1.remove("Value 2");
    assertNotEquals(set1.size(), set2.size());
  }

  @Test
  void testAppendObj() {
    StringColumn column = StringColumn.create("testing");
    column.appendObj("Value 1");
    column.appendObj(null);
    column.appendObj("Value 2");
    assertEquals(3, column.size());
  }

  @Test
  void testConditionalSet() {
    column.set(column.isEqualTo("Value 4"), "no Value");
    assertTrue(column.contains("no Value"));
    assertFalse(column.contains("Value 4"));
  }

  @Test
  void lag() {
    StringColumn c1 = column.lag(1);
    Table t = Table.create("Test");
    t.addColumns(column, c1);
    assertEquals("", c1.get(0));
    assertEquals("Value 1", c1.get(1));
    assertEquals("Value 2", c1.get(2));
  }

  @Test
  void lag2() {
    StringColumn c1 = column.lag(-1);
    Table t = Table.create("Test");
    t.addColumns(column, c1);
    assertEquals("Value 2", c1.get(0));
    assertEquals("Value 3", c1.get(1));
    assertEquals("", c1.get(3));
  }

  @Test
  void lead() {
    StringColumn c1 = column.lead(1);
    Table t = Table.create("Test");
    t.addColumns(column, c1);
    assertEquals("Value 2", c1.get(0));
    assertEquals("Value 3", c1.get(1));
    assertEquals("", c1.get(3));
  }

  @Test
  void testSelectWhere() {
    StringColumn result = column.where(column.equalsIgnoreCase("VALUE 1"));
    assertEquals(1, result.size());
  }

  @Test
  void testDefaultReturnValue() {
    assertEquals(-1, column.firstIndexOf("test"));
  }

  @Test
  void testType() {
    assertEquals(ColumnType.STRING, column.type());
  }

  @Test
  void testGetString() {
    assertEquals("Value 2", column.getString(1));
  }

  @Test
  void testSize() {
    assertEquals(4, column.size());
  }

  @Test
  void testGetDummies() {
    List<BooleanColumn> dummies = column.getDummies();
    assertEquals(4, dummies.size());
  }

  @Test
  void testToString() {
    assertEquals("String column: testing", column.toString());
  }

  @Test
  void testMax() {
    StringColumn stringColumn = StringColumn.create("US States");
    stringColumn.addAll(TestDataUtil.usStates());
    assertEquals("Wyoming", stringColumn.top(5).get(0));
  }

  @Test
  void testMin() {
    StringColumn stringColumn = StringColumn.create("US States");
    stringColumn.addAll(TestDataUtil.usStates());
    assertEquals("Alabama", stringColumn.bottom(5).get(0));
  }

  @Test
  void testStartsWith() {
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
  void testFormattedPrinting() {
    StringColumn stringColumn = StringColumn.create("US States");
    stringColumn.addAll(TestDataUtil.usStates());

    Function<String, String> formatter = s -> String.format("[[%s]]", s);

    stringColumn.setPrintFormatter(new StringColumnFormatter(formatter));
    assertEquals("[[Alabama]]", stringColumn.getString(0));
  }

  @Test
  void testSelectWithFilter() {
    StringColumn stringColumn = StringColumn.create("US States");
    stringColumn.addAll(TestDataUtil.usStates());

    StringColumn selection =
        stringColumn.where(stringColumn.startsWith("A").and(stringColumn.containsString("kan")));

    assertEquals(1, selection.size());
    assertEquals("Arkansas", selection.getString(0));
  }

  @Test
  void testIsNotEqualTo() {
    StringColumn stringColumn = StringColumn.create("US States");
    stringColumn.addAll(TestDataUtil.usStates());

    Selection selection = stringColumn.isNotEqualTo("Alabama");
    StringColumn result = stringColumn.where(selection);
    assertEquals(result.size(), stringColumn.size() - 1);
    assertFalse(result.contains("Alabama"));
    assertEquals(51, stringColumn.size());
  }

  @Test
  void testColumnEqualIgnoringCase() {
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
  void testIsEqualTo() {
    StringColumn stringColumn = StringColumn.create("US States");
    stringColumn.addAll(TestDataUtil.usStates());
    stringColumn.append("Alabama"); // so we have two entries
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
  void testIsNotEqualTo2() {
    StringColumn stringColumn = StringColumn.create("US States");
    stringColumn.addAll(TestDataUtil.usStates());

    Selection selection2 = stringColumn.isNotEqualTo("Yugoslavia");
    assertEquals(51, selection2.size());
    StringColumn result2 = stringColumn.where(selection2);
    assertEquals(result2.size(), stringColumn.size());
  }

  @Test
  void testIsIn() {
    StringColumn stringColumn = StringColumn.create("US States");
    stringColumn.addAll(TestDataUtil.usStates());
    StringColumn selection = stringColumn.where(stringColumn.isIn("Alabama", "Texas"));
    assertEquals("Alabama", selection.get(0));
    assertEquals("Texas", selection.get(1));
    assertEquals(2, selection.size());
  }

  @Test
  void testIsNotIn() {
    StringColumn stringColumn = StringColumn.create("US States");
    stringColumn.addAll(TestDataUtil.usStates());
    StringColumn selection = stringColumn.where(stringColumn.isNotIn("Alabama", "Texas"));
    assertEquals("Alaska", selection.get(0));
    assertEquals("Arizona", selection.get(1));
    assertEquals("Arkansas", selection.get(2));
    assertEquals(49, selection.size());
  }

  @Test
  void testToList() {
    StringColumn stringColumn = StringColumn.create("US States");
    stringColumn.addAll(TestDataUtil.usStates());
    List<String> states = stringColumn.asList();
    assertEquals(51, states.size()); // includes Wash. DC
  }

  @Test
  void testFormatting() {
    String[] names = {"John White", "George Victor"};
    StringColumn nameColumn = StringColumn.create("names", names);
    StringColumn formatted = nameColumn.format("Name: %s");
    assertEquals("Name: John White", formatted.get(0));
  }

  @Test
  void testDistance() {
    String[] words = {"canary", "banana", "island", "reggae"};
    String[] words2 = {"cancel", "bananas", "islander", "calypso"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn word2Column = StringColumn.create("words2", words2);
    DoubleColumn distance = wordColumn.distance(word2Column);
    assertEquals(3, distance.get(0), 0.0001);
    assertEquals(7, distance.get(3), 0.0001);
  }

  @Test
  void testCommonSuffix() {
    String[] words = {"running", "icecube", "regular", "reggae"};
    String[] words2 = {"rowing", "cube", "premium", "place"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn word2Column = StringColumn.create("words2", words2);
    StringColumn suffix = wordColumn.commonSuffix(word2Column);
    assertEquals("ing", suffix.get(0));
    assertEquals("cube", suffix.get(1));
    assertEquals("e", suffix.get(3));
  }

  @Test
  void testCommonPrefix() {
    String[] words = {"running", "icecube", "back"};
    String[] words2 = {"rowing", "iceland", "backup"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn word2Column = StringColumn.create("words2", words2);
    StringColumn result = wordColumn.commonPrefix(word2Column);
    assertEquals("r", result.get(0));
    assertEquals("ice", result.get(1));
    assertEquals("back", result.get(2));
  }

  @Test
  void testPadStart() {
    String[] words = {"running", "icecube", "back"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.padStart(8, ' ');
    assertEquals(" running", result.get(0));
    assertEquals(" icecube", result.get(1));
    assertEquals("    back", result.get(2));
  }

  @Test
  void testPadEnd() {
    String[] words = {"running", "icecube", "back"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.padEnd(8, 'X');
    assertEquals("runningX", result.get(0));
    assertEquals("icecubeX", result.get(1));
    assertEquals("backXXXX", result.get(2));
  }

  @Test
  void testParseInt() {
    String[] values = {"4", "72", "132"};
    StringColumn wordColumn = StringColumn.create("values", values);
    IntColumn result = wordColumn.parseInt();
    assertEquals(4, result.get(0));
    assertEquals(72, result.get(1));
    assertEquals(132, result.get(2));
  }

  @Test
  void testParseDouble() {
    String[] values = {"0.4", "0.72", "1.132"};
    StringColumn wordColumn = StringColumn.create("values", values);
    DoubleColumn result = wordColumn.parseDouble();
    assertEquals(0.4, result.get(0));
    assertEquals(0.72, result.get(1));
    assertEquals(1.132, result.get(2));
  }

  @Test
  void testParseFloat() {
    String[] values = {"0.4", "0.72", "1.132"};
    StringColumn wordColumn = StringColumn.create("values", values);
    FloatColumn result = wordColumn.parseFloat();
    assertEquals(0.4f, result.get(0));
    assertEquals(0.72f, result.get(1));
    assertEquals(1.132f, result.get(2));
  }

  @Test
  void testSubstring() {
    String[] words = {"running", "icecube", "back"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.substring(3);
    assertEquals("ning", result.get(0));
    assertEquals("cube", result.get(1));
    assertEquals("k", result.get(2));
  }

  @Test
  void testRepeat() {
    String[] words = {"running", "icecube", "back"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.repeat(3);
    assertEquals("runningrunningrunning", result.get(0));
  }

  @Test
  void testCapitalize() {
    String[] words = {"running", "ice cube", "stuff4us"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.capitalize();
    assertEquals("Running", result.get(0));
    assertEquals("Ice cube", result.get(1));
    assertEquals("Stuff4us", result.get(2));
  }

  @Test
  void testConcatinate() {
    String[] words = {"running", "icecube", "back"};
    StringColumn wordColumn1 = StringColumn.create("words1", words);
    StringColumn wordColumn2 = StringColumn.create("words2", words);
    StringColumn result = wordColumn1.concatenate(wordColumn2);
    assertEquals("runningrunning", result.get(0));
  }

  @Test
  void testSubstring2() {
    String[] words = {"running", "icecube", "back"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.substring(1, 3);
    assertEquals("un", result.get(0));
    assertEquals("ce", result.get(1));
    assertEquals("ac", result.get(2));
  }

  @Test
  void testReplaceFirst() {
    String[] words = {"running", "run run run"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.replaceFirst("run", "walk");
    assertEquals("walkning", result.get(0));
    assertEquals("walk run run", result.get(1));
  }

  @Test
  void testReplaceAll() {
    String[] words = {"running", "run run run"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.replaceAll("run", "walk");
    assertEquals("walkning", result.get(0));
    assertEquals("walk walk walk", result.get(1));
  }

  @Test
  void testReplaceAll2() {
    String[] words = {"running", "run run run"};
    String[] regex = {"n", "g"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.replaceAll(regex, "XX");
    assertEquals("ruXXXXiXXXX", result.get(0));
    assertEquals("ruXX ruXX ruXX", result.get(1));
  }

  @Test
  void testJoin() {
    String[] words = {"running", "run"};
    String[] words2 = {"walking", "walk"};
    String[] words3 = {"swimming", "swim"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn wordColumn2 = StringColumn.create("words2", words2);
    StringColumn wordColumn3 = StringColumn.create("words3", words3);
    StringColumn result = wordColumn.join("--", wordColumn2, wordColumn3);
    assertEquals("running--walking--swimming", result.get(0));
    assertEquals("run--walk--swim", result.get(1));
  }

  @Test
  void testTrim() {
    String[] words = {" running ", " run run run "};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.trim();
    assertEquals("running", result.get(0));
    assertEquals("run run run", result.get(1));
  }

  @Test
  void testUpperCase() {
    String[] words = {"running", "run run run"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.upperCase();
    assertEquals("RUNNING", result.get(0));
    assertEquals("RUN RUN RUN", result.get(1));
  }

  @Test
  void testLowerCase() {
    String[] words = {"RUNNING", "RUN RUN RUN"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.lowerCase();
    assertEquals("running", result.get(0));
    assertEquals("run run run", result.get(1));
  }

  @Test
  void testAbbreviate() {
    String[] words = {"running", "Stop Breaking Down", "Backwards Writing"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.abbreviate(10);
    assertEquals("running", result.get(0));
    assertEquals("Stop Br...", result.get(1));
    assertEquals("Backwar...", result.get(2));
  }

  @Test
  void tokenizeAndSort() {
    String[] words = {"Stop Breaking Down", "Backwards Writing"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.tokenizeAndSort();
    assertEquals("Breaking Down Stop", result.get(0));
    assertEquals("Backwards Writing", result.get(1));
  }

  @Test
  void tokenizeAndSort1() {
    String[] words = {"Stop,Breaking,Down", "Writing Backwards"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.tokenizeAndSort(",");
    assertEquals("Breaking,Down,Stop", result.get(0));
    assertEquals("Writing Backwards", result.get(1));
  }

  @Test
  void tokenizeAndRemoveDuplicates() {
    String[] words = {"Stop Breaking Stop Down", "walk run run"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.tokenizeAndRemoveDuplicates(" ");
    assertEquals("Stop Breaking Down", result.get(0));
    assertEquals("walk run", result.get(1));
  }

  @Test
  void chainMaps() {
    String[] words = {"Stop Breaking Stop Down", "walk run run"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.tokenizeAndRemoveDuplicates(" ").tokenizeAndSort();
    assertEquals("Breaking Down Stop", result.get(0));
    assertEquals("run walk", result.get(1));
  }

  @Test
  void chainMaps1() {
    String[] words = {"foo", "bar"};
    StringColumn wordColumn = StringColumn.create("words", words);
    StringColumn result = wordColumn.concatenate(" bam");
    assertEquals("foo bam", result.get(0));
    assertEquals("bar bam", result.get(1));
  }

  @Test
  void asDoubleColumn() {
    String[] words = {"foo", "bar", "larry", "foo", "lion", "ben", "tiger", "bar"};
    StringColumn wordColumn = StringColumn.create("words", words);
    DoubleColumn result = wordColumn.asDoubleColumn();
    assertArrayEquals(
        new double[] {0.0, 1.0, 2.0, 0.0, 3.0, 4.0, 5.0, 1.0}, result.asDoubleArray(), 0.000_000_1);
  }

  @Test
  void asDoubleArray() {
    String[] words = {"foo", "bar", "larry", "foo", "lion", null, "ben", "tiger", "bar"};
    StringColumn wordColumn = StringColumn.create("words", words);
    double[] result = wordColumn.asDoubleArray();
    assertArrayEquals(
        new double[] {0.0, 1.0, 2.0, 0.0, 3.0, 4.0, 5.0, 6.0, 1.0}, result, 0.000_000_1);
  }

  @Test
  void getDouble() {
    String[] words = {"foo", "bar", "larry", "foo", "lion", null, "ben", "tiger", "bar"};
    StringColumn wordColumn = StringColumn.create("words", words);
    double[] expected = new double[] {0.0, 1.0, 2.0, 0.0, 3.0, 4.0, 5.0, 6.0, 1.0};
    double[] result = new double[words.length];
    for (int i = 0; i < words.length; i++) {
      result[i] = wordColumn.getDouble(i);
    }
    assertArrayEquals(expected, result, 0.000_000_1);
  }

  @Test
  void countUniqueSetAfterCreateByteDict() {
    StringColumn col = StringColumn.create("col1", 2);
    assertEquals(1, col.countUnique(), "Wrong number of unique values after StringColumn.create");
    assertEquals(2, col.countMissing(), "Wrong number of missing values after StringColumn.create");
    col.set(0, "A");
    col.set(1, "B");
    assertEquals(2, col.countUnique(), "Wrong number of unique values after Column.set");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after Column.set");
    col.clear();
    assertEquals(0, col.countUnique(), "Wrong number of unique values after Column.clear");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after Column.clear");
  }

  @Test
  void countUniqueSetAfterAppendByteDict() {
    StringColumn col = StringColumn.create("col1");
    assertEquals(0, col.countUnique(), "Wrong number of unique values after StringColumn.create");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after StringColumn.create");
    col.append("A");
    col.append("B");
    assertEquals(2, col.countUnique(), "Wrong number of unique values after Column.set");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after Column.set");
    col.set(0, "C");
    col.set(1, "D");
    assertEquals(2, col.countUnique(), "Wrong number of unique values after Column.set");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after Column.set");
    col.clear();
    assertEquals(0, col.countUnique(), "Wrong number of unique values after Column.clear");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after Column.clear");
  }

  @Test
  void countUniqueSetAfterCreateShortDict() {
    int size = Byte.MAX_VALUE - Byte.MIN_VALUE + 1;
    StringColumn col = StringColumn.create("col1", size);
    assertEquals(1, col.countUnique(), "Wrong number of unique values after StringColumn.create");
    assertEquals(
        size, col.countMissing(), "Wrong number of missing values after StringColumn.create");
    for (int i = size; --i >= 0; ) {
      col.set(i, Integer.toString(i));
    }
    assertEquals(size, col.countUnique(), "Wrong number of unique values after Column.set");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after Column.set");
    col.clear();
    assertEquals(0, col.countUnique(), "Wrong number of unique values after Column.clear");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after Column.clear");
  }

  @Test
  void countUniqueSetAfterAppendShortDict() {
    StringColumn col = StringColumn.create("col1");
    assertEquals(0, col.countUnique(), "Wrong number of unique values after StringColumn.create");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after StringColumn.create");
    int size = Byte.MAX_VALUE - Byte.MIN_VALUE + 1;
    for (int i = size; --i >= 0; ) {
      col.append(Integer.toString(i));
    }
    assertEquals(size, col.countUnique(), "Wrong number of unique values after Column.set");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after Column.set");
    for (int i = size; --i >= 0; ) {
      col.set(i, "A" + i);
    }
    assertEquals(size, col.countUnique(), "Wrong number of unique values after Column.set");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after Column.set");
    col.clear();
    assertEquals(0, col.countUnique(), "Wrong number of unique values after Column.clear");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after Column.clear");
  }

  @Test
  void countUniqueSetAfterCreateIntDict() {
    int size = Short.MAX_VALUE - Short.MIN_VALUE + 1;
    StringColumn col = StringColumn.create("col1", size);
    assertEquals(1, col.countUnique(), "Wrong number of unique values after StringColumn.create");
    assertEquals(
        size, col.countMissing(), "Wrong number of missing values after StringColumn.create");
    for (int i = size; --i >= 0; ) {
      col.set(i, Integer.toString(i));
    }
    assertEquals(size, col.countUnique(), "Wrong number of unique values after Column.set");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after Column.set");
    col.clear();
    assertEquals(0, col.countUnique(), "Wrong number of unique values after Column.clear");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after Column.clear");
  }

  @Test
  void countUniqueSetAfterAppendIntDict() {
    StringColumn col = StringColumn.create("col1");
    assertEquals(0, col.countUnique(), "Wrong number of unique values after StringColumn.create");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after StringColumn.create");
    int size = Short.MAX_VALUE - Short.MIN_VALUE + 1;
    for (int i = size; --i >= 0; ) {
      col.append(Integer.toString(i));
    }
    assertEquals(size, col.countUnique(), "Wrong number of unique values after Column.set");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after Column.set");
    for (int i = size; --i >= 0; ) {
      col.set(i, "A" + i);
    }
    assertEquals(size, col.countUnique(), "Wrong number of unique values after Column.set");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after Column.set");
    col.clear();
    assertEquals(0, col.countUnique(), "Wrong number of unique values after Column.clear");
    assertEquals(0, col.countMissing(), "Wrong number of missing values after Column.clear");
  }

  @Test
  public void countUniqueWithDuplicates() {
    StringColumn col = StringColumn.create("col1");
    col.append("1");
    col.append("1");
    col.append("2");
    col.append("2");
    col.append("3");

    assertEquals(3, col.countUnique());
    assertEquals(3, col.unique().size());
    assertEquals(3, col.asStringColumn().unique().size());
  }

  @Test
  public void isIn() {
    String[] data = {"1", "1", "2", "2", "3", "4"};
    StringColumn col = StringColumn.create("col", data);
    Table t = Table.create("t", col);
    String[] data1 = {"1", "2"};
    StringColumn col1 = StringColumn.create("col1", data1);
    Table t2 = t.where(t.stringColumn("col").isIn(col1));
    assertEquals(4, t2.rowCount());
  }

  @Test
  public void isNotIn() {
    String[] data = {"1", "1", "2", "2", "3", "4"};
    StringColumn col = StringColumn.create("col", data);
    Table t = Table.create("t", col);
    String[] data1 = {"1", "2"};
    StringColumn col1 = StringColumn.create("col1", data1);
    Table t2 = t.where(t.stringColumn("col").isNotIn(col1));
    assertEquals(2, t2.rowCount());
  }

  @Test
  public void countUniqueWithMissing() {
    StringColumn col1 = StringColumn.create("col1");
    col1.append("1");
    col1.append("1");
    col1.append("2");
    col1.appendMissing();

    assertEquals(3, col1.countUnique());
    assertEquals(3, col1.unique().size());
  }

  @Test
  public void testSummary() {
    Table summary = column.summary();
    assertEquals(2, summary.columnCount());
    assertEquals(4, summary.rowCount());
    assertEquals("Count", summary.getUnformatted(0, 0));
    assertEquals("4", summary.getUnformatted(0, 1));
    assertEquals("Unique", summary.getUnformatted(1, 0));
    assertEquals("4", summary.getUnformatted(1, 1));
    assertEquals("Top", summary.getUnformatted(2, 0));
    assertEquals("Value 4", summary.getUnformatted(2, 1));
    assertEquals("Top Freq.", summary.getUnformatted(3, 0));
    assertEquals("1", summary.getUnformatted(3, 1));
  }
}
