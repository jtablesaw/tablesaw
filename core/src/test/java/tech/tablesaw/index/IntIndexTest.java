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

package tech.tablesaw.index;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.DateColumnUtils;
import tech.tablesaw.columns.IntColumnUtils;
import tech.tablesaw.columns.packeddata.PackedLocalDate;
import tech.tablesaw.index.DateIndex;
import tech.tablesaw.index.IntIndex;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.util.Selection;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static tech.tablesaw.api.ColumnType.*;

/**
 *
 */
public class IntIndexTest {

    private ColumnType[] types = {
            LOCAL_DATE,     // date of poll
            INTEGER,        // approval rating (pct)
            CATEGORY        // polling org
    };

    private IntIndex index;
    private DateIndex dateIndex;
    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/BushApproval.csv").columnTypes(types));
        index = new IntIndex(table.intColumn("approval"));
        dateIndex = new DateIndex(table.dateColumn("date"));
    }

    @Test
    public void testGet() {
        Selection fromCol = table.intColumn("approval").select(IntColumnUtils.isEqualTo, 71);
        Selection fromIdx = index.get(71);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGet2() {
        LocalDate date = LocalDate.of(2001, 12, 12);
        int packedDate = PackedLocalDate.pack(date);
        Selection fromCol = table.dateColumn("date").select(DateColumnUtils.isEqualTo, packedDate);
        Selection fromIdx = dateIndex.get(date);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGTE() {
        Selection fromCol = table.intColumn("approval").select(IntColumnUtils.isGreaterThanOrEqualTo, 71);
        Selection fromIdx = index.atLeast(71);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGTE2() {
        LocalDate date = LocalDate.of(2001, 12, 12);
        int packedDate = PackedLocalDate.pack(date);
        Selection fromCol = table.dateColumn("date").select(DateColumnUtils.isGreaterThanOrEqualTo, packedDate);
        Selection fromIdx = dateIndex.atLeast(date);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLTE() {
        Selection fromCol = table.intColumn("approval").select(IntColumnUtils.isLessThanOrEqualTo, 71);
        Selection fromIdx = index.atMost(71);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLT() {
        Selection fromCol = table.intColumn("approval").select(IntColumnUtils.isLessThan, 71);
        Selection fromIdx = index.lessThan(71);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGT() {
        Selection fromCol = table.intColumn("approval").select(IntColumnUtils.isGreaterThan, 71);
        Selection fromIdx = index.greaterThan(71);
        assertEquals(fromCol, fromIdx);
    }
}