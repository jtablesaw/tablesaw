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

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/** An index for byte columns (BooleanColumn) */
public class ByteIndex implements Index {

  private final Byte2ObjectAVLTreeMap<IntArrayList> index;

  /** Constructs an index for the given column */
  public ByteIndex(BooleanColumn column) {
    Preconditions.checkArgument(
        column.type().equals(ColumnType.BOOLEAN), "Byte indexing only allowed on BOOLEAN columns");
    int sizeEstimate = Integer.min(1_000_000, column.size() / 100);
    Byte2ObjectOpenHashMap<IntArrayList> tempMap = new Byte2ObjectOpenHashMap<>(sizeEstimate);
    for (int i = 0; i < column.size(); i++) {
      byte value = column.getByte(i);
      IntArrayList recordIds = tempMap.get(value);
      if (recordIds == null) {
        recordIds = new IntArrayList();
        recordIds.add(i);
        tempMap.trim();
        tempMap.put(value, recordIds);
      } else {
        recordIds.add(i);
      }
    }
    index = new Byte2ObjectAVLTreeMap<>(tempMap);
  }

  private static void addAllToSelection(IntArrayList tableKeys, Selection selection) {
    for (int i : tableKeys) {
      selection.add(i);
    }
  }

  /**
   * Returns a bitmap containing row numbers of all cells matching the given int
   *
   * @param value This is a 'key' from the index perspective, meaning it is a value from the
   *     standpoint of the column
   */
  public Selection get(byte value) {
    Selection selection = new BitmapBackedSelection();
    IntArrayList list = index.get(value);
    if (list != null) {
      addAllToSelection(list, selection);
    }
    return selection;
  }

  /** Returns a {@link Selection} of all values at least as large as the given value */
  public Selection atLeast(byte value) {
    Selection selection = new BitmapBackedSelection();
    Byte2ObjectSortedMap<IntArrayList> tail = index.tailMap(value);
    for (IntArrayList keys : tail.values()) {
      addAllToSelection(keys, selection);
    }
    return selection;
  }

  /** Returns a {@link Selection} of all values greater than the given value */
  public Selection greaterThan(byte value) {
    Selection selection = new BitmapBackedSelection();
    Byte2ObjectSortedMap<IntArrayList> tail = index.tailMap((byte) (value + 1));
    for (IntArrayList keys : tail.values()) {
      addAllToSelection(keys, selection);
    }
    return selection;
  }

  /** Returns a {@link Selection} of all values at most as large as the given value */
  public Selection atMost(byte value) {
    Selection selection = new BitmapBackedSelection();
    Byte2ObjectSortedMap<IntArrayList> head =
        index.headMap((byte) (value + 1)); // we add 1 to get values equal to the arg
    for (IntArrayList keys : head.values()) {
      addAllToSelection(keys, selection);
    }
    return selection;
  }

  /** Returns a {@link Selection} of all values less than the given value */
  public Selection lessThan(byte value) {
    Selection selection = new BitmapBackedSelection();
    Byte2ObjectSortedMap<IntArrayList> head =
        index.headMap(value); // we add 1 to get values equal to the arg
    for (IntArrayList keys : head.values()) {
      addAllToSelection(keys, selection);
    }
    return selection;
  }
}
