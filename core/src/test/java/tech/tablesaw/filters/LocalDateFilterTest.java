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
import tech.tablesaw.columns.ColumnReference;
import tech.tablesaw.filtering.datetimes.IsFirstDayOfTheMonth;
import tech.tablesaw.filtering.datetimes.IsInFebruary;
import tech.tablesaw.filtering.datetimes.IsInMarch;
import tech.tablesaw.filtering.datetimes.IsInYear;
import tech.tablesaw.filtering.datetimes.IsLastDayOfTheMonth;
import tech.tablesaw.filtering.datetimes.IsMonday;
import tech.tablesaw.filtering.datetimes.IsSunday;
import tech.tablesaw.util.Selection;

import java.time.LocalDate;

import static org.junit.Assert.*;


public class LocalDateFilterTest {

    private DateColumn localDateColumn = new DateColumn("testing");
    private Table table = Table.create("test");

    @Before
    public void setUp() throws Exception {
        localDateColumn.append(LocalDate.of(2016, 2, 28));
        localDateColumn.append(LocalDate.of(2016, 2, 29));
        localDateColumn.append(LocalDate.of(2016, 3, 1));
        table.addColumn(localDateColumn);
    }

    @Test
    public void testIsSunday() {
        ColumnReference reference = new ColumnReference("testing");
        IsSunday isSunday = new IsSunday(reference);
        Selection selection = isSunday.apply(table);
        assertTrue(selection.contains(0));
        assertFalse(selection.contains(1));
        assertFalse(selection.contains(2));
    }

    @Test
    public void testIsMonday() {
        ColumnReference reference = new ColumnReference("testing");
        IsMonday isSunday = new IsMonday(reference);
        Selection selection = isSunday.apply(table);
        assertFalse(selection.contains(0));
        assertTrue(selection.contains(1));
        assertFalse(selection.contains(2));
    }

    @Test
    public void testIsFebruary() {
        ColumnReference reference = new ColumnReference("testing");
        IsInFebruary isFebruary = new IsInFebruary(reference);
        Selection selection = isFebruary.apply(table);
        assertTrue(selection.contains(0));
        assertTrue(selection.contains(1));
        assertFalse(selection.contains(2));
    }

    @Test
    public void testIsMarch() {
        ColumnReference reference = new ColumnReference("testing");
        IsInMarch result = new IsInMarch(reference);
        Selection selection = result.apply(table);
        assertFalse(selection.contains(0));
        assertFalse(selection.contains(1));
        assertTrue(selection.contains(2));
    }

    @Test
    public void testIsFirstDayOfTheMonth() {
        ColumnReference reference = new ColumnReference("testing");
        IsFirstDayOfTheMonth result = new IsFirstDayOfTheMonth(reference);
        Selection selection = result.apply(table);
        assertFalse(selection.contains(0));
        assertFalse(selection.contains(1));
        assertTrue(selection.contains(2));
    }

    @Test
    public void testIsLastDayOfTheMonth() {
        ColumnReference reference = new ColumnReference("testing");
        IsLastDayOfTheMonth result = new IsLastDayOfTheMonth(reference);
        Selection selection = result.apply(table);
        assertFalse(selection.contains(0));
        assertTrue(selection.contains(1));
        assertFalse(selection.contains(2));
    }

    @Test
    public void testIsInYear() {
        ColumnReference reference = new ColumnReference("testing");
        IsInYear result = new IsInYear(reference, 2016);
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
