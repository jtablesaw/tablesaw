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

package tech.tablesaw.io.html;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.table.StandardTableSliceGroup;
import tech.tablesaw.table.TableSliceGroup;

public class HtmlTableWriterTest {

    private Table table;

    @BeforeEach
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));
    }

    @Test
    public void testWrite() {
        StringColumn byColumn = table.stringColumn("who");
        TableSliceGroup group = StandardTableSliceGroup.create(table, byColumn);
        Table result = group.aggregate("approval", AggregateFunctions.mean);
        HtmlTableWriter.write(result);
    }
}
