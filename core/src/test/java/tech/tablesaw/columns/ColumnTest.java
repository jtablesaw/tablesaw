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

package tech.tablesaw.columns;

import org.junit.Before;
import org.junit.Test;

import tech.tablesaw.api.CategoryColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static tech.tablesaw.api.ColumnType.*;

/**
 * Tests for Column functionality that is common across column types
 */
public class ColumnTest {

    private static final ColumnType[] types = {
            LOCAL_DATE,     // date of poll
            INTEGER,        // approval rating (pct)
            CATEGORY             // polling org
    };

    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/BushApproval.csv").columnTypes(types));
    }

    @Test
    public void testFirst() throws Exception {
        // test with dates
        DateColumn first = (DateColumn) table.dateColumn("date").first(3);
        assertEquals(LocalDate.parse("2004-02-04"), first.get(0));
        assertEquals(LocalDate.parse("2004-01-21"), first.get(1));
        assertEquals(LocalDate.parse("2004-01-07"), first.get(2));

        // test with ints
        IntColumn first2 = (IntColumn) table.intColumn("approval").first(3);
        assertEquals(53, first2.get(0));
        assertEquals(53, first2.get(1));
        assertEquals(58, first2.get(2));

        // test with categories
        CategoryColumn first3 = (CategoryColumn) table.categoryColumn("who").first(3);
        assertEquals("fox", first3.get(0));
        assertEquals("fox", first3.get(1));
        assertEquals("fox", first3.get(2));
    }

    @Test
    public void testLast() throws Exception {

        // test with dates
        DateColumn last = (DateColumn) table.dateColumn("date").last(3);
        assertEquals(LocalDate.parse("2001-03-27"), last.get(0));
        assertEquals(LocalDate.parse("2001-02-27"), last.get(1));
        assertEquals(LocalDate.parse("2001-02-09"), last.get(2));

        // test with ints
        IntColumn last2 = (IntColumn) table.intColumn("approval").last(3);
        assertEquals(52, last2.get(0));
        assertEquals(53, last2.get(1));
        assertEquals(57, last2.get(2));

        // test with categories
        CategoryColumn last3 = (CategoryColumn) table.categoryColumn("who").last(3);
        assertEquals("zogby", last3.get(0));
        assertEquals("zogby", last3.get(1));
        assertEquals("zogby", last3.get(2));
    }

    @Test
    public void testName() throws Exception {
        Column c = table.intColumn("approval");
        assertEquals("approval", c.name());
    }

    @Test
    public void testComment() throws Exception {
        Column c = table.intColumn("approval");
        c.setComment("Dumb comment");
        assertEquals("Dumb comment", c.comment());
    }

    @Test
    public void testType() throws Exception {
        Column c = table.intColumn("approval");
        assertEquals(INTEGER, c.type());
    }
}
