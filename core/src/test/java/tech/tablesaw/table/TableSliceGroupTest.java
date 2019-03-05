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

package tech.tablesaw.table;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tech.tablesaw.aggregate.NumericAggregateFunction;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

public class TableSliceGroupTest {

    private static NumericAggregateFunction exaggerate = new NumericAggregateFunction("exageration") {

        @Override
        public Double summarize(NumericColumn<?> data) {
            return StatUtils.max(data.asDoubleArray()) + 1000;
        }
    };

    private Table table;

    @BeforeEach
    public void setUp() throws Exception {
        table = Table.read().csv(CsvReadOptions.builder("../data/bush.csv"));
    }

    @Test
    public void testViewGroupCreation() {

        TableSliceGroup group = StandardTableSliceGroup.create(table, table.categoricalColumn("who"));
        assertEquals(6, group.size());
        List<TableSlice> viewList = group.getSlices();

        int count = 0;
        for (TableSlice view : viewList) {
            count += view.rowCount();
        }
        assertEquals(table.rowCount(), count);
    }

    @Test
    public void testViewTwoColumn() {
        TableSliceGroup group = StandardTableSliceGroup.create(table,
                table.categoricalColumn("who"),
                table.categoricalColumn("approval"));
        List<TableSlice> viewList = group.getSlices();

        int count = 0;
        for (TableSlice view : viewList) {
            count += view.rowCount();
        }
        assertEquals(table.rowCount(), count);
    }

    @Test
    public void testCustomFunction() {
        Table exaggeration = table.summarize("approval", exaggerate).by("who");
        StringColumn group = exaggeration.stringColumn(0);
        assertTrue(group.contains("fox"));
    }

    @Test
    public void asTableList() {
        TableSliceGroup group = StandardTableSliceGroup.create(table, "who");
        List<Table> tables = group.asTableList();
        assertEquals(6, tables.size());
    }
}