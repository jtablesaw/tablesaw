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

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class DeferredDateColumnTest {

  private Table table;
  private DeferredDateColumn deferredCol;

  @BeforeEach
  public void setUp() {
    // Surgical dataset covering Boundary Values and Partitions
    DateColumn col = DateColumn.create("dateCol");
    col.append(
        LocalDate.of(2020, Month.JANUARY, 1)); // 0: First day of year / Q1 / January / Wednesday
    col.append(LocalDate.of(2020, Month.FEBRUARY, 29)); // 1: Leap year / Q1 / February / Saturday
    col.append(LocalDate.of(2020, Month.JUNE, 15)); // 2: Mid year / Q2 / June / Monday
    col.append(
        LocalDate.of(2020, Month.DECEMBER, 31)); // 3: Last day of year / Q4 / December / Thursday
    col.appendMissing(); // 4: Missing value

    table = Table.create("DummyTable", col);
    deferredCol = new DeferredDateColumn("dateCol");
  }

  // =================================================================================
  // 1. DATE COMPARISON TESTS
  // =================================================================================

  @Test
  public void testComparisons() {
    LocalDate pivot = LocalDate.of(2020, Month.JUNE, 15);

    // isBefore
    Selection selBefore = deferredCol.isBefore(pivot).apply(table);
    assertTrue(selBefore.contains(0));
    assertTrue(selBefore.contains(1));
    assertFalse(selBefore.contains(2)); // Equal is not before

    // isAfter
    Selection selAfter = deferredCol.isAfter(pivot).apply(table);
    assertTrue(selAfter.contains(3));
    assertFalse(selAfter.contains(2));

    // isOnOrBefore / isOnOrAfter
    assertTrue(deferredCol.isOnOrBefore(pivot).apply(table).contains(2));
    assertTrue(deferredCol.isOnOrAfter(pivot).apply(table).contains(2));

    // isEqualTo
    assertTrue(deferredCol.isEqualTo(pivot).apply(table).contains(2));
    assertFalse(deferredCol.isEqualTo(pivot).apply(table).contains(0));

    // isBetweenExcluding / isBetweenIncluding
    LocalDate start = LocalDate.of(2020, Month.JANUARY, 1);
    LocalDate end = LocalDate.of(2020, Month.JUNE, 15);

    Selection selBetweenEx = deferredCol.isBetweenExcluding(start, end).apply(table);
    assertFalse(selBetweenEx.contains(0)); // Start is exclusive
    assertTrue(selBetweenEx.contains(1)); // Inside
    assertFalse(selBetweenEx.contains(2)); // End is exclusive

    Selection selBetweenInc = deferredCol.isBetweenIncluding(start, end).apply(table);
    assertTrue(selBetweenInc.contains(0)); // Start is inclusive
    assertTrue(selBetweenInc.contains(1)); // Inside
    assertTrue(selBetweenInc.contains(2)); // End is inclusive

    // isNotEqualTo
    assertTrue(deferredCol.isNotEqualTo(pivot).apply(table).contains(0));
    assertFalse(deferredCol.isNotEqualTo(pivot).apply(table).contains(2));
  }

  // =================================================================================
  // 2. MONTHS AND DAYS OF WEEK TESTS
  // =================================================================================

  @Test
  public void testMonthsAndDaysOfWeek() {
    // Test months
    assertTrue(deferredCol.isInJanuary().apply(table).contains(0));
    assertTrue(deferredCol.isInFebruary().apply(table).contains(1));
    assertTrue(deferredCol.isInJune().apply(table).contains(2));
    assertTrue(deferredCol.isInDecember().apply(table).contains(3));
    assertFalse(deferredCol.isInJanuary().apply(table).contains(1));

    // Test days of week
    assertTrue(deferredCol.isWednesday().apply(table).contains(0));
    assertTrue(deferredCol.isSaturday().apply(table).contains(1));
    assertTrue(deferredCol.isMonday().apply(table).contains(2));
    assertTrue(deferredCol.isThursday().apply(table).contains(3));
    assertFalse(deferredCol.isWednesday().apply(table).contains(2));

    assertTrue(deferredCol.isInMarch().apply(table).isEmpty());
    assertTrue(deferredCol.isInApril().apply(table).isEmpty());
    assertTrue(deferredCol.isInMay().apply(table).isEmpty());
    assertTrue(deferredCol.isInJuly().apply(table).isEmpty());
    assertTrue(deferredCol.isInAugust().apply(table).isEmpty());
    assertTrue(deferredCol.isInSeptember().apply(table).isEmpty());
    assertTrue(deferredCol.isInOctober().apply(table).isEmpty());
    assertTrue(deferredCol.isInNovember().apply(table).isEmpty());

    assertTrue(deferredCol.isSunday().apply(table).isEmpty());
    assertTrue(deferredCol.isTuesday().apply(table).isEmpty());
    assertTrue(deferredCol.isFriday().apply(table).isEmpty());
  }

  // =================================================================================
  // 3. QUARTERS AND YEAR TESTS
  // =================================================================================

  @Test
  public void testQuartersAndYear() {
    // Test Quarters
    Selection q1 = deferredCol.isInQ1().apply(table);
    assertTrue(q1.contains(0));
    assertTrue(q1.contains(1));
    assertFalse(q1.contains(2));

    Selection q2 = deferredCol.isInQ2().apply(table);
    assertTrue(q2.contains(2));

    Selection q4 = deferredCol.isInQ4().apply(table);
    assertTrue(q4.contains(3));

    // Test Year
    Selection year2020 = deferredCol.isInYear(2020).apply(table);
    assertTrue(year2020.contains(0));
    assertTrue(year2020.contains(1));
    assertTrue(year2020.contains(2));
    assertTrue(year2020.contains(3));

    Selection year2021 = deferredCol.isInYear(2021).apply(table);
    assertTrue(year2021.isEmpty());

    assertTrue(deferredCol.isInQ3().apply(table).isEmpty());
  }

  // =================================================================================
  // 4. MONTH EDGES AND MISSING TESTS
  // =================================================================================

  @Test
  public void testMonthEdgesAndMissing() {
    // Test first and last days of the month
    Selection firstDay = deferredCol.isFirstDayOfMonth().apply(table);
    assertTrue(firstDay.contains(0));
    assertFalse(firstDay.contains(1));

    Selection lastDay = deferredCol.isLastDayOfMonth().apply(table);
    assertTrue(lastDay.contains(1)); // 2020-02-29 is the last day
    assertTrue(lastDay.contains(3)); // 2020-12-31 is the last day
    assertFalse(lastDay.contains(0));

    // Test missing values
    Selection missing = deferredCol.isMissing().apply(table);
    assertTrue(missing.contains(4));
    assertFalse(missing.contains(0));

    Selection notMissing = deferredCol.isNotMissing().apply(table);
    assertTrue(notMissing.contains(0));
    assertTrue(notMissing.contains(1));
    assertTrue(notMissing.contains(2));
    assertTrue(notMissing.contains(3));
    assertFalse(notMissing.contains(4));
  }

  // =================================================================================
  // 5. COLUMN-TO-COLUMN COMPARISON TESTS
  // =================================================================================

  @Test
  public void testColumnComparisons() {
    // Create a second column to compare side-by-side with dateCol
    DateColumn otherCol = DateColumn.create("otherCol");
    otherCol.append(LocalDate.of(2020, Month.JANUARY, 1)); // Row 0: Equal to dateCol
    otherCol.append(LocalDate.of(2020, Month.JANUARY, 1)); // Row 1: Before dateCol (Feb 29)
    otherCol.append(LocalDate.of(2020, Month.DECEMBER, 31)); // Row 2: After dateCol (Jun 15)
    otherCol.append(LocalDate.of(2020, Month.DECEMBER, 31)); // Row 3: Equal to dateCol
    otherCol.appendMissing(); // Row 4: Missing

    table.addColumns(otherCol);

    // Test the 6 remaining white-box methods
    assertTrue(deferredCol.isEqualTo(otherCol).apply(table).contains(0));
    assertTrue(deferredCol.isNotEqualTo(otherCol).apply(table).contains(1));
    assertTrue(deferredCol.isAfter(otherCol).apply(table).contains(1));
    assertTrue(deferredCol.isBefore(otherCol).apply(table).contains(2));
    assertTrue(deferredCol.isOnOrAfter(otherCol).apply(table).contains(0));
    assertTrue(deferredCol.isOnOrBefore(otherCol).apply(table).contains(2));
  }
}
