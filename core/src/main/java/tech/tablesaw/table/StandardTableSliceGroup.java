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

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import tech.tablesaw.api.CategoricalColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/** A group of tables formed by performing splitting operations on an original table */
public class StandardTableSliceGroup extends TableSliceGroup {

  private StandardTableSliceGroup(Table original, CategoricalColumn<?>... columns) {
    super(original, splitColumnNames(columns));
    setSourceTable(getSourceTable());
    splitOn(getSplitColumnNames());
  }

  private static String[] splitColumnNames(CategoricalColumn<?>... columns) {
    String[] splitColumnNames = new String[columns.length];
    for (int i = 0; i < columns.length; i++) {
      splitColumnNames[i] = columns[i].name();
    }
    return splitColumnNames;
  }

  /**
   * Returns a viewGroup splitting the original table on the given columns. The named columns must
   * be CategoricalColumns
   */
  public static StandardTableSliceGroup create(Table original, String... columnsNames) {
    List<CategoricalColumn<?>> columns = original.categoricalColumns(columnsNames);
    return new StandardTableSliceGroup(original, columns.toArray(new CategoricalColumn<?>[0]));
  }

  /**
   * Returns a viewGroup splitting the original table on the given columns. The named columns must
   * be CategoricalColumns
   */
  public static StandardTableSliceGroup create(Table original, CategoricalColumn<?>... columns) {
    return new StandardTableSliceGroup(original, columns);
  }

  /**
   * Splits the sourceTable table into sub-tables, grouping on the columns whose names are given in
   * splitColumnNames
   */
  private void splitOn(String... columnNames) {
    Map<ByteArray, Selection> selectionMap = new LinkedHashMap<>();
    Map<ByteArray, String> sliceNameMap = new HashMap<>();
    List<Column<?>> splitColumns = getSourceTable().columns(columnNames);
    int byteSize = getByteSize(splitColumns);

    for (int i = 0; i < getSourceTable().rowCount(); i++) {
      StringBuilder stringKey = new StringBuilder();
      ByteBuffer byteBuffer = ByteBuffer.allocate(byteSize);
      int count = 0;
      for (Column<?> col : splitColumns) {
        stringKey.append(col.getString(i));
        if (count < splitColumns.size() - 1) {
          stringKey.append(SPLIT_STRING);
        }
        byteBuffer.put(col.asBytes(i));
        count++;
      }
      // Add to the matching selection.
      ByteArray byteArray = new ByteArray(byteBuffer.array());
      Selection selection = selectionMap.getOrDefault(byteArray, new BitmapBackedSelection());
      selection.add(i);
      selectionMap.put(byteArray, selection);
      sliceNameMap.put(byteArray, stringKey.toString());
    }

    // Add all slices
    for (Entry<ByteArray, Selection> entry : selectionMap.entrySet()) {
      TableSlice slice = new TableSlice(getSourceTable(), entry.getValue());
      slice.setName(sliceNameMap.get(entry.getKey()));
      addSlice(slice);
    }
  }

  /** Wrapper class for a byte[] that implements equals and hashcode. */
  private static class ByteArray {
    final byte[] bytes;

    ByteArray(byte[] bytes) {
      this.bytes = bytes;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ByteArray byteArray = (ByteArray) o;
      return Arrays.equals(bytes, byteArray.bytes);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(bytes);
    }
  }
}
