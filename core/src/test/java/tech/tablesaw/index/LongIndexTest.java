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

import org.junit.Before;
import org.junit.Test;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.LongColumnUtils;
import tech.tablesaw.index.LongIndex;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.util.Selection;

import static org.junit.Assert.assertEquals;
import static tech.tablesaw.api.ColumnType.*;

/**
 *
 */
public class LongIndexTest {
    private ColumnType[] types = {
            LOCAL_DATE,     // date of poll
            LONG_INT,       // approval rating (pct)
            CATEGORY        // polling org
    };

    private LongIndex index;
    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/BushApproval.csv").columnTypes(types));
        index = new LongIndex(table.longColumn("approval"));
    }

    @Test
    public void testGet() {
        Selection fromCol = table.longColumn("approval").select(LongColumnUtils.isEqualTo, 71);
        Selection fromIdx = index.get(71);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGTE() {
        Selection fromCol = table.longColumn("approval").select(LongColumnUtils.isGreaterThanOrEqualTo, 71);
        Selection fromIdx = index.atLeast(71);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLTE() {
        Selection fromCol = table.longColumn("approval").select(LongColumnUtils.isLessThanOrEqualTo, 71);
        Selection fromIdx = index.atMost(71);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLT() {
        Selection fromCol = table.longColumn("approval").select(LongColumnUtils.isLessThan, 71);
        Selection fromIdx = index.lessThan(71);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGT() {
        Selection fromCol = table.longColumn("approval").select(LongColumnUtils.isGreaterThan, 71);
        Selection fromIdx = index.greaterThan(71);
        assertEquals(fromCol, fromIdx);
    }
}