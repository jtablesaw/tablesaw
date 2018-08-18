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

import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.DoubleColumnType;
import tech.tablesaw.selection.Selection;

import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

import static tech.tablesaw.api.TimeColumn.MISSING_VALUE;
import static tech.tablesaw.columns.times.PackedLocalTime.*;
import static org.junit.Assert.*;

public class TimeColumnTest {

    private TimeColumn column1;

    @Before
    public void setUp() {
        Table table = Table.create("Test");
        column1 = TimeColumn.create("Game time");
        table.addColumns(column1);
    }

    @Test
    public void testMaxAndMin() {
        column1.appendCell("05:15:30");
        column1.appendCell("10:15:30");
        column1.appendCell("07:04:02");
        assertEquals(LocalTime.of(5, 15, 30), column1.min());
        assertEquals(LocalTime.of(10, 15, 30), column1.max());
    }

    @Test
    public void testContains() {
        column1.appendCell("05:15:30");
        column1.appendCell("10:15:30");
        column1.appendCell("07:04:02");
        assertTrue(column1.contains(LocalTime.of(5, 15, 30)));
        assertTrue(column1.contains(LocalTime.of(10, 15, 30)));
        assertFalse(column1.contains(LocalTime.of(9, 15, 30)));
    }

    @Test
    public void testTopAndBottom() {
        fillLargerColumn();

        List<LocalTime> top = column1.top(3);
        List<LocalTime> bottom = column1.bottom(3);

        assertTrue(bottom.contains(LocalTime.of(0, 4, 2)));
        assertTrue(bottom.contains(LocalTime.of(3, 6, 2)));
        assertTrue(bottom.contains(LocalTime.of(4, 4, 2)));
        assertEquals(3, bottom.size());

        assertTrue(top.contains(LocalTime.of(18, 4, 2)));
        assertTrue(top.contains(LocalTime.of(14, 4, 2)));
        assertTrue(top.contains(LocalTime.of(15, 4, 2)));
        assertEquals(3, top.size());
    }

    @Test
    public void testSorting() {
        fillLargerColumn();

        List<LocalTime> top = column1.top(3);

        column1.sortAscending();
        Column<?> first = column1.first(3);
        TimeColumn timeColumn = (TimeColumn) first;
        List<LocalTime> sortedA = timeColumn.asList();

        column1.sortDescending();
        List<LocalTime> sortedD = ((TimeColumn) column1.first(3)).asList();

        assertEquals(null, sortedA.get(0));
        assertEquals(LocalTime.of(0, 4, 2), sortedA.get(1));
        assertEquals(LocalTime.of(3, 6, 2), sortedA.get(2));
        assertEquals(top, sortedD);
    }

    @Test
    public void testAppendColumn() {
        column1.appendInternal(of(5,15,30));
        column1.appendInternal(of(10, 15, 30));
        column1.appendInternal(of(7, 4, 2));
        column1.appendInternal(of(4, 4, 2));
        column1.appendInternal(of(18, 4, 2));

        TimeColumn column2 = TimeColumn.create("TC2");
        column2.appendInternal(of(15, 4, 2));
        column2.appendInternal(of(14, 4, 2));
        column2.appendInternal(of(0, 4, 2));
        column2.appendInternal(of(3, 6, 2));
        column2.appendInternal(of(11, 4, 2));

        column1.append(column2);

        assertEquals(10, column1.size());
        assertTrue(column1.contains(LocalTime.of(14, 4, 2)));
    }

    @Test
    public void testAppendCell() {
        column1.appendCell("10:15:30");
        column1.appendCell("11:30:00");
        column1.appendCell("14:00:00");
        column1.appendCell("18:15:30");
        assertEquals(4, column1.size());
    }

    @Test
    public void testSet() {
        column1.appendCell("10:15:30");
        column1.appendCell("11:30:00");
        column1.appendCell("14:00:00");
        column1.appendCell("18:15:30");
        column1.set(column1.isBeforeNoon(), LocalTime.NOON);
        assertEquals(LocalTime.NOON, column1.get(0));
        assertEquals(LocalTime.NOON, column1.get(1));
        assertNotEquals(LocalTime.NOON, column1.get(2));
        assertNotEquals(LocalTime.NOON, column1.get(3));
    }

    @Test
    public void testAsSet() {
        column1.appendCell("10:15:30");
        column1.appendCell("11:30:00");
        column1.appendCell("14:00:00");
        column1.appendCell("18:15:30");
        column1.appendInternal(of(12,0, 0));
        column1.appendInternal(of(12,0, 0));
        Set<LocalTime> set = column1.asSet();
        assertEquals(column1.size(), set.size() + 1);
    }

    @Test
    public void testAppendCell2() {
        column1.appendCell("12:18:03 AM");
        column1.appendCell("8:18:03 AM");
        column1.appendCell("12:18:03 AM");
        assertEquals(3, column1.size());
    }

    @Test
    public void copy() {
        fillLargerColumn();
        TimeColumn column2 = column1.copy();
        for (int i = 0; i < column1.size(); i++) {
            assertEquals(column2.getIntInternal(i), column1.getIntInternal(i));
        }
        assertEquals(column1.name(), column2.name());
    }

    @Test
    public void clear() {
        fillLargerColumn();
        assertEquals(11, column1.size());
        column1.clear();
        assertEquals(0, column1.size());
    }

    @Test
    public void summary() {
        fillLargerColumn();
        Table t = column1.summary();
        assertEquals("11", t.get(0, "Value"));
        assertEquals("1", t.get(1, "Value"));
        assertEquals("00:04:02", t.get(2, "Value"));
        assertEquals("18:04:02", t.get(3, "Value"));
    }

    @Test
    public void asBytesAndByteSize() {
        fillLargerColumn();
        assertEquals(4, column1.byteSize());
        assertEquals(column1.getPackedTime(0), ByteBuffer.wrap(column1.asBytes(0)).getInt());
    }

    @Test
    public void countMissing() {
        fillLargerColumn();
        column1.appendInternal(MISSING_VALUE);
        column1.appendInternal(MISSING_VALUE);
        assertEquals(3, column1.countMissing());
    }

    @Test
    public void isMissingIsNotMissing() {
        fillLargerColumn();
        column1.appendInternal(MISSING_VALUE);
        column1.appendInternal(MISSING_VALUE);
        Selection s = column1.isMissing();
        assertEquals(3, s.size());
        Selection s2 = column1.isNotMissing();
        assertEquals(10, s2.size());
    }

    @Test
    public void countUnique() {
        fillLargerColumn();
        column1.appendInternal(MISSING_VALUE);
        assertEquals(10, column1.countUnique());
    }

    @Test
    public void lag() {
        fillLargerColumn();
        TimeColumn column2 = column1.lag(2);
        Table t = Table.create("t");
        t.addColumns(column1, column2);
        for (int i = 0; i < column1.size() - 2; i++) {
            assertEquals(column2.getIntInternal(i+2), column1.getIntInternal(i));
        }
    }

    @Test
    public void lead() {
        fillLargerColumn();
        TimeColumn column2 = column1.lead(2);
        Table t = Table.create("t");
        t.addColumns(column1, column2);
        for (int i = 0; i < column1.size() - 2; i++) {
            assertEquals(column2.getIntInternal(i), column1.getIntInternal(i + 2));
        }
    }

    @Test
    public void minuteOfDay() {
        fillLargerColumn();
        DoubleColumn column2 = column1.minuteOfDay();
        for (int i = 0; i < column1.size() - 2; i++) {
            assertEquals(column2.get(i), getMinuteOfDay(column1.getPackedTime(i)), 0.0001);
        }
    }

    @Test
    public void secondOfDay() {
        fillLargerColumn();
        DoubleColumn column2 = column1.secondOfDay();
        for (int i = 0; i < column1.size() - 2; i++) {
            assertEquals(column2.get(i), getSecondOfDay(column1.getPackedTime(i)), 0.0001);
        }
    }

    @Test
    public void testPlusHours() {
        fillColumn();
        TimeColumn column2 = column1.plusHours(3);
        DoubleColumn numberColumn = column2.differenceInHours(column1);
        double expected = -3;
        check(numberColumn, expected);
    }

    @Test
    public void testTruncatedTo() {
        fillColumn();

        TimeColumn column2 = column1.truncatedTo(ChronoUnit.HOURS);

        assertEquals(column1.get(0).getHour(), column2.get(0).getHour());
        assertEquals(0, column2.get(0).getMinute());
        assertEquals(0, column2.get(0).getSecond());
        assertEquals(0, column2.get(0).getNano());
        assertEquals(MISSING_VALUE, column2.getIntInternal(2));
    }

    @Test
    public void testWithHour() {
        fillColumn();
        TimeColumn column2 = column1.withHour(3);
        assertEquals(3, column2.hour().min(), 0.001);
        assertEquals(3, column2.hour().max(), 0.001);
    }

    @Test
    public void testWithMinute() {
        fillColumn();
        TimeColumn column2 = column1.withMinute(3);
        assertEquals(3, column2.minute().min(), 0.001);
        assertEquals(3, column2.minute().max(), 0.001);
    }

    @Test
    public void testWithSecond() {
        fillColumn();
        TimeColumn column2 = column1.withSecond(3);
        assertEquals(3, column2.second().min(), 0.001);
        assertEquals(3, column2.second().max(), 0.001);
    }

    @Test
    public void testSecond() {
        fillColumn();
        DoubleColumn second = column1.second();
        assertEquals(2, second.get(0), 0.001);
        assertEquals(30, second.get(1), 0.001);
        assertEquals(DoubleColumnType.missingValueIndicator(), second.get(2), 0.001);
    }

    @Test
    public void testMinute() {
        fillColumn();
        DoubleColumn minute = column1.minute();
        assertEquals(4, minute.get(0), 0.001);
        assertEquals(15, minute.get(1), 0.001);
        assertEquals(DoubleColumnType.missingValueIndicator(), minute.get(2), 0.001);
    }

    @Test
    public void testWithMillisecond() {
        fillColumn();
        TimeColumn column2 = column1.withMillisecond(3);
        assertEquals(3, column2.milliseconds().min(), 0.001);
        assertEquals(3, column2.milliseconds().max(), 0.001);
    }

    @Test
    public void testMinusHours() {
        fillColumn();
        TimeColumn column2 = column1.minusHours(0);
        DoubleColumn numberColumn = column2.differenceInHours(column1);
        double expected = 0;
        check(numberColumn, expected);
    }

    @Test
    public void testPlusMinutes() {
        fillColumn();
        TimeColumn column2 = column1.plusMinutes(30);
        DoubleColumn numberColumn = column2.differenceInMinutes(column1);
        double expected = -30;
        check(numberColumn, expected);
    }

    @Test
    public void testMinusMinutes() {
        fillColumn();
        TimeColumn column2 = column1.minusMinutes(30);
        DoubleColumn numberColumn = column2.differenceInMinutes(column1);
        double expected = 30;
        check(numberColumn, expected);
    }

    @Test
    public void testPlusSeconds() {
        fillColumn();
        TimeColumn column2 = column1.plusSeconds(101);
        DoubleColumn numberColumn = column2.differenceInSeconds(column1);
        double expected = -101;
        check(numberColumn, expected);
    }

    @Test
    public void testMinusSeconds() {
        fillColumn();
        TimeColumn column2 = column1.minusSeconds(101);
        DoubleColumn numberColumn = column2.differenceInSeconds(column1);
        double expected = 101;
        check(numberColumn, expected);
    }

    @Test
    public void testPlusMilliseconds() {
        fillColumn();
        TimeColumn column2 = column1.plusMilliseconds(101);
        DoubleColumn numberColumn = column2.differenceInMilliseconds(column1);
        double expected = -101;
        check(numberColumn, expected);
    }

    @Test
    public void testMinusMilliseconds() {
        fillColumn();
        TimeColumn column2 = column1.minusMilliseconds(101);
        DoubleColumn numberColumn = column2.differenceInMilliseconds(column1);
        double expected = 101;
        check(numberColumn, expected);
    }

    @Test
    public void testNull() {
        TimeColumn col = TimeColumn.create("Game time");
        col.appendCell(null);
        assertNull(col.get(0));
    }

    private void check(DoubleColumn numberColumn, double expected) {
        assertEquals(expected, numberColumn.min(), .0001);
        assertEquals(expected, numberColumn.max(), .0001);
    }

    private void fillColumn() {
        column1.appendCell("07:04:02");
        column1.appendCell("10:15:30");
        column1.appendCell("");
    }

    private void fillLargerColumn() {
        column1.appendInternal(of(5,15,30));
        column1.appendInternal(of(10, 15, 30));
        column1.appendInternal(of(7, 4, 2));
        column1.appendInternal(of(4, 4, 2));
        column1.appendCell("");
        column1.appendInternal(of(18, 4, 2));
        column1.appendInternal(of(15, 4, 2));
        column1.appendInternal(of(14, 4, 2));
        column1.appendInternal(of(0, 4, 2));
        column1.appendInternal(of(3, 6, 2));
        column1.appendInternal(of(11, 4, 2));
    }
}
