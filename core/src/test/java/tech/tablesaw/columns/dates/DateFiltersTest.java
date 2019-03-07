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

package tech.tablesaw.columns.dates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.tablesaw.columns.dates.PackedLocalDate.asLocalDate;
import static tech.tablesaw.columns.dates.PackedLocalDate.minusDays;
import static tech.tablesaw.columns.dates.PackedLocalDate.pack;
import static tech.tablesaw.columns.dates.PackedLocalDate.plusDays;


public class DateFiltersTest {

    private DateColumn localDateColumn = DateColumn.create("testing");
    private Table table = Table.create("test");

    @BeforeEach
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
        table.addColumns(localDateColumn);
    }

    @Test
    public void testDow() {

        assertTrue(localDateColumn.isSunday().contains(0));
        assertTrue(localDateColumn.isMonday().contains(1));
        assertTrue(localDateColumn.isTuesday().contains(2));
        assertTrue(localDateColumn.isWednesday().contains(3));
        assertTrue(localDateColumn.isThursday().contains(4));
        assertTrue(localDateColumn.isFriday().contains(7));
        assertTrue(localDateColumn.isSaturday().contains(8));
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

        selection = localDateColumn.isInYear(2015);
        assertFalse(selection.contains(0));
        assertFalse(selection.contains(1));
        assertFalse(selection.contains(2));
    }

    @Test
    public void testGetMonthValue() {
        LocalDate date = LocalDate.of(2015, 1, 25);
        Month[] months = Month.values();

        DateColumn dateColumn = DateColumn.create("test");
        for (int i = 0; i < months.length; i++) {
            dateColumn.append(date);
            date = date.plusMonths(1);
        }

        StringColumn month = dateColumn.month();
        IntColumn monthValue = dateColumn.monthValue();

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

        Table t = Table.create("Test");
        t.addColumns(dateColumn);
        IntColumn index = IntColumn.indexColumn("index", t.rowCount(), 0);
        t.addColumns(index);

        assertTrue(t.where(t.dateColumn("test").isInJanuary()).intColumn("index").contains(0));
        assertTrue(t.where(t.dateColumn("test").isInFebruary()).intColumn("index").contains(1));
        assertTrue(t.where(t.dateColumn("test").isInMarch()).intColumn("index").contains(2));
        assertTrue(t.where(t.dateColumn("test").isInApril()).intColumn("index").contains(3));
        assertTrue(t.where(t.dateColumn("test").isInMay()).intColumn("index").contains(4));
        assertTrue(t.where(t.dateColumn("test").isInJune()).intColumn("index").contains(5));
        assertTrue(t.where(t.dateColumn("test").isInJuly()).intColumn("index").contains(6));
        assertTrue(t.where(t.dateColumn("test").isInAugust()).intColumn("index").contains(7));
        assertTrue(t.where(t.dateColumn("test").isInSeptember()).intColumn("index").contains(8));
        assertTrue(t.where(t.dateColumn("test").isInOctober()).intColumn("index").contains(9));
        assertTrue(t.where(t.dateColumn("test").isInNovember()).intColumn("index").contains(10));
        assertTrue(t.where(t.dateColumn("test").isInDecember()).intColumn("index").contains(11));

        assertTrue(t.where(t.dateColumn("test").isInQ1()).intColumn("index").contains(2));
        assertTrue(t.where(t.dateColumn("test").isInQ2()).intColumn("index").contains(4));
        assertTrue(t.where(t.dateColumn("test").isInQ3()).intColumn("index").contains(8));
        assertTrue(t.where(t.dateColumn("test").isInQ4()).intColumn("index").contains(11));
    }

    @Test
    public void testComparison() {
        LocalDate date = LocalDate.of(2015, 1, 25);
        int packed = pack(date);

        DateColumn dateColumn = DateColumn.create("test");

        int before = minusDays(1, packed);
        int after = plusDays(1, packed);

        LocalDate beforeDate = asLocalDate(before);
        LocalDate afterDate = asLocalDate(after);

        dateColumn.appendInternal(before);
        dateColumn.appendInternal(packed);
        dateColumn.appendInternal(after);

        assertTrue(dateColumn.isBefore(packed).contains(0));
        assertTrue(dateColumn.isBefore(date).contains(0));
        assertTrue(dateColumn.isOnOrBefore(date).contains(0));

        assertTrue(dateColumn.isEqualTo(packed).contains(1));
        assertTrue(dateColumn.isEqualTo(date).contains(1));
        assertTrue(dateColumn.isOnOrBefore(date).contains(1));
        assertTrue(dateColumn.isOnOrAfter(date).contains(1));

        assertFalse(dateColumn.isOnOrBefore(date).contains(2));
        assertTrue(dateColumn.isAfter(packed).contains(2));
        assertTrue(dateColumn.isAfter(date).contains(2));
        assertTrue(dateColumn.isOnOrAfter(date).contains(2));

        assertTrue(dateColumn.isBetweenExcluding(beforeDate, afterDate).contains(1));
        assertTrue(dateColumn.isBetweenIncluding(beforeDate, afterDate).contains(2));
        assertTrue(dateColumn.isBetweenIncluding(beforeDate, afterDate).contains(0));
        assertFalse(dateColumn.isBetweenExcluding(beforeDate, afterDate).contains(2));
        assertFalse(dateColumn.isBetweenExcluding(beforeDate, afterDate).contains(0));

        IntColumn index = IntColumn.indexColumn("index", dateColumn.size(), 0);
        Table t = Table.create("test", dateColumn, index);

        assertTrue(t.where(dateColumn.isBefore(packed)).intColumn("index").contains(0));
        assertTrue(t.where(dateColumn.isEqualTo(packed)).intColumn("index").contains(1));
        assertTrue(t.where(dateColumn.isAfter(packed)).intColumn("index").contains(2));
        assertTrue(t.where(t.dateColumn("test")
                .isBetweenExcluding(beforeDate, afterDate)).intColumn("index").contains(1));
        assertTrue(t.where(t.dateColumn("test")
                .isBetweenIncluding(beforeDate, afterDate)).intColumn("index").contains(2));
        assertTrue(t.where(t.dateColumn("test")
                .isBetweenIncluding(beforeDate, afterDate)).intColumn("index").contains(0));
        assertFalse(t.where(t.dateColumn("test")
                .isBetweenExcluding(beforeDate, afterDate)).intColumn("index").contains(2));
        assertFalse(t.where(t.dateColumn("test")
                .isBetweenExcluding(beforeDate, afterDate)).intColumn("index").contains(0));
    }

    @Test
    public void testIsMissing() {
        DateColumn column = DateColumn.create("test");
        column.append(LocalDate.now());
        column.appendInternal(DateColumnType.missingValueIndicator());

        assertTrue(column.isMissing().contains(1));
        assertTrue(column.isNotMissing().contains(0));
        assertTrue(column.isNotMissing().contains(0));
        assertTrue(column.isMissing().contains(1));
    }

    @Test
    public void testColumnComparisons() {
        LocalDate dateTime = LocalDate.of(2015, 1, 25);
        DateColumn dateColumn = DateColumn.create("test");

        LocalDate beforeDate = dateTime.minusDays(1);
        LocalDate afterDate = dateTime.plusDays(1);

        dateColumn.append(beforeDate);
        dateColumn.append(dateTime);
        dateColumn.append(afterDate);

        DateColumn same = DateColumn.create("same");
        same.append(beforeDate);
        same.append(dateTime);
        same.append(afterDate);

        DateColumn before = DateColumn.create("before");
        before.append(beforeDate.minusDays(1));
        before.append(dateTime.minusDays(1));
        before.append(afterDate.minusDays(1));

        DateColumn after = DateColumn.create("after");
        after.append(beforeDate.plusDays(1));
        after.append(dateTime.plusDays(1));
        after.append(afterDate.plusDays(1));

        Table t = Table.create("test", dateColumn, same, before, after);

        assertTrue(dateColumn.isOnOrAfter(same).contains(0));
        assertTrue(t.dateColumn("test").isOnOrAfter(same).contains(0));
        assertTrue(t.dateColumn("test").isOnOrAfter(t.dateColumn("same")).contains(0));

        assertTrue(dateColumn.isOnOrBefore(same).contains(0));
        assertTrue(t.dateColumn("test").isOnOrBefore(same).contains(0));
        assertTrue(t.dateColumn("test").isOnOrBefore(t.dateColumn("same")).contains(0));

        assertTrue(dateColumn.isEqualTo(same).contains(0));
        assertTrue(t.dateColumn("test").isEqualTo(same).contains(0));
        assertTrue(t.dateColumn("test").isEqualTo(t.dateColumn("same")).contains(0));

        assertTrue(dateColumn.isBefore(after).contains(0));
        assertFalse(dateColumn.isOnOrAfter(after).contains(0));
        assertTrue(t.dateColumn("test").isBefore(after).contains(0));
        assertTrue(t.dateColumn("test").isBefore(t.dateColumn("after")).contains(0));

        assertTrue(dateColumn.isAfter(before).contains(0));
        assertFalse(dateColumn.isOnOrBefore(before).contains(0));
        assertTrue(t.dateColumn("test").isAfter(before).contains(0));
        assertTrue(t.dateColumn("test").isAfter(t.dateColumn("before")).contains(0));

        assertFalse(dateColumn.isNotEqualTo(same).contains(0));
        assertFalse(t.dateColumn("test").isNotEqualTo(same).contains(0));
        assertFalse(t.dateColumn("test").isNotEqualTo(t.dateColumn("same")).contains(0));

        assertTrue(dateColumn.isOnOrBefore(same).contains(0));
        assertTrue(dateColumn.isOnOrBefore(after).contains(0));
        assertFalse(dateColumn.isOnOrBefore(before).contains(0));
        assertTrue(dateColumn.isNotEqualTo(before).contains(0));

        assertTrue(dateColumn.isOnOrAfter(same).contains(1));
        assertTrue(dateColumn.isOnOrAfter(before).contains(2));
        assertFalse(dateColumn.isOnOrAfter(after).contains(2));
        assertTrue(dateColumn.isNotEqualTo(after).contains(0));

/*
        assertTrue(t.dateColumn("test")
                .isOnOrAfter(t.dateColumn("same")).contains(0));
        assertTrue(t.dateColumn("test")
                .isOnOrAfter(same).contains(0));

        assertFalse(t.dateColumn("test")
                .isOnOrAfter(t.dateColumn("after")).contains(0));
        assertFalse(t.dateColumn("test")
                .isOnOrAfter(after).contains(0));

        assertTrue(t.dateColumn("test")
                .isOnOrBefore(t.dateColumn("same")).contains(0));
        assertTrue(t.dateColumn("test")
                .isOnOrBefore(same).contains(0));

        assertTrue(t.dateColumn("test")
                .isOnOrBefore(t.dateColumn("after")).contains(0));
        assertTrue(t.dateColumn("test")
                .isOnOrBefore(after).contains(0));

        assertFalse(t.dateColumn("test")
                .isOnOrBefore(t.dateColumn("before")).contains(0));
        assertFalse(t.dateColumn("test")
                .isOnOrBefore(before).contains(0));
*/

        assertTrue(t.dateColumn("test")
                .isNotEqualTo(t.dateColumn("before")).contains(0));
        assertTrue(t.dateColumn("test")
                .isNotEqualTo(before).contains(0));
        assertFalse(t.dateColumn("test")
                .isNotEqualTo(t.dateColumn("same")).contains(0));
        assertFalse(t.dateColumn("test")
                .isNotEqualTo(same).contains(0));

    }
}
