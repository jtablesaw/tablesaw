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

package tech.tablesaw.columns.times;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.tablesaw.columns.times.PackedLocalTime.of;
import static tech.tablesaw.columns.times.PackedLocalTime.pack;
import static tech.tablesaw.columns.times.PackedLocalTime.toShortTimeString;

import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.selection.Selection;

public class LocalTimeFilterTest {

    private TimeColumn localTimeColumn = TimeColumn.create("testing");
    private TimeColumn column1 = TimeColumn.create("Game time");

    private Table table = Table.create("test");

    @BeforeEach
    public void setUp() {
        localTimeColumn.appendInternal(of(10, 2, 28));
        localTimeColumn.appendInternal(of(12, 0, 0));
        localTimeColumn.appendInternal(of(20, 3, 1));
        localTimeColumn.appendInternal(pack(LocalTime.MIDNIGHT));
        table.addColumns(localTimeColumn);
    }

    @Test
    public void testColumnEquality() {
        TimeColumn column1 = localTimeColumn.copy();
        column1.setName("copy");
        table.addColumns(column1);
        Selection selection = localTimeColumn.isEqualTo(column1);
        assertTrue(selection.contains(0));
        selection = localTimeColumn.isEqualTo(column1);
        assertTrue(selection.contains(0));
    }

    @Test
    public void testColumnInEquality() {
        TimeColumn column1 = localTimeColumn.copy();
        column1.setName("copy");
        table.addColumns(column1);
        Selection selection = localTimeColumn.isNotEqualTo(column1);
        assertFalse(selection.contains(0));
        selection = localTimeColumn.isNotEqualTo(column1);
        assertFalse(selection.contains(0));
    }

    @Test
    public void testColumnIsBefore() {
        TimeColumn column1 = localTimeColumn.copy();
        column1.setName("copy");

        TimeColumn before = localTimeColumn.minusHours(1);
        TimeColumn after = localTimeColumn.plusHours(1);

        table.addColumns(column1, before, after);

        Selection selection = localTimeColumn.isBefore(column1);
        assertFalse(selection.contains(0));
        selection = localTimeColumn.isAfter(column1);
        assertFalse(selection.contains(0));

        selection = localTimeColumn.isBefore(after);
        assertTrue(selection.contains(0));
        selection = localTimeColumn.isBefore(after);
        assertTrue(selection.contains(0));
    }

    @Test
    public void testColumnIsAfter() {
        TimeColumn column1 = localTimeColumn.copy();
        column1.setName("copy");

        TimeColumn before = localTimeColumn.minusHours(1);
        TimeColumn after = localTimeColumn.plusHours(1);

        table.addColumns(column1, before, after);

        Selection selection = localTimeColumn.isBefore(column1);
        assertFalse(selection.contains(0));
        selection = localTimeColumn.isAfter(column1);
        assertFalse(selection.contains(0));

        selection = localTimeColumn.isAfter(before);
        assertTrue(selection.contains(0));
        selection = localTimeColumn.isAfter(before);
        assertTrue(selection.contains(0));
    }

    @Test
    public void testIsAM() {
        Selection selection = localTimeColumn.isBeforeNoon();
        assertTrue(selection.contains(0));
        assertFalse(selection.contains(1));
        assertFalse(selection.contains(2));
        assertTrue(selection.contains(3));
    }

    @Test
    public void testIsAM2() {
        Selection selection = localTimeColumn.isBeforeNoon();
        assertTrue(selection.contains(0));
        assertFalse(selection.contains(1));
        assertFalse(selection.contains(2));
        assertTrue(selection.contains(3));
    }

    @Test
    public void testIsPM() {
        Selection selection = localTimeColumn.isAfterNoon();
        assertFalse(selection.contains(0));
        assertTrue(selection.contains(1));
        assertTrue(selection.contains(2));
    }

    @Test
    public void testIsPM2() {
        Selection selection = localTimeColumn.isAfterNoon();
        assertFalse(selection.contains(0));
        assertTrue(selection.contains(1));
        assertTrue(selection.contains(2));
    }

    @Test
    public void testIsMidnightIsNoon() {
        Selection selection = localTimeColumn.isMidnight();
        assertFalse(selection.contains(0));
        assertFalse(selection.contains(1));
        assertFalse(selection.contains(2));
        assertTrue(selection.contains(3));
    }

    @Test
    public void testIsNoon() {
        Selection selection = localTimeColumn.isNoon();
        assertFalse(selection.contains(0));
        assertTrue(selection.contains(1));
        assertFalse(selection.contains(2));
        assertFalse(selection.contains(3));
    }

    @Test
    public void testIsMidnight2() {
        Selection selection = localTimeColumn.isMidnight();
        assertFalse(selection.contains(0));
        assertFalse(selection.contains(1));
        assertFalse(selection.contains(2));
        assertTrue(selection.contains(3));
    }

    @Test
    public void testIsNoon2() {
        Selection selection = localTimeColumn.isNoon();
        assertFalse(selection.contains(0));
        assertTrue(selection.contains(1));
        assertFalse(selection.contains(2));
        assertFalse(selection.contains(3));
    }

    @Test
    public void testAfter() {
        Table t = Table.create("test");
        t.addColumns(column1);
        column1.appendCell("05:15:30");
        column1.appendCell("10:15:30");
        Table result = t.where(t.timeColumn("Game time")
                .isAfter(LocalTime.of(7, 4, 2, 0)));
        assertEquals(result.rowCount(), 1);
    }

    @Test
    public void testAfter2() {
        column1.appendCell("05:15:30");
        column1.appendCell("10:15:30");
        Selection result = column1.isAfter(LocalTime.of(7, 4, 2, 0));
        assertEquals(result.size(), 1);
        assertEquals(1, result.get(0));
    }

    @Test
    public void testEqual() {
        Table t = Table.create("test");
        t.addColumns(column1);
        fillColumn();
        Table result = t.where(t.timeColumn("Game time")
                .isEqualTo(LocalTime.of(7, 4, 2, 0)));
        assertEquals(result.rowCount(), 1);
        assertEquals(result.getUnformatted(0, 0), toShortTimeString(pack(LocalTime.of(7, 4, 2))));
    }

    @Test
    public void testNotEqual() {
        Table t = Table.create("test");
        t.addColumns(column1);
        fillColumn();
        Table result = t.where(t.timeColumn("Game time")
                .isNotEqualTo(LocalTime.of(7, 4, 2, 0)));
        assertEquals(result.rowCount(), 1);
        assertNotEquals(result.get(0, 0), toShortTimeString(pack(LocalTime.of(7, 4, 2))));
    }

    @Test
    public void testEqual2() {
        column1.appendCell("05:15:30");
        column1.appendCell("10:15:30");
        Selection result = column1.isEqualTo(LocalTime.of(5, 15, 30, 0));
        assertEquals(result.size(), 1);
        assertEquals(0, result.get(0));
    }

    @Test
    public void testNotEqual2() {
        column1.appendCell("05:15:30");
        column1.appendCell("10:15:30");
        Selection result = column1.isNotEqualTo(LocalTime.of(5, 15, 30, 0));
        assertEquals(result.size(), 1);
        assertEquals(1, result.get(0));
    }

    @Test
    public void testBefore() {
        Table t = Table.create("test");
        t.addColumns(column1);
        column1.appendCell("05:15:30");
        column1.appendCell("10:15:30");
        Table result = t.where(t.timeColumn("Game time")
                .isBefore(LocalTime.of(7, 4, 2, 0)));
        assertEquals(result.rowCount(), 1);
        assertEquals(LocalTime.of(5, 15, 30), column1.get(0));
    }

    @Test
    public void testBefore2() {
        column1.appendCell("05:15:30");
        column1.appendCell("10:15:30");
        Selection result = column1.isBefore(LocalTime.of(7, 4, 2, 0));
        assertEquals(result.size(), 1);
        assertEquals(0, result.get(0));
    }

    @Test
    public void testOnOrAfter() {
        Table t = Table.create("test");
        t.addColumns(column1);
        fillColumn();
        Table result = t.where(t.timeColumn("Game time")
                .isOnOrAfter(LocalTime.of(7, 4, 2, 0)));
        assertEquals(result.rowCount(), 2);
    }

    @Test
    public void testOnOrBefore() {
        Table t = Table.create("test");
        t.addColumns(column1);
        fillColumn();
        Table result = t.where(t.timeColumn("Game time")
                .isOnOrBefore(LocalTime.of(7, 4, 2, 0)));
        assertEquals(result.rowCount(), 1);
        assertEquals(result.getUnformatted(0, 0), toShortTimeString(pack(LocalTime.of(7, 4, 2))));
    }

    @Test
    public void testOnOrBefore2() {
        fillColumn();
        Selection result = column1.isOnOrBefore(LocalTime.of(7, 4, 2, 0));
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), 0);
    }

    @Test
    public void testOnOrAfter2() {
        fillColumn();
        Selection selection = column1.isOnOrAfter(LocalTime.of(7, 4, 2, 0));
        assertEquals(selection.size(), 2);
    }

    private void fillColumn() {
        column1.appendCell("07:04:02");
        column1.appendCell("10:15:30");
    }
}
