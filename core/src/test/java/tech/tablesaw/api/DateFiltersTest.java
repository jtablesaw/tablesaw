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

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Locale;

import static tech.tablesaw.columns.dates.PackedLocalDate.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for Date Column
 */
public class DateFiltersTest {

    private DateColumn column1;

    @Before
    public void setUp() {
        Table table = Table.create("Test");
        column1 = DateColumn.create("Game date", Locale.ENGLISH);
        table.addColumn(column1);
    }

    @Test
    public void testDayOfWeek() {
        LocalDate date = LocalDate.of(2015, 1, 25);
        int packed = pack(date);

        DateColumn dateColumn = DateColumn.create("test");
        for (int i = 0; i < 7; i++) {
            dateColumn.append(date);
            date = date.plusDays(1);
        }

        assertTrue(dateColumn.isSunday().contains(0));

        packed = plusDays(1, packed);
        assertTrue(dateColumn.isMonday().contains(1));

        packed = plusDays(1, packed);
        assertTrue(dateColumn.isTuesday().contains(2));

        packed = plusDays(1, packed);
        assertTrue(dateColumn.isWednesday().contains(3));

        packed = plusDays(1, packed);
        assertTrue(dateColumn.isThursday().contains(4));

        packed = plusDays(1, packed);
        assertTrue(dateColumn.isFriday().contains(5));
        packed = plusDays(1, packed);
        assertTrue(dateColumn.isSaturday().contains(6));
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

        StringColumn month = dateColumn.month();
        NumberColumn monthValue = dateColumn.monthValue();

        for (int i = 0; i < months.length; i++) {

            assertEquals(months[i].name(), month.get(i).toUpperCase());
            assertEquals(i + 1, monthValue.get(i), 0.001);
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
        int equal = packed;
        int after = plusDays(1, packed);

        dateColumn.appendInternal(before);
        dateColumn.appendInternal(equal);
        dateColumn.appendInternal(after);

        assertTrue(dateColumn.isBefore(date).contains(0));
        assertTrue(dateColumn.isEqualTo(date).contains(1));
        assertTrue(dateColumn.isAfter(date).contains(2));

        // on-or-after on-or-before
        assertTrue(dateColumn.isOnOrBefore(date).contains(0));
        assertTrue(dateColumn.isOnOrBefore(date).contains(1));
        assertTrue(dateColumn.isOnOrAfter(date).contains(1));
        assertTrue(dateColumn.isOnOrAfter(date).contains(2));
    }

    @Test
    public void testComparison2() {
        LocalDate date = LocalDate.of(2015, 1, 25);
        int packed = pack(date);

        DateColumn dateColumn = DateColumn.create("test");

        int before = minusDays(1, packed);
        int equal = packed;
        int after = plusDays(1, packed);

        dateColumn.appendInternal(before);
        dateColumn.appendInternal(equal);
        dateColumn.appendInternal(after);

        assertTrue(dateColumn.isBefore(packed).contains(0));
        assertTrue(dateColumn.isEqualTo(packed).contains(1));
        assertTrue(dateColumn.isAfter(packed).contains(2));
    }

    @Test
    public void testIsInYear() {
        LocalDate date = LocalDate.of(2015, 1, 25);
        int packed = pack(date);

        DateColumn dateColumn = DateColumn.create("test");

        int otherYear = withYear(2016, packed);
        int sameYear = withDayOfMonth(24, packed);

        dateColumn.appendInternal(otherYear);
        dateColumn.appendInternal(sameYear);

        assertTrue(dateColumn.isInYear(2016).contains(0));
        assertTrue(dateColumn.isInYear(2015).contains(1));
    }

    @Test
    public void testDayOfMonth2() {
        LocalDate date = LocalDate.of(2015, 1, 31);
        int last = pack(date);

        DateColumn dateColumn = DateColumn.create("test");
        int first = plusDays(1, last);

        dateColumn.appendInternal(last);
        dateColumn.appendInternal(first);

        assertTrue(dateColumn.isLastDayOfMonth().contains(0));
        assertTrue(dateColumn.isFirstDayOfMonth().contains(1));
        assertFalse(dateColumn.isLastDayOfMonth().contains(1));
        assertFalse(dateColumn.isFirstDayOfMonth().contains(0));
    }
}
