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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.Comparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.booleans.BooleanColumnType;
import tech.tablesaw.columns.booleans.BooleanFormatter;
import tech.tablesaw.columns.booleans.BooleanParser;

/** Tests for BooleanColumn */
public class BooleanColumnTest {

  private static final String LINE_END = System.lineSeparator();

  private final BooleanColumn column = BooleanColumn.create("Test");

  @BeforeEach
  public void setUp() {
    column.append(false);
    column.append(false);
    column.append(false);
    column.append(false);
    column.append(true);
    column.append(true);
    column.append(false);
  }

  @Test
  public void testAny() {
    assertTrue(column.any());
  }

  @Test
  public void testAll() {
    assertFalse(column.all());
    BooleanColumn filtered = column.where(column.isTrue());
    assertTrue(filtered.all());
  }

  @Test
  public void asDoubleColumn() {
    DoubleColumn doubleColumn = column.asDoubleColumn();
    assertEquals(column.size(), doubleColumn.size());
  }

  @Test
  public void inRange() {
    assertFalse(column.all());
    BooleanColumn filtered = column.inRange(0, 2);
    assertEquals(2, filtered.size());
  }

  @Test
  public void fixNPESortingWithMissingValues() {
    assertFalse(column.all());
    column.appendMissing();
    column.appendMissing();
    column.sortAscending(); // Look. No NPE
    assertEquals(2, column.countMissing());
  }

  @Test
  public void testNone() {
    assertFalse(column.none());
    BooleanColumn filtered = column.where(column.isFalse());
    assertTrue(filtered.none());
  }

  @Test
  public void testSet() {
    assertFalse(column.none());
    column.set(column.isTrue(), false);
    assertTrue(column.none());
  }

  @Test
  public void testGetDouble() {
    assertEquals(1, column.getDouble(4), 0.0);
    assertEquals(0, column.getDouble(0), 0.0);
  }

  @Test
  public void testProportionTrue() {
    assertEquals(.285714, column.proportionTrue(), 0.00001);
  }

  @Test
  public void testAppendColumn() {
    BooleanColumn column1 = column.copy();
    column1.append(column);
    assertEquals(2 * column.size(), column1.size());
  }

  @Test
  void testSortAscending() {
    BooleanColumn column1 = BooleanColumn.create("t");
    column1.append(false);
    column1.append((byte) -128);
    column1.append(false);
    column1.append(false);
    column1.append(true);
    column1.append(true);
    column1.append(false);

    column1.sortAscending();
    assertEquals(BooleanColumnType.MISSING_VALUE, column1.getByte(0));
    assertEquals(false, column1.get(1));
    assertEquals(false, column1.get(2));
    assertEquals(false, column1.get(3));
    assertEquals(false, column1.get(4));
    assertEquals(true, column1.get(5));
    assertEquals(true, column1.get(6));
  }

  @Test
  void testSort() {
    BooleanColumn column1 = BooleanColumn.create("t");
    column1.append(false);
    column1.append((byte) -128);
    column1.append(false);
    column1.append(false);
    column1.append(true);
    column1.append(true);
    column1.append(false);

    Comparator<Boolean> comparator = Boolean::compare;
    Comparator<Boolean> c2 = Comparator.nullsFirst(comparator);
    column1 = column1.sorted(c2);
    assertEquals(BooleanColumnType.MISSING_VALUE, column1.getByte(0));
    assertEquals(false, column1.get(1));
    assertEquals(false, column1.get(2));
    assertEquals(false, column1.get(3));
    assertEquals(false, column1.get(4));
    assertEquals(true, column1.get(5));
    assertEquals(true, column1.get(6));
  }

  @Test
  void testSortDescending() {
    BooleanColumn column1 = BooleanColumn.create("t");
    column1.append(false);
    column1.append((byte) -128);
    column1.append(false);
    column1.append(false);
    column1.append(true);
    column1.append(true);
    column1.append(false);

    column1.sortDescending();
    assertEquals(true, column1.get(0));
    assertEquals(true, column1.get(1));
    assertEquals(false, column1.get(2));
    assertEquals(false, column1.get(3));
    assertEquals(false, column1.get(4));
    assertEquals(false, column1.get(5));
    assertEquals(BooleanColumnType.MISSING_VALUE, column1.getByte(6));
  }

  @Test
  public void testPrinting() {
    column.appendCell("");
    column.setPrintFormatter(new BooleanFormatter("Yes", "No", "IDK"));
    assertEquals("No", column.getString(0));
    assertEquals("Yes", column.getString(5));
    assertEquals("IDK", column.getString(column.size() - 1));
  }

  @Test
  public void testGetElements() {
    assertEquals(7, column.size());
  }

  @Test
  public void testCounts() {
    assertEquals(7, column.size());
    assertEquals(7, column.countTrue() + column.countFalse());
    assertEquals(2, column.countTrue());
  }

  @Test
  public void testAddCell() {
    column.append(true);
    assertEquals(8, column.size());

    // Add some other types and ensure that they're correctly truthy
    column.appendCell("true");
    assertTrue(lastEntry());
    column.appendCell("false");
    assertFalse(lastEntry());
    column.appendCell("TRUE");
    assertTrue(lastEntry());
    column.appendCell("FALSE");
    assertFalse(lastEntry());
    column.appendCell("T");
    assertTrue(lastEntry());
    column.appendCell("F");
    assertFalse(lastEntry());
    column.appendCell("Y");
    assertTrue(lastEntry());
    column.appendCell("N");
    assertFalse(lastEntry());
    column.appendCell("");
    assertNull(column.get(column.size() - 1));
  }

  @Test
  public void testCustomParser() {
    // Just do enough to ensure the parser is wired up correctly
    BooleanParser customParser = new BooleanParser(ColumnType.LOCAL_DATE);
    customParser.setMissingValueStrings(Collections.singletonList("not here"));
    column.setParser(customParser);

    column.appendCell("not here");
    assertTrue(column.isMissing(column.size() - 1));
    column.appendCell("true");
    assertFalse(column.isMissing(column.size() - 1));
  }

  @Test
  public void testGetType() {
    assertEquals("Boolean".toUpperCase(), column.type().name());
  }

  @Test
  public void testToString() {
    assertEquals("Boolean column: " + column.name(), column.toString());
  }

  @Test
  public void testPrint() {
    assertEquals(
        "Column: Test"
            + LINE_END
            + "false"
            + LINE_END
            + "false"
            + LINE_END
            + "false"
            + LINE_END
            + "false"
            + LINE_END
            + "true"
            + LINE_END
            + "true"
            + LINE_END
            + "false"
            + LINE_END,
        column.print());
  }

  @Test
  public void testSummary() {
    Table summary = column.summary();
    assertEquals(2, summary.columnCount());
    assertEquals(2, summary.rowCount());
    assertEquals("false", summary.getUnformatted(0, 0));
    assertEquals("5.0", summary.getUnformatted(0, 1));
    assertEquals("true", summary.getUnformatted(1, 0));
    assertEquals("2.0", summary.getUnformatted(1, 1));
  }

  @Test
  public void testCountUnique() {
    int result = column.countUnique();
    assertEquals(2, result);
    column.appendMissing();
    result = column.countUnique();
    assertEquals(3, result);
  }

  @Test
  public void testCountMissing() {
    int result = column.countMissing();
    assertEquals(0, result);
    column.appendMissing();
    result = column.countMissing();
    assertEquals(1, result);
  }

  @Test
  public void countTrue() {
    int result = column.countTrue();
    assertEquals(2, result);
  }

  @Test
  public void toByteArray() {
    column.appendMissing();
    byte[] expected = new byte[8];
    expected[0] = 0;
    expected[1] = 0;
    expected[2] = 0;
    expected[3] = 0;
    expected[4] = 1;
    expected[5] = 1;
    expected[6] = 0;
    expected[7] = -128;
    assertArrayEquals(expected, column.toByteArrayList().toByteArray());
  }

  @Test
  public void countFalse() {
    int result = column.countFalse();
    assertEquals(5, result);
  }

  @Test
  public void testToDoubleArray() {
    double[] result = column.asDoubleArray();
    assertEquals(0.0, result[0], 0.01);
    assertEquals(0.0, result[1], 0.01);
    assertEquals(0.0, result[2], 0.01);
    assertEquals(0.0, result[3], 0.01);
    assertEquals(1.0, result[4], 0.01);
    assertEquals(1.0, result[5], 0.01);
    assertEquals(0.0, result[6], 0.01);
  }

  /**
   * Tests construction from a bitmap. The test uses the isFalse() method, which inverts the values
   * in the column it's invoked on, so the true false counts are the opposite of those in the
   * original
   */
  @Test
  public void testBitmapConstructor() {
    BooleanColumn bc = BooleanColumn.create("Is false", column.isFalse(), column.size());
    Table summary = bc.summary();
    assertEquals(2, summary.columnCount());
    assertEquals(2, summary.rowCount());
    assertEquals("false", summary.getUnformatted(0, 0));
    assertEquals("2.0", summary.getUnformatted(0, 1));
    assertEquals("true", summary.getUnformatted(1, 0));
    assertEquals("5.0", summary.getUnformatted(1, 1));
  }

  @Test
  public void testSelectionMethods() {
    assertEquals(5, column.isFalse().size());
    assertEquals(2, column.isTrue().size());
    assertEquals(7, column.isNotMissing().size());
    assertEquals(0, column.isMissing().size());
  }

  /** Returns true if the last item added to the column is true and false otherwise */
  private boolean lastEntry() {
    return column.get(column.size() - 1);
  }
}
