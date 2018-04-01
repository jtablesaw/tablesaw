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

import tech.tablesaw.api.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

/**
 * Tests for Column functionality that is common across column types
 */
public class ColumnTest {

    private static final ColumnType[] types = {
            ColumnType.LOCAL_DATE,     // date of poll
            ColumnType.NUMBER,        // approval rating (pct)
            ColumnType.STRING             // polling org
    };

    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").columnTypes(types));
    }

    @Test
    public void testFirst() {
        // test with dates
        DateColumn first = (DateColumn) table.dateColumn("date").first(3);
        assertEquals(LocalDate.parse("2004-02-04"), first.get(0));
        assertEquals(LocalDate.parse("2004-01-21"), first.get(1));
        assertEquals(LocalDate.parse("2004-01-07"), first.get(2));

        // test with ints
        NumberColumn first2 = (NumberColumn) table.numberColumn("approval").first(3);
        assertEquals(53, first2.get(0), 0.0001);
        assertEquals(53, first2.get(1), 0.0001);
        assertEquals(58, first2.get(2), 0.0001);

        // test with categories
        StringColumn first3 = (StringColumn) table.stringColumn("who").first(3);
        assertEquals("fox", first3.get(0));
        assertEquals("fox", first3.get(1));
        assertEquals("fox", first3.get(2));
    }

    @Test
    public void testLast() {

        // test with dates
        DateColumn last = (DateColumn) table.dateColumn("date").last(3);
        assertEquals(LocalDate.parse("2001-03-27"), last.get(0));
        assertEquals(LocalDate.parse("2001-02-27"), last.get(1));
        assertEquals(LocalDate.parse("2001-02-09"), last.get(2));

        // test with ints
        NumberColumn last2 = (NumberColumn) table.numberColumn("approval").last(3);
        assertEquals(52, last2.get(0), 0.0001);
        assertEquals(53, last2.get(1), 0.0001);
        assertEquals(57, last2.get(2), 0.0001);

        // test with categories
        StringColumn last3 = (StringColumn) table.stringColumn("who").last(3);
        assertEquals("zogby", last3.get(0));
        assertEquals("zogby", last3.get(1));
        assertEquals("zogby", last3.get(2));
    }

    @Test
    public void testName() {
        Column c = table.numberColumn("approval");
        assertEquals("approval", c.name());
    }

    @Test
    public void testType() {
        Column c = table.numberColumn("approval");
        assertEquals(ColumnType.NUMBER, c.type());
    }
}
