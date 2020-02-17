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

package tech.tablesaw.beakerx;

import com.google.common.collect.Lists;
import com.twosigma.beakerx.jvm.object.OutputCell;
import com.twosigma.beakerx.table.TableDisplay;
import java.util.Map;
import jupyter.Displayer;
import jupyter.Displayers;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class TablesawDisplayer {

  private TablesawDisplayer() {}

  /**
   * Registers {@link Table} and {@link Column} for display in Jupyter. Call {@link
   * #registerTable()} or {@link #registerColumns()} instead if you'd like to only display one or
   * the other.
   */
  public static void register() {
    registerTable();
    registerColumns();
  }

  /** Registers {@link Table} for display in Jupyter. */
  public static void registerTable() {
    Displayers.register(
        Table.class,
        new Displayer<Table>() {
          @Override
          public Map<String, String> display(Table table) {
            new TableDisplay(
                    table.rowCount(),
                    table.columnCount(),
                    table.columnNames(),
                    (int columnIndex, int rowIndex) -> table.getUnformatted(rowIndex, columnIndex))
                .display();
            return OutputCell.DISPLAYER_HIDDEN;
          }
        });
  }

  /**
   * Registers and {@link Column} for display in Jupyter. Call {@link #registerTable()} or {@link
   * #registerColumns()} instead if you'd like to only display one or the other.
   */
  // TODO: remove rawtypes warnings suppression after PR below is merged
  // https://github.com/jupyter/jvm-repr/pull/22
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void registerColumns() {
    Displayers.register(
        Column.class,
        new Displayer<Column>() {
          @Override
          public Map<String, String> display(Column column) {
            new TableDisplay(
                    column.size(),
                    1,
                    Lists.newArrayList(column.name()),
                    (int columnIndex, int rowIndex) -> column.getUnformattedString(rowIndex))
                .display();
            return OutputCell.DISPLAYER_HIDDEN;
          }
        });
  }
}
