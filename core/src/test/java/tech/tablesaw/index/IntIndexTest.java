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

import tech.tablesaw.columns.DateAndTimePredicates;
import tech.tablesaw.columns.number.NumberPredicates;
import org.junit.Before;
import org.junit.Test;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.util.selection.Selection;

import java.time.LocalDate;

import static tech.tablesaw.columns.number.NumberPredicates.isEqualTo;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class IntIndexTest {

    private ColumnType[] types = {
            ColumnType.LOCAL_DATE,     // date of poll
            ColumnType.NUMBER,        // approval rating (pct)
            ColumnType.STRING        // polling org
    };

    private DoubleIndex index;
    private IntIndex dateIndex;
    private Table table;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv").columnTypes(types));
        index = new DoubleIndex(table.numberColumn("approval"));
        dateIndex = new IntIndex(table.dateColumn("date"));
    }

    @Test
    public void testGet() {
        Selection fromCol = table.numberColumn("approval").eval(isEqualTo, 71);
        Selection fromIdx = index.get(71);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGet2() {
        LocalDate date = LocalDate.of(2001, 12, 12);
        int packedDate = PackedLocalDate.pack(date);
        Selection fromCol = table.dateColumn("date").eval(DateAndTimePredicates.isEqualTo, packedDate);
        Selection fromIdx = dateIndex.get(packedDate);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGTE() {
        Selection fromCol = table.numberColumn("approval").eval(NumberPredicates.isGreaterThanOrEqualTo, 71);
        Selection fromIdx = index.atLeast(71);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGTE2() {
        LocalDate date = LocalDate.of(2001, 12, 12);
        int packedDate = PackedLocalDate.pack(date);
        Selection fromCol = table.dateColumn("date").eval(DateAndTimePredicates.isGreaterThanOrEqualTo, packedDate);
        Selection fromIdx = dateIndex.atLeast(date);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLTE() {
        Selection fromCol = table.numberColumn("approval").eval(NumberPredicates.isLessThanOrEqualTo, 71);
        Selection fromIdx = index.atMost(71);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLT() {
        Selection fromCol = table.numberColumn("approval").eval(NumberPredicates.isLessThan, 71);
        Selection fromIdx = index.lessThan(71);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGT() {
        Selection fromCol = table.numberColumn("approval").eval(NumberPredicates.isGreaterThan, 71);
        Selection fromIdx = index.greaterThan(71);
        assertEquals(fromCol, fromIdx);
    }
}