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

package tech.tablesaw;

import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.QueryHelper;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static tech.tablesaw.api.QueryHelper.*;

/**
 * Tests for filtering on the T class
 */
public class TableFilteringTest {

    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));
    }

    @Test
    public void testFilter1() {
        Table result = table.selectWhere(numberColumn("approval").isLessThan(70));
        NumberColumn a = result.numberColumn("approval");
        for (double v : a) {
            assertTrue(v < 70);
        }
    }

    @Test
    public void testReject() {
        Table result = table.rejectWhere(numberColumn("approval").isLessThan(70));
        NumberColumn a = result.numberColumn("approval");
        for (double v : a) {
            assertFalse(v < 70);
        }
    }

    @Test
    public void testFilter2() {
        Table result = table.selectWhere(dateColumn("date").isInApril());
        DateColumn d = result.dateColumn("date");
        for (LocalDate v : d) {
            assertTrue(PackedLocalDate.isInApril(PackedLocalDate.pack(v)));
        }
    }

    @Test
    public void testFilter3() {
        Table result = table.selectWhere(
                QueryHelper.both(
                        table.dateColumn("date").isInApril(),
                        table.numberColumn("approval").isGreaterThan(70)));

        DateColumn dates = result.dateColumn("date");
        NumberColumn approval = result.numberColumn("approval");
        for (int row : result) {
            assertTrue(PackedLocalDate.isInApril(dates.getIntInternal(row)));
            assertTrue(approval.get(row) > 70);
        }
    }

    @Test
    public void testFilter4() {
        Table result =
                table.select("who", "approval")
                        .where(
                                QueryHelper.both(
                                        table.dateColumn("date").isInApril(),
                                        table.numberColumn("approval").isGreaterThan(70)));
        assertEquals(2, result.columnCount());
        assertTrue(result.columnNames().contains("who"));
        assertTrue(result.columnNames().contains("approval"));
    }
}
