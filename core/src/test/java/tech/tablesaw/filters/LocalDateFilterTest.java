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

package tech.tablesaw.filters;

import tech.tablesaw.columns.dates.DateColumnReference;
import tech.tablesaw.columns.datetimes.filters.IsInApril;
import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.datetimes.filters.IsFirstDayOfTheMonth;
import tech.tablesaw.columns.datetimes.filters.IsInFebruary;
import tech.tablesaw.columns.datetimes.filters.IsInMarch;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.columns.datetimes.filters.IsInYear;
import tech.tablesaw.columns.datetimes.filters.IsLastDayOfTheMonth;
import tech.tablesaw.selection.Selection;

import java.time.LocalDate;

import static org.junit.Assert.*;


public class LocalDateFilterTest {

    private DateColumn localDateColumn = DateColumn.create("testing");
    private Table table = Table.create("test");
    private DateColumnReference reference = new DateColumnReference("testing");

    @Before
    public void setUp() {
        localDateColumn.append(LocalDate.of(2016, 2, 28)); // sunday
        localDateColumn.append(LocalDate.of(2016, 2, 29)); // monday
        localDateColumn.append(LocalDate.of(2016, 3, 1));  // tues
        localDateColumn.append(LocalDate.of(2016, 3, 2));  // weds
        localDateColumn.append(LocalDate.of(2016, 3, 3));  // thurs
        localDateColumn.append(LocalDate.of(2016, 4, 1));
        localDateColumn.append(LocalDate.of(2016, 4, 2));
        localDateColumn.append(LocalDate.of(2016, 3, 4));   // fri
        localDateColumn.append(LocalDate.of(2016, 3, 5));   // sat
        table.addColumn(localDateColumn);
    }

    @Test
    public void testDow() {
        Selection selection = reference.isSunday().apply(table);
        assertTrue(reference.isSunday().apply(table).contains(0));
        assertTrue(reference.isMonday().apply(table).contains(1));
        assertTrue(reference.isTuesday().apply(table).contains(2));
        assertTrue(reference.isWednesday().apply(table).contains(3));
        assertTrue(reference.isThursday().apply(table).contains(4));
        assertTrue(reference.isFriday().apply(table).contains(7));
        assertTrue(reference.isSaturday().apply(table).contains(8));
        assertFalse(selection.contains(3));
        assertFalse(selection.contains(4));
        assertFalse(selection.contains(5));
        assertFalse(selection.contains(6));
        assertFalse(selection.contains(7));
    }

    @Test
    public void testIsFebruary() {
        IsInFebruary isFebruary = reference.isInFebruary();
        Selection selection = isFebruary.apply(table);
        assertTrue(selection.contains(0));
        assertTrue(selection.contains(1));
        assertFalse(selection.contains(2));
    }

    @Test
    public void testIsMarch() {
        IsInMarch result = reference.isInMarch();
        Selection selection = result.apply(table);
        assertFalse(selection.contains(0));
        assertFalse(selection.contains(1));
        assertTrue(selection.contains(2));
    }

    @Test
    public void testIsApril() {
        IsInApril result = reference.isInApril();
        Selection selection = result.apply(table);
        assertFalse(selection.contains(0));
        assertTrue(selection.contains(5));
        assertTrue(selection.contains(6));
    }

    @Test
    public void testIsFirstDayOfTheMonth() {
        IsFirstDayOfTheMonth result = reference.isFirstDayOfMonth();
        Selection selection = result.apply(table);
        assertFalse(selection.contains(0));
        assertFalse(selection.contains(1));
        assertTrue(selection.contains(2));
        assertTrue(selection.contains(5));
        assertFalse(selection.contains(6));
    }

    @Test
    public void testIsLastDayOfTheMonth() {
        IsLastDayOfTheMonth result = reference.isLastDayOfMonth();
        Selection selection = result.apply(table);
        assertFalse(selection.contains(0));
        assertTrue(selection.contains(1));
        assertFalse(selection.contains(2));
    }

    @Test
    public void testIsInYear() {
        IsInYear result = reference.isInYear(2016);
        Selection selection = result.apply(table);
        assertTrue(selection.contains(0));
        assertTrue(selection.contains(1));
        assertTrue(selection.contains(2));
        result = new IsInYear(reference, 2015);
        selection = result.apply(table);
        assertFalse(selection.contains(0));
        assertFalse(selection.contains(1));
        assertFalse(selection.contains(2));
    }
}
