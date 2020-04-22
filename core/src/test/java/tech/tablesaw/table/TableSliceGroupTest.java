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

import com.google.common.collect.ImmutableListMultimap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.apache.commons.math3.stat.StatUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.tablesaw.aggregate.NumericAggregateFunction;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.TextColumn;
import tech.tablesaw.io.csv.CsvReadOptions;

public class TableSliceGroupTest {

  private static NumericAggregateFunction exaggerate =
      new NumericAggregateFunction("exageration") {

        @Override
        public Double summarize(NumericColumn<?> data) {
          return StatUtils.max(data.asDoubleArray()) + 1000;
        }
      };

  private Table table;

  @BeforeEach
  public void setUp() throws Exception {
    // The source data is sorted by who. Put it in a different order.
    table =
        Table.read().csv(CsvReadOptions.builder("../data/bush.csv")).sortAscendingOn("approval");
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
  public void testViewGroupCreationNames() {

    TableSliceGroup group = StandardTableSliceGroup.create(table, "who", "approval");
    List<TableSlice> viewList = group.getSlices();
    assertEquals(146, group.size());

    Set<String> viewNames = new HashSet<>();
    int count = 0;
    for (TableSlice view : viewList) {
      viewNames.add(view.name());
      count += view.rowCount();
    }
    assertEquals(table.rowCount(), count);
    assertTrue(viewNames.contains("zogby~~~45"));
  }

  @Test
  public void testViewTwoColumn() {
    TableSliceGroup group =
        StandardTableSliceGroup.create(
            table, table.categoricalColumn("who"), table.categoricalColumn("approval"));
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

  @Test
  public void aggregate() {
    TableSliceGroup group = StandardTableSliceGroup.create(table, table.categoricalColumn("who"));
    Table aggregated = group.aggregate("approval", exaggerate);
    assertEquals(aggregated.rowCount(), group.size());
  }

  @Test
  public void testCreateWithTextColumn() {
    TextColumn whoText = table.stringColumn("who").asTextColumn();
    whoText.setName("who text");
    table.addColumns(whoText);
    TableSliceGroup group1 =
        StandardTableSliceGroup.create(table, table.categoricalColumn("who text"));
    TableSliceGroup group2 = StandardTableSliceGroup.create(table, table.categoricalColumn("who"));
    Table aggregated1 = group1.aggregate("approval", exaggerate);
    Table aggregated2 = group2.aggregate("approval", exaggerate);
    assertEquals(aggregated1.rowCount(), aggregated2.rowCount());
  }

  @Test
  public void aggregateWithMultipleColumns() {
    table.addColumns(table.categoricalColumn("approval").copy().setName("approval2"));
    TableSliceGroup group = StandardTableSliceGroup.create(table, table.categoricalColumn("who"));

    Table aggregated =
        group.aggregate(ImmutableListMultimap.of("approval", exaggerate, "approval2", exaggerate));
    assertEquals(aggregated.rowCount(), group.size());
  }

  /**
   * Make sure that aggregations are allowed on empty tables. They should however just create new
   * empty tables.
   *
   * <p>see <a href="https://github.com/jtablesaw/tablesaw/issues/785">Issue#785</a>
   */
  @Test
  public void aggregateWithEmptyResult() {
    // drop all rows in order to carry out aggregation on an empty table
    table = table.dropRows(IntStream.range(0, table.column(0).size()).toArray());

    TableSliceGroup group = StandardTableSliceGroup.create(table, table.categoricalColumn("who"));
    Table aggregated = group.aggregate("approval", exaggerate);
    assertEquals(0, aggregated.rowCount(), "result should be empty");
    assertEquals(2, aggregated.columnCount()); // 1 original column + the aggregation column

    table.addColumns(table.categoricalColumn("approval").copy().setName("approval2"));
    group = StandardTableSliceGroup.create(table, table.categoricalColumn("who"));

    aggregated =
        group.aggregate(ImmutableListMultimap.of("approval", exaggerate, "approval2", exaggerate));
    assertEquals(0, aggregated.rowCount(), "result should be empty");
    assertEquals(3, aggregated.columnCount()); // 2 original columns + the aggregation column
  }
}
