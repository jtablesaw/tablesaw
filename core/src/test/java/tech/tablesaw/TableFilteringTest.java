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

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.packeddata.PackedLocalDate;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static tech.tablesaw.api.QueryHelper.*;

/**
 * Tests for filtering on the T class
 */
public class TableFilteringTest {

    private final ColumnType[] types = {
            ColumnType.LOCAL_DATE,     // date of poll
            ColumnType.INTEGER,        // approval rating (pct)
            ColumnType.CATEGORY             // polling org
    };

    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/BushApproval.csv").columnTypes(types));
    }

    @Test
    public void testFilter1() {
        Table result = table.selectWhere(column("approval").isLessThan(70));
        IntColumn a = result.intColumn("approval");
        for (int v : a) {
            assertTrue(v < 70);
        }
    }

    @Test
    public void testFilter2() {
        Table result = table.selectWhere(column("date").isInApril());
        DateColumn d = result.dateColumn("date");
        for (LocalDate v : d) {
            assertTrue(PackedLocalDate.isInApril(PackedLocalDate.pack(v)));
        }
    }

    @Test
    public void testFilter3() {
        Table result = table.selectWhere(
                both(column("date").isInApril(),
                        column("approval").isGreaterThan(70)));

        DateColumn dates = result.dateColumn("date");
        IntColumn approval = result.intColumn("approval");
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
                                and(column("date").isInApril(),
                                        column("approval").isGreaterThan(70)));
        assertEquals(2, result.columnCount());
        assertTrue(result.columnNames().contains("who"));
        assertTrue(result.columnNames().contains("approval"));
    }
}
