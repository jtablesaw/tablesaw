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

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import tech.tablesaw.columns.packeddata.PackedLocalDate;

/**
 * Tests for Date Column
 */
public class DateColumnTest {

    private DateColumn column1;

    @Before
    public void setUp() throws Exception {
        Table table = Table.create("Test");
        column1 = new DateColumn("Game date", Locale.ENGLISH);
        table.addColumn(column1);
    }

    @Test
    public void testAddCell() throws Exception {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/23/1924");
        column1.appendCell("12-May-2015");
        column1.appendCell("12-Jan-2015");
        assertEquals(4, column1.size());
        LocalDate date = LocalDate.now();
        column1.append(date);
        assertEquals(5, column1.size());
    }

    @Test
    public void testDayOfMonth() throws Exception {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/24/1924");
        column1.appendCell("12-May-2015");
        column1.appendCell("14-Jan-2015");
        ShortColumn c2 = column1.dayOfMonth();
        assertEquals(23, c2.get(0));
        assertEquals(24, c2.get(1));
        assertEquals(12, c2.get(2));
        assertEquals(14, c2.get(3));
    }

    @Test
    public void testMonth() throws Exception {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/24/1924");
        column1.appendCell("12-May-2015");
        column1.appendCell("14-Jan-2015");
        ShortColumn c2 = column1.monthValue();
        assertEquals(10, c2.get(0));
        assertEquals(12, c2.get(1));
        assertEquals(5, c2.get(2));
        assertEquals(1, c2.get(3));
    }

    @Test
    public void testYearMonthString() throws Exception {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/24/1924");
        column1.appendCell("12-May-2015");
        column1.appendCell("14-Jan-2015");
        CategoryColumn c2 = column1.yearMonthString();
        assertEquals("2013-10", c2.get(0));
        assertEquals("1924-12", c2.get(1));
        assertEquals("2015-05", c2.get(2));
        assertEquals("2015-01", c2.get(3));
    }

    @Test
    public void testYear() throws Exception {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/24/1924");
        column1.appendCell("12-May-2015");
        ShortColumn c2 = column1.year();
        assertEquals(2013, c2.get(0));
        assertEquals(1924, c2.get(1));
        assertEquals(2015, c2.get(2));
    }

    @Test
    public void testSummary() throws Exception {
        column1.appendCell("2013-10-23");
        column1.appendCell("12/24/1924");
        column1.appendCell("12-May-2015");
        column1.appendCell("14-Jan-2015");
        Table summary = column1.summary();
        assertEquals(4, summary.rowCount());
        assertEquals(2, summary.columnCount());
        assertEquals("Measure", summary.column(0).name());
        assertEquals("Value", summary.column(1).name());
    }

    @Test
    public void testMin() {
        column1.appendInternal(DateColumn.MISSING_VALUE);
        column1.appendCell("2013-10-23");

        LocalDate actual = column1.min();

        assertEquals(PackedLocalDate.asLocalDate(column1.convert("2013-10-23")), actual);
    }

    @Test
    public void testSortOn() {
      Table unsorted = Table.read().csv(
          "Date,1 Yr Treasury Rate\n"
              + "\"01-01-1871\",4.44%\n"
              + "\"01-01-1920\",8.83%\n"
              + "\"01-01-1921\",7.11%\n"
              + "\"01-01-1919\",7.85%\n",
          "1 Yr Treasury Rate");
      Table sorted = unsorted.sortOn("Date");
      assertEquals(
          sorted.dateColumn("Date").asList().stream().sorted().collect(Collectors.toList()),
          sorted.dateColumn("Date").asList());
    }

}
