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
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.datetimes.DateTimePredicates;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.selection.Selection;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
public class LongIndexTest {

    private LongIndex index;
    private Table table;

    @BeforeEach
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));
        DateTimeColumn midnights = table.dateColumn("date").atStartOfDay();
        midnights.setName("Midnights");
        table.addColumns(midnights);
        index = new LongIndex(table.dateTimeColumn("Midnights"));
    }

    @Test
    public void testGet() {
        Selection fromCol = table.dateTimeColumn("Midnights").eval(DateTimePredicates.isEqualTo, 71);
        Selection fromIdx = index.get(71);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGTE() {
        Selection fromCol = table.dateTimeColumn("Midnights").eval(DateTimePredicates.isGreaterThanOrEqualTo, 71);
        Selection fromIdx = index.atLeast(71);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLTE() {
        Selection fromCol = table.dateTimeColumn("Midnights").eval(DateTimePredicates.isLessThanOrEqualTo, 71);
        Selection fromIdx = index.atMost(71);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testLT() {
        Selection fromCol = table.dateTimeColumn("Midnights").eval(DateTimePredicates.isLessThan, 71);
        Selection fromIdx = index.lessThan(71);
        assertEquals(fromCol, fromIdx);
    }

    @Test
    public void testGT() {
        Selection fromCol = table.dateTimeColumn("Midnights").eval(DateTimePredicates.isGreaterThan, 71);
        Selection fromIdx = index.greaterThan(71);
        assertEquals(fromCol, fromIdx);
    }
}