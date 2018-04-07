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
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.selection.Selection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.*;


public class DateTimeFiltersTest {

    private DateTimeColumn localDateTimeColumn = DateTimeColumn.create("testing");

    @Before
    public void setUp() {
        localDateTimeColumn.append(LocalDate.of(2016, 2, 28).atStartOfDay()); // sunday
        localDateTimeColumn.append(LocalDate.of(2016, 2, 29).atStartOfDay()); // monday
        localDateTimeColumn.append(LocalDate.of(2016, 3, 1).atStartOfDay());  // tues
        localDateTimeColumn.append(LocalDate.of(2016, 3, 2).atStartOfDay());  // weds
        localDateTimeColumn.append(LocalDate.of(2016, 3, 3).atStartOfDay());  // thurs
        localDateTimeColumn.append(LocalDate.of(2016, 4, 1).atStartOfDay());
        localDateTimeColumn.append(LocalDate.of(2016, 4, 2).atStartOfDay());
        localDateTimeColumn.append(LocalDate.of(2016, 3, 4).atStartOfDay());   // fri
        localDateTimeColumn.append(LocalDate.of(2016, 3, 5).atStartOfDay());   // sat
    }

    @Test
    public void testDow() {
        assertTrue(localDateTimeColumn.isSunday().contains(0));
        assertTrue(localDateTimeColumn.isMonday().contains(1));
        assertTrue(localDateTimeColumn.isTuesday().contains(2));
        assertTrue(localDateTimeColumn.isWednesday().contains(3));
        assertTrue(localDateTimeColumn.isThursday().contains(4));
        assertTrue(localDateTimeColumn.isFriday().contains(7));
        assertTrue(localDateTimeColumn.isSaturday().contains(8));
    }

    @Test
    public void testIsFirstDayOfTheMonth() {
        Selection selection = localDateTimeColumn.isFirstDayOfMonth();
        assertFalse(selection.contains(0));
        assertFalse(selection.contains(1));
        assertTrue(selection.contains(2));
        assertTrue(selection.contains(5));
        assertFalse(selection.contains(6));
    }

    @Test
    public void testIsLastDayOfTheMonth() {
        Selection selection = localDateTimeColumn.isLastDayOfMonth();
        assertFalse(selection.contains(0));
        assertTrue(selection.contains(1));
        assertFalse(selection.contains(2));
    }

    @Test
    public void testIsInYear() {
        Selection selection = localDateTimeColumn.isInYear(2016);
        assertTrue(selection.contains(0));
        assertTrue(selection.contains(1));
        assertTrue(selection.contains(2));
    }

    @Test
    public void testGetMonthValue() {
        LocalDateTime date = LocalDate.of(2015, 1, 25).atStartOfDay();
        Month[] months = Month.values();

        DateTimeColumn dateTimeColumn = DateTimeColumn.create("test");
        for (int i = 0, monthsLength = months.length; i < monthsLength; i++) {
            dateTimeColumn.append(date);
            date = date.plusMonths(1);
        }

        assertTrue(dateTimeColumn.isInJanuary().contains(0));
        assertTrue(dateTimeColumn.isInFebruary().contains(1));
        assertTrue(dateTimeColumn.isInMarch().contains(2));
        assertTrue(dateTimeColumn.isInApril().contains(3));
        assertTrue(dateTimeColumn.isInMay().contains(4));
        assertTrue(dateTimeColumn.isInJune().contains(5));
        assertTrue(dateTimeColumn.isInJuly().contains(6));
        assertTrue(dateTimeColumn.isInAugust().contains(7));
        assertTrue(dateTimeColumn.isInSeptember().contains(8));
        assertTrue(dateTimeColumn.isInOctober().contains(9));
        assertTrue(dateTimeColumn.isInNovember().contains(10));
        assertTrue(dateTimeColumn.isInDecember().contains(11));

        assertTrue(dateTimeColumn.isInQ1().contains(2));
        assertTrue(dateTimeColumn.isInQ2().contains(4));
        assertTrue(dateTimeColumn.isInQ3().contains(8));
        assertTrue(dateTimeColumn.isInQ4().contains(11));
    }

    @Test
    public void testComparison() {
        LocalDateTime date = LocalDate.of(2015, 1, 25).atStartOfDay();
        DateTimeColumn dateTimeColumn = DateTimeColumn.create("test");

        LocalDateTime beforeDate = date.minusDays(1);
        LocalDateTime afterDate = date.plusDays(1);

        dateTimeColumn.append(beforeDate);
        dateTimeColumn.append(date);
        dateTimeColumn.append(afterDate);

        assertTrue(dateTimeColumn.isBefore(date).contains(0));
        assertTrue(dateTimeColumn.isEqualTo(date).contains(1));
        assertTrue(dateTimeColumn.isAfter(date).contains(2));
        assertTrue(dateTimeColumn.isBetweenExcluding(beforeDate, afterDate).contains(1));
        assertTrue(dateTimeColumn.isBetweenIncluding(beforeDate, afterDate).contains(2));
        assertTrue(dateTimeColumn.isBetweenIncluding(beforeDate, afterDate).contains(0));
        assertFalse(dateTimeColumn.isBetweenExcluding(beforeDate, afterDate).contains(2));
        assertFalse(dateTimeColumn.isBetweenExcluding(beforeDate, afterDate).contains(0));
    }
}
