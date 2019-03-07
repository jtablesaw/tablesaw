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

package tech.tablesaw.columns.datetimes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class DateTimeFiltersTest {

    private DateTimeColumn localDateTimeColumn = DateTimeColumn.create("testing");
    private Table table = Table.create("test");

    @BeforeEach
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
        table.addColumns(localDateTimeColumn);
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

        selection = localDateTimeColumn.isInYear(2015);
        assertFalse(selection.contains(0));
        assertFalse(selection.contains(1));
        assertFalse(selection.contains(2));
    }

    @Test
    public void testIsMissing() {
        DateTimeColumn column = DateTimeColumn.create("test");
        column.append(LocalDateTime.now());
        column.appendInternal(DateTimeColumnType.missingValueIndicator());

        assertTrue(column.isMissing().contains(1));
        assertTrue(column.isNotMissing().contains(0));
        assertTrue(column.isNotMissing().contains(0));
        assertTrue(column.isMissing().contains(1));
    }

    @Test
    public void testTimeOfDatePredicates() {
        DateTimeColumn column = DateTimeColumn.create("test");
        column.append(LocalDate.of(2015, 1, 1).atStartOfDay());
        column.append(LocalDateTime.of(2015, 1, 1, 1, 0));
        column.append(LocalDateTime.of(2015, 1, 1, 12, 0));
        column.append(LocalDateTime.of(2015, 1, 1, 13, 0));

        assertTrue(column.isMidnight().contains(0));
        assertFalse(column.isMidnight().contains(1));

        assertTrue(column.isBeforeNoon().contains(0));
        assertTrue(column.isBeforeNoon().contains(1));

        assertTrue(column.isNoon().contains(2));

        assertFalse(column.isAfterNoon().contains(1));
        assertTrue(column.isAfterNoon().contains(3));
    }

    @Test
    public void testGetMonthValue() {
        LocalDateTime date = LocalDate.of(2015, 1, 25).atStartOfDay();
        Month[] months = Month.values();

        DateTimeColumn dateTimeColumn = DateTimeColumn.create("test");
        for (int i = 0; i < months.length; i++) {
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

        Table t = Table.create("Test");
        t.addColumns(dateTimeColumn);
        IntColumn index = IntColumn.indexColumn("index", t.rowCount(), 0);
        t.addColumns(index);
    }

    @Test
    public void testComparison() {
        LocalDate date = LocalDate.of(2015, 1, 25);
        LocalDateTime dateTime = LocalDate.of(2015, 1, 25).atStartOfDay();
        DateTimeColumn dateTimeColumn = DateTimeColumn.create("test");

        LocalDateTime beforeDate = dateTime.minusDays(1);
        LocalDateTime afterDate = dateTime.plusDays(1);

        dateTimeColumn.append(beforeDate);
        dateTimeColumn.append(dateTime);
        dateTimeColumn.append(afterDate);

        IntColumn index = IntColumn.indexColumn("index", dateTimeColumn.size(), 0);
        Table t = Table.create("test", dateTimeColumn, index);

        assertTrue(dateTimeColumn.isOnOrBefore(date).contains(0));
        assertTrue(dateTimeColumn.isOnOrBefore(date).contains(1));

        assertTrue(dateTimeColumn.isOnOrAfter(date).contains(1));
        assertTrue(dateTimeColumn.isOnOrAfter(date).contains(2));

        assertTrue(dateTimeColumn.isBefore(dateTime).contains(0));
        assertTrue(dateTimeColumn.isBefore(date).contains(0));
        assertTrue(t.where(t.dateTimeColumn("test")
                .isBefore(dateTime)).intColumn("index").contains(0));
        assertTrue(t.where(t.dateTimeColumn("test")
                .isBefore(date)).intColumn("index").contains(0));

        assertTrue(dateTimeColumn.isEqualTo(dateTime).contains(1));
        assertTrue(t.where(t.dateTimeColumn("test")
                .isEqualTo(dateTime)).intColumn("index").contains(1));

        assertTrue(dateTimeColumn.isAfter(dateTime).contains(2));
        assertTrue(dateTimeColumn.isAfter(date).contains(2));
        assertTrue(t.where(t.dateTimeColumn("test")
                .isAfter(dateTime)).intColumn("index").contains(2));
        assertTrue(t.where(t.dateTimeColumn("test")
                .isAfter(date)).intColumn("index").contains(2));

        assertTrue(dateTimeColumn.isNotEqualTo(dateTime).contains(2));
        assertTrue(t.where(t.dateTimeColumn("test")
                .isNotEqualTo(dateTime)).intColumn("index").contains(2));

        assertTrue(dateTimeColumn.isBetweenExcluding(beforeDate, afterDate).contains(1));
        assertTrue(t.where(t.dateTimeColumn("test")
                .isBetweenExcluding(beforeDate, afterDate)).intColumn("index").contains(1));
        assertTrue(dateTimeColumn.isBetweenExcluding(beforeDate, afterDate).contains(1));
        assertTrue(t.where(t.dateTimeColumn("test")
                .isBetweenExcluding(beforeDate, afterDate)).intColumn("index").contains(1));

        assertTrue(dateTimeColumn.isBetweenIncluding(beforeDate, afterDate).contains(2));
        assertTrue(t.where(t.dateTimeColumn("test")
                .isBetweenIncluding(beforeDate, afterDate)).intColumn("index").contains(2));

        assertTrue(dateTimeColumn.isBetweenIncluding(beforeDate, afterDate).contains(0));
        assertTrue(t.where(t.dateTimeColumn("test")
                .isBetweenIncluding(beforeDate, afterDate)).intColumn("index").contains(0));

        assertFalse(dateTimeColumn.isBetweenExcluding(beforeDate, afterDate).contains(2));
        assertFalse(t.where(t.dateTimeColumn("test")
                .isBetweenExcluding(beforeDate, afterDate)).intColumn("index").contains(2));

        assertFalse(dateTimeColumn.isBetweenExcluding(beforeDate, afterDate).contains(0));
        assertFalse(t.where(t.dateTimeColumn("test")
                .isBetweenExcluding(beforeDate, afterDate)).intColumn("index").contains(0));
    }


    @Test
    public void testComparison2() {
        LocalDateTime dateTime = LocalDate.of(2015, 1, 25).atStartOfDay();
        DateTimeColumn dateTimeColumn = DateTimeColumn.create("test");

        LocalDateTime beforeDate = dateTime.minusDays(1);
        LocalDateTime afterDate = dateTime.plusDays(1);

        dateTimeColumn.append(beforeDate);
        dateTimeColumn.append(dateTime);
        dateTimeColumn.append(afterDate);

        DateTimeColumn same = DateTimeColumn.create("same");
        same.append(beforeDate);
        same.append(dateTime);
        same.append(afterDate);

        DateTimeColumn before = DateTimeColumn.create("before");
        before.append(beforeDate.minusDays(1));
        before.append(dateTime.minusDays(1));
        before.append(afterDate.minusDays(1));

        DateTimeColumn after = DateTimeColumn.create("after");
        after.append(beforeDate.plusDays(1));
        after.append(dateTime.plusDays(1));
        after.append(afterDate.plusDays(1));

        Table t = Table.create("test", dateTimeColumn, same, before, after);


        assertTrue(dateTimeColumn.isEqualTo(same).contains(0));
        assertTrue(t.dateTimeColumn("test").isEqualTo(same).contains(0));
        assertTrue(t.dateTimeColumn("test").isEqualTo(t.dateTimeColumn("same")).contains(0));

        assertTrue(dateTimeColumn.isBefore(after).contains(0));
        assertTrue(t.dateTimeColumn("test").isBefore(after).contains(0));
        assertTrue(t.dateTimeColumn("test").isBefore(t.dateTimeColumn("after")).contains(0));

        assertTrue(dateTimeColumn.isAfter(before).contains(0));
        assertTrue(t.dateTimeColumn("test").isAfter(before).contains(0));
        assertTrue(t.dateTimeColumn("test").isAfter(t.dateTimeColumn("before")).contains(0));

        assertFalse(dateTimeColumn.isNotEqualTo(same).contains(0));
        assertFalse(t.dateTimeColumn("test").isNotEqualTo(same).contains(0));
        assertFalse(t.dateTimeColumn("test").isNotEqualTo(t.dateTimeColumn("same")).contains(0));

        assertTrue(dateTimeColumn.isOnOrBefore(same).contains(0));
        assertTrue(dateTimeColumn.isOnOrBefore(after).contains(0));
        assertFalse(dateTimeColumn.isOnOrBefore(before).contains(0));
        assertTrue(dateTimeColumn.isNotEqualTo(before).contains(0));

        assertTrue(dateTimeColumn.isOnOrAfter(same).contains(1));
        assertTrue(dateTimeColumn.isOnOrAfter(before).contains(2));
        assertFalse(dateTimeColumn.isOnOrAfter(after).contains(2));
        assertTrue(dateTimeColumn.isNotEqualTo(after).contains(0));

        assertTrue(t.dateTimeColumn("test")
                .isOnOrAfter(t.dateTimeColumn("same")).contains(0));
        assertTrue(t.dateTimeColumn("test")
                .isOnOrAfter(same).contains(0));

        assertFalse(t.dateTimeColumn("test")
                .isOnOrAfter(t.dateTimeColumn("after")).contains(0));
        assertFalse(t.dateTimeColumn("test")
                .isOnOrAfter(after).contains(0));

        assertTrue(t.dateTimeColumn("test")
                .isOnOrBefore(t.dateTimeColumn("same")).contains(0));
        assertTrue(t.dateTimeColumn("test")
                .isOnOrBefore(same).contains(0));

        assertTrue(t.dateTimeColumn("test")
                .isOnOrBefore(t.dateTimeColumn("after")).contains(0));
        assertTrue(t.dateTimeColumn("test")
                .isOnOrBefore(after).contains(0));

        assertFalse(t.dateTimeColumn("test")
                .isOnOrBefore(t.dateTimeColumn("before")).contains(0));
        assertFalse(t.dateTimeColumn("test")
                .isOnOrBefore(before).contains(0));

        assertTrue(t.dateTimeColumn("test")
                .isNotEqualTo(t.dateTimeColumn("before")).contains(0));
        assertTrue(t.dateTimeColumn("test")
                .isNotEqualTo(before).contains(0));
        assertFalse(t.dateTimeColumn("test")
                .isNotEqualTo(t.dateTimeColumn("same")).contains(0));
        assertFalse(t.dateTimeColumn("test")
                .isNotEqualTo(same).contains(0));
    }
}
