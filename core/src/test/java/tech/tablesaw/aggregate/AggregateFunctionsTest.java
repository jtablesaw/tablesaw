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

package tech.tablesaw.aggregate;

import org.junit.Before;
import org.junit.Test;

import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.table.ViewGroup;

import static org.junit.Assert.assertEquals;


public class AggregateFunctionsTest {

    private static ColumnType[] types = {
            ColumnType.LOCAL_DATE,     // date of poll
            ColumnType.INTEGER,        // approval rating (pct)
            ColumnType.CATEGORY        // polling org
    };

    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/BushApproval.csv").columnTypes(types));
    }

    @Test
    public void testMean() {
        double result = table.agg("approval", AggregateFunctions.mean);
        assertEquals(64.88235294117646, result, 0.01);
    }

    @Test
    public void testGroupMean() {
        Column byColumn = table.column("who");
        ViewGroup group = new ViewGroup(table, byColumn);
        Table result = group.agg("approval", AggregateFunctions.mean);
        assertEquals(2, result.columnCount());
        assertEquals("who", result.column(0).name());
        assertEquals(6, result.rowCount());
        assertEquals("65.671875", result.get(0, 1));
    }

    @Test
    public void test2ColumnGroupMean() {
        Column byColumn1 = table.column("who");
        ViewGroup group = new ViewGroup(table, byColumn1);
        Table result = group.agg("approval", AggregateFunctions.mean);
        assertEquals(2, result.columnCount());
        assertEquals("who", result.column(0).name());
        assertEquals(6, result.rowCount());
        assertEquals("65.671875", result.get(0, 1));
    }
}