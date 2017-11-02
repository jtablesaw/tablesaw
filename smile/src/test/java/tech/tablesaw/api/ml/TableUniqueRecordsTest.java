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

package tech.tablesaw.api.ml;

import org.junit.Before;
import org.junit.Test;

import tech.tablesaw.api.Table;

import static org.junit.Assert.*;

/**
 *
 */
public class TableUniqueRecordsTest {

    private Table table;
    private Table table2;

    @Before
    public void setUp() throws Exception {
        table = Table.read().csv("../data/BushApproval.csv");
        table = table.first(4);
        table2 = Table.create("2 column version");
        table2.addColumn(table.column(1), table.column(2));
    }

    @Test
    public void testUniqueRecord() throws Exception {

        Table uniques = table.uniqueRecords();
        assertEquals(table.rowCount(), uniques.rowCount());
        assertTrue(table.columnCount() == uniques.columnCount());

        Table uniques2 = table2.uniqueRecords();
        assertTrue(table2.rowCount() > uniques2.rowCount());
        assertEquals(table2.columnCount(), uniques2.columnCount());
    }
}
