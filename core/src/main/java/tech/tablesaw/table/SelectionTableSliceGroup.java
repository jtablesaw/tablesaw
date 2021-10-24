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

import java.util.ArrayList;
import java.util.List;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/** A group of tables formed by performing splitting operations on an original table */
public class SelectionTableSliceGroup extends TableSliceGroup {

  /**
   * Creates a TableSliceGroup where each slice contains {@code step} number of rows from the
   * backing table
   *
   * @param original The original backing table that provides the data for the new slice group
   * @param subTableNameTemplate The prefix of a name for each slice in the group. If the argument
   *     is "step" the name will take the form "step 1", "step 2", etc.
   * @param step The number of rows per slice
   * @return The new table
   */
  public static SelectionTableSliceGroup create(
      Table original, String subTableNameTemplate, int step) {
    return new SelectionTableSliceGroup(original, subTableNameTemplate, step);
  }

  private SelectionTableSliceGroup(Table original, String subTableNameTemplate, int step) {
    super(original);
    List<Selection> selections = new ArrayList<>();
    for (int i = 0; i < original.rowCount() - step; i += step) {
      Selection selection = new BitmapBackedSelection();
      selection.addRange(i, i + step);
      selections.add(selection);
    }
    splitOnSelection(subTableNameTemplate, selections);
  }

  private void splitOnSelection(String nameTemplate, List<Selection> selections) {
    for (int i = 0; i < selections.size(); i++) {
      TableSlice view = new TableSlice(getSourceTable(), selections.get(i));
      String name = nameTemplate + ": " + i + 1;
      view.setName(name);
      getSlices().add(view);
    }
  }
}
