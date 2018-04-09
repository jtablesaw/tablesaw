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

import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.datetimes.DateTimeColumnReference;
import tech.tablesaw.columns.datetimes.filters.IsFirstDayOfTheMonth;
import tech.tablesaw.columns.datetimes.filters.IsInYear;
import tech.tablesaw.columns.datetimes.filters.IsLastDayOfTheMonth;
import tech.tablesaw.selection.Selection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.*;
import static tech.tablesaw.api.QueryHelper.*;

public class DateTimeTableFiltersTest {

    private DateTimeColumn localDateTimeColumn = DateTimeColumn.create("testing");
    private Table table = Table.create("test");
    private DateTimeColumnReference reference = new DateTimeColumnReference("testing");

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
        table.addColumn(localDateTimeColumn);
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

    @Test
    public void testGetMonthValue() {
        LocalDateTime date = LocalDate.of(2015, 1, 25).atStartOfDay();
        Month[] months = Month.values();

        DateTimeColumn dateTimeColumn = DateTimeColumn.create("test");
        for (int i = 0, monthsLength = months.length; i < monthsLength; i++) {
            dateTimeColumn.append(date);
            date = date.plusMonths(1);
        }
        Table t = Table.create("Test");
        t.addColumn(dateTimeColumn);
        NumberColumn index = DoubleColumn.indexColumn("index", t.rowCount(), 0);
        t.addColumn(index);

        assertTrue(t.selectWhere(dateTimeColumn("test").isInJanuary()).numberColumn("index").contains(0.0));
        assertTrue(t.selectWhere(dateTimeColumn("test").isInFebruary()).numberColumn("index").contains(1.0));
        assertTrue(t.selectWhere(dateTimeColumn("test").isInMarch()).numberColumn("index").contains(2.0));
        assertTrue(t.selectWhere(dateTimeColumn("test").isInApril()).numberColumn("index").contains(3.0));
        assertTrue(t.selectWhere(dateTimeColumn("test").isInMay()).numberColumn("index").contains(4.0));
        assertTrue(t.selectWhere(dateTimeColumn("test").isInJune()).numberColumn("index").contains(5.0));
        assertTrue(t.selectWhere(dateTimeColumn("test").isInJuly()).numberColumn("index").contains(6.0));
        assertTrue(t.selectWhere(dateTimeColumn("test").isInAugust()).numberColumn("index").contains(7.0));
        assertTrue(t.selectWhere(dateTimeColumn("test").isInSeptember()).numberColumn("index").contains(8.0));
        assertTrue(t.selectWhere(dateTimeColumn("test").isInOctober()).numberColumn("index").contains(9.0));
        assertTrue(t.selectWhere(dateTimeColumn("test").isInNovember()).numberColumn("index").contains(10.0));
        assertTrue(t.selectWhere(dateTimeColumn("test").isInDecember()).numberColumn("index").contains(11.0));

        assertTrue(t.selectWhere(dateTimeColumn("test").isInQ1()).nCol("index").contains(2));
        assertTrue(t.selectWhere(dateTimeColumn("test").isInQ2()).nCol("index").contains(4));
        assertTrue(t.selectWhere(dateTimeColumn("test").isInQ3()).nCol("index").contains(8));
        assertTrue(t.selectWhere(dateTimeColumn("test").isInQ4()).nCol("index").contains(11));
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

        NumberColumn index = DoubleColumn.indexColumn("index", dateTimeColumn.size(), 0);
        Table t = Table.create("test", dateTimeColumn, index);

        assertTrue(t.selectWhere(dateTimeColumn("test").isBefore(date)).nCol("index").contains(0));
        assertTrue(t.selectWhere(dateTimeColumn("test").isEqualTo(date)).nCol("index").contains(1));
        assertTrue(t.selectWhere(dateTimeColumn("test").isAfter(date)).nCol("index").contains(2));

        assertTrue(t.selectWhere(dateTimeColumn("test")
                .isBetweenExcluding(beforeDate, afterDate)).nCol("index").contains(1));

        assertTrue(t.selectWhere(dateTimeColumn("test")
                .isBetweenIncluding(beforeDate, afterDate)).nCol("index").contains(2));

        assertTrue(t.selectWhere(dateTimeColumn("test")
                .isBetweenIncluding(beforeDate, afterDate)).nCol("index").contains(0));

        assertFalse(t.selectWhere(dateTimeColumn("test")
                .isBetweenExcluding(beforeDate, afterDate)).nCol("index").contains(2));

        assertFalse(t.selectWhere(dateTimeColumn("test")
                .isBetweenExcluding(beforeDate, afterDate)).nCol("index").contains(0));
    }
}
