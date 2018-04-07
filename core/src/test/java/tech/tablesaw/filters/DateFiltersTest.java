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

import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.selection.Selection;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.Assert.*;
import static tech.tablesaw.columns.dates.PackedLocalDate.*;

public class DateFiltersTest {

    private DateColumn localDateColumn = DateColumn.create("testing");
    private Table table = Table.create("test");

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
        assertTrue(localDateColumn.isSunday().apply(table).contains(0));
        assertTrue(localDateColumn.isMonday().apply(table).contains(1));
        assertTrue(localDateColumn.isTuesday().apply(table).contains(2));
        assertTrue(localDateColumn.isWednesday().apply(table).contains(3));
        assertTrue(localDateColumn.isThursday().apply(table).contains(4));
        assertTrue(localDateColumn.isFriday().apply(table).contains(7));
        assertTrue(localDateColumn.isSaturday().apply(table).contains(8));
    }

    @Test
    public void testIsFirstDayOfTheMonth() {
        Selection selection = localDateColumn.isFirstDayOfMonth();
        assertFalse(selection.contains(0));
        assertFalse(selection.contains(1));
        assertTrue(selection.contains(2));
        assertTrue(selection.contains(5));
        assertFalse(selection.contains(6));
    }

    @Test
    public void testIsLastDayOfTheMonth() {
        Selection selection = localDateColumn.isLastDayOfMonth();
        assertFalse(selection.contains(0));
        assertTrue(selection.contains(1));
        assertFalse(selection.contains(2));
    }

    @Test
    public void testIsInYear() {
        Selection selection = localDateColumn.isInYear(2016);
        assertTrue(selection.contains(0));
        assertTrue(selection.contains(1));
        assertTrue(selection.contains(2));
    }

    @Test
    public void testGetMonthValue() {
        LocalDate date = LocalDate.of(2015, 1, 25);
        Month[] months = Month.values();

        DateColumn dateColumn = DateColumn.create("test");
        for (int i = 0, monthsLength = months.length; i < monthsLength; i++) {
            dateColumn.append(date);
            date = date.plusMonths(1);
        }

        assertTrue(dateColumn.isInJanuary().contains(0));
        assertTrue(dateColumn.isInFebruary().contains(1));
        assertTrue(dateColumn.isInMarch().contains(2));
        assertTrue(dateColumn.isInApril().contains(3));
        assertTrue(dateColumn.isInMay().contains(4));
        assertTrue(dateColumn.isInJune().contains(5));
        assertTrue(dateColumn.isInJuly().contains(6));
        assertTrue(dateColumn.isInAugust().contains(7));
        assertTrue(dateColumn.isInSeptember().contains(8));
        assertTrue(dateColumn.isInOctober().contains(9));
        assertTrue(dateColumn.isInNovember().contains(10));
        assertTrue(dateColumn.isInDecember().contains(11));

        assertTrue(dateColumn.isInQ1().contains(2));
        assertTrue(dateColumn.isInQ2().contains(4));
        assertTrue(dateColumn.isInQ3().contains(8));
        assertTrue(dateColumn.isInQ4().contains(11));
    }

    @Test
    public void testComparison() {
        LocalDate date = LocalDate.of(2015, 1, 25);
        int packed = pack(date);

        DateColumn dateColumn = DateColumn.create("test");

        int before = minusDays(1, packed);
        int after = plusDays(1, packed);

        LocalDate beforeDate = PackedLocalDate.asLocalDate(before);
        LocalDate afterDate = PackedLocalDate.asLocalDate(after);

        dateColumn.appendInternal(before);
        dateColumn.appendInternal(packed);
        dateColumn.appendInternal(after);

        assertTrue(dateColumn.isBefore(packed).contains(0));
        assertTrue(dateColumn.isEqualTo(packed).contains(1));
        assertTrue(dateColumn.isAfter(packed).contains(2));

        assertTrue(dateColumn.isBetweenExcluding(beforeDate, afterDate).contains(1));
        assertTrue(dateColumn.isBetweenIncluding(beforeDate, afterDate).contains(2));
        assertTrue(dateColumn.isBetweenIncluding(beforeDate, afterDate).contains(0));
        assertFalse(dateColumn.isBetweenExcluding(beforeDate, afterDate).contains(2));
        assertFalse(dateColumn.isBetweenExcluding(beforeDate, afterDate).contains(0));
    }

}
