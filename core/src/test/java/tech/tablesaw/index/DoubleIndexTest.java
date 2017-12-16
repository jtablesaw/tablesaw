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

import static org.junit.Assert.assertEquals;
import static tech.tablesaw.io.csv.CsvReadOptions.builder;

import org.junit.Before;
import org.junit.Test;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.DoubleColumnUtils;
import tech.tablesaw.util.Selection;

/**
 *
 */
public class DoubleIndexTest {

    private DoubleIndex index;
    private Table table;

    @Before
    public void setUp() throws Exception {
        
        table = Table.read().csv(
                    builder("../data/bus_stop_test.csv")
                        // explicitly set column type, due to CsvReader#detectType returns ColumnType.FLOAT
                        // for 'stop_lat' and 'stop_lon' columns
                        .columnTypes(new ColumnType[] {
                            ColumnType.SHORT_INT,
                            ColumnType.CATEGORY,
                            ColumnType.CATEGORY,
                            ColumnType.DOUBLE,
                            ColumnType.DOUBLE }));
        index = new DoubleIndex(table.doubleColumn("stop_lat"));
    }

    @Test
    public void testGet() {
        Selection fromCol = table.doubleColumn("stop_lat").select(DoubleColumnUtils.isEqualTo, 30.330425);
        Selection fromIdx = index.get(30.330425);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGTE() {
        Selection fromCol = table.doubleColumn("stop_lat").select(DoubleColumnUtils.isGreaterThanOrEqualTo, 30.330425);
        Selection fromIdx = index.atLeast(30.330425);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLTE() {
        Selection fromCol = table.doubleColumn("stop_lat").select(DoubleColumnUtils.isLessThanOrEqualTo, 30.330425);
        Selection fromIdx = index.atMost(30.330425);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLT() {
        Selection fromCol = table.doubleColumn("stop_lat").select(DoubleColumnUtils.isLessThan, 30.330425);
        Selection fromIdx = index.lessThan(30.330425);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGT() {
        Selection fromCol = table.doubleColumn("stop_lat").select(DoubleColumnUtils.isGreaterThan, 30.330425);
        Selection fromIdx = index.greaterThan(30.330425);
        assertEquals(fromCol, fromIdx);
    }
}