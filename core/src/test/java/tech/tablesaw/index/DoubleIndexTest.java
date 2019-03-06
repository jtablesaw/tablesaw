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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.columns.numbers.NumberPredicates;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class DoubleIndexTest {

    private DoubleIndex index;
    private Table table;

    @BeforeEach
    public void setUp() throws Exception {

        table = Table.read().csv(
                CsvReadOptions.builder("../data/bus_stop_test.csv")
                        // explicitly set column type, due to CsvReader#detectType returns ColumnType.FLOAT
                        // for 'stop_lat' and 'stop_lon' columns
                        .columnTypes(new ColumnType[]{
                                ColumnType.DOUBLE,
                                ColumnType.STRING,
                                ColumnType.STRING,
                                ColumnType.DOUBLE,
                                ColumnType.DOUBLE}));
        index = new DoubleIndex(table.doubleColumn("stop_lat"));
    }

    @Test
    public void testGet() {
        Selection fromCol = table.numberColumn("stop_lat").eval(NumberPredicates.isEqualTo, 30.330425);
        Selection fromIdx = index.get(30.330425);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGTE() {
        Selection fromCol = table.numberColumn("stop_lat").eval(NumberPredicates.isGreaterThanOrEqualTo, 30.330425);
        Selection fromIdx = index.atLeast(30.330425);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLTE() {
        Selection fromCol = table.numberColumn("stop_lat").eval(NumberPredicates.isLessThanOrEqualTo, 30.330425);
        Selection fromIdx = index.atMost(30.330425);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLT() {
        Selection fromCol = table.numberColumn("stop_lat").eval(NumberPredicates.isLessThan, 30.330425);
        Selection fromIdx = index.lessThan(30.330425);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGT() {
        Selection fromCol = table.numberColumn("stop_lat").eval(NumberPredicates.isGreaterThan, 30.330425);
        Selection fromIdx = index.greaterThan(30.330425);
        assertEquals(fromCol, fromIdx);
    }
}