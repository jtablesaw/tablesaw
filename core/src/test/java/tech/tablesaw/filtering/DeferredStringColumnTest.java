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

package tech.tablesaw.filtering;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

/**
 * Unit tests for DeferredStringColumn. Validates that deferred string filtering methods map
 * correctly to their underlying StringColumn implementations and yield the correct Selection.
 */
public class DeferredStringColumnTest {

  private Table table;
  private DeferredStringColumn deferredCol;
  private StringColumn otherCol;

  @BeforeEach
  public void setUp() {
    // Set up a mock text column with diverse equivalence partitions:
    // normal strings, empty strings, nulls, alphanumerics, and case variations.
    StringColumn col =
        StringColumn.create(
            "textCol",
            new String[] {
              "Hello World", // 0
              "World", // 1
              "", // 2
              null, // 3
              "123", // 4
              "abc", // 5
              "HELLO", // 6
              "abc12", // 7
              "hello" // 8
            });

    // Secondary column to test column-to-column comparisons
    otherCol =
        StringColumn.create(
            "otherCol",
            new String[] {
              "Hello World", // 0
              "No Match", // 1
              "", // 2
              "different", // 3
              "123", // 4
              "ABC", // 5
              "HELLO", // 6
              "Abc12", // 7
              "hello" // 8
            });

    table = Table.create("DummyTable", col, otherCol);
    deferredCol = new DeferredStringColumn("textCol");
  }

  /** Tests string prefix matching operations against both a scalar string and another column. */
  @Test
  public void testStartsWith() {
    Selection sel1 = deferredCol.startsWith("Hello").apply(table);
    assertTrue(sel1.contains(0)); // "Hello World" starts with "Hello"
    assertFalse(sel1.contains(1)); // "World" does not

    Selection sel2 = deferredCol.startsWith(otherCol).apply(table);
    assertTrue(sel2.contains(0)); // "Hello World" starts with "Hello World"
    assertFalse(sel2.contains(1)); // "World" does not start with "No Match"
  }

  /** Tests string suffix matching and substring inclusion operations. */
  @Test
  public void testEndsWithAndContains() {
    Selection selEnds = deferredCol.endsWith("ld").apply(table);
    assertTrue(selEnds.contains(0)); // "Hello World" ends with "ld"
    assertTrue(selEnds.contains(1)); // "World" ends with "ld"

    Selection selContains = deferredCol.containsString("lo").apply(table);
    assertTrue(selContains.contains(0)); // "Hello World" contains "lo"
    assertTrue(selContains.contains(8)); // "hello" contains "lo"
  }

  /** Tests filtering by Regular Expression patterns. */
  @Test
  public void testMatchesRegex() {
    Selection sel1 = deferredCol.matchesRegex("\\d+").apply(table);
    assertTrue(sel1.contains(4)); // "123" is numeric
    assertFalse(sel1.contains(5)); // "abc" is not numeric
  }

  /**
   * Tests equality checks (isEqualTo, isNotEqualTo, equalsIgnoreCase) against both scalar strings
   * and other columns.
   */
  @Test
  public void testEquality() {
    // Scalar comparisons
    Selection selEq = deferredCol.isEqualTo("World").apply(table);
    assertTrue(selEq.contains(1));

    Selection selNotEq = deferredCol.isNotEqualTo("World").apply(table);
    assertTrue(selNotEq.contains(0));

    // Column comparisons
    Selection selEqCol = deferredCol.isEqualTo(otherCol).apply(table);
    assertTrue(selEqCol.contains(0)); // Match
    assertTrue(selEqCol.contains(4)); // Match
    assertFalse(selEqCol.contains(1)); // Mismatch

    Selection selNotEqCol = deferredCol.isNotEqualTo(otherCol).apply(table);
    assertTrue(selNotEqCol.contains(1));
    assertFalse(selNotEqCol.contains(0));

    // Case-insensitive column comparisons
    Selection selIgnore = deferredCol.equalsIgnoreCase(otherCol).apply(table);
    assertTrue(selIgnore.contains(5)); // "abc" vs "ABC" ignores case
  }

  /** Tests set inclusion operations (isIn, isNotIn) using both varargs arrays and Collections. */
  @Test
  public void testInAndNotIn() {
    // Varargs isIn
    Selection selIn = deferredCol.isIn("123", "abc").apply(table);
    assertTrue(selIn.contains(4));
    assertTrue(selIn.contains(5));

    // Collection isIn
    List<String> list = Arrays.asList("123", "abc");
    Selection selInList = deferredCol.isIn(list).apply(table);
    assertTrue(selInList.contains(4));

    // Varargs isNotIn
    Selection selNotIn = deferredCol.isNotIn("123", "abc").apply(table);
    assertTrue(selNotIn.contains(1));
    assertFalse(selNotIn.contains(4));

    // Collection isNotIn
    Selection selNotInList = deferredCol.isNotIn(list).apply(table);
    assertTrue(selNotInList.contains(1));
  }

  /** Tests boolean property checks on strings (e.g., numeric, alphabetic, case, emptiness). */
  @Test
  public void testProperties() {
    assertTrue(deferredCol.isNumeric().apply(table).contains(4)); // "123"
    assertTrue(deferredCol.isAlpha().apply(table).contains(5)); // "abc"
    assertTrue(deferredCol.isAlphaNumeric().apply(table).contains(7)); // "abc12"
    assertTrue(deferredCol.isUpperCase().apply(table).contains(6)); // "HELLO"
    assertTrue(deferredCol.isLowerCase().apply(table).contains(5)); // "abc"
    assertTrue(deferredCol.isEmptyString().apply(table).contains(2)); // ""
  }

  /** Tests operations based on the length of the strings. */
  @Test
  public void testLengthMethods() {
    assertTrue(deferredCol.lengthEquals(3).apply(table).contains(4)); // "123" length is 3
    assertTrue(deferredCol.isShorterThan(5).apply(table).contains(2)); // "" length is 0 < 5
    assertTrue(
        deferredCol.isLongerThan(5).apply(table).contains(0)); // "Hello World" length is 11 > 5
  }

  /** Tests missing value operations inherited from DeferredColumn. */
  @Test
  public void testMissing() {
    assertTrue(deferredCol.isMissing().apply(table).contains(3)); // null
    assertTrue(deferredCol.isNotMissing().apply(table).contains(0)); // "Hello World"
  }
}
