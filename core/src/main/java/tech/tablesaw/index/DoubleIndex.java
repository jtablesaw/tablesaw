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

import it.unimi.dsi.fastutil.doubles.Double2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/** An index for double-precision 64-bit IEEE 754 floating point columns. */
public class DoubleIndex implements Index {

  private final Double2ObjectAVLTreeMap<IntArrayList> index;

  /** Constructs an index for the given column */
  public DoubleIndex(DoubleColumn column) {
    int sizeEstimate = Integer.min(1_000_000, column.size() / 100);
    Double2ObjectOpenHashMap<IntArrayList> tempMap = new Double2ObjectOpenHashMap<>(sizeEstimate);
    for (int i = 0; i < column.size(); i++) {
      double value = column.getDouble(i);
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
    index = new Double2ObjectAVLTreeMap<>(tempMap);
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
  public Selection get(double value) {
    Selection selection = new BitmapBackedSelection();
    IntArrayList list = index.get(value);
    if (list != null) {
      addAllToSelection(list, selection);
    }
    return selection;
  }

  /** Returns a {@link Selection} of all values at least as large as the given value */
  public Selection atLeast(double value) {
    Selection selection = new BitmapBackedSelection();
    Double2ObjectSortedMap<IntArrayList> tail = index.tailMap(value);
    for (IntArrayList keys : tail.values()) {
      addAllToSelection(keys, selection);
    }
    return selection;
  }

  /** Returns a {@link Selection} of all values greater than the given value */
  public Selection greaterThan(double value) {
    Selection selection = new BitmapBackedSelection();
    Double2ObjectSortedMap<IntArrayList> tail = index.tailMap(value + 0.000001);
    for (IntArrayList keys : tail.values()) {
      addAllToSelection(keys, selection);
    }
    return selection;
  }

  /** Returns a {@link Selection} of all values at most as large as the given value */
  public Selection atMost(double value) {
    Selection selection = new BitmapBackedSelection();
    Double2ObjectSortedMap<IntArrayList> head =
        index.headMap(value + 0.000001); // we add 1 to get values equal
    // to the arg
    for (IntArrayList keys : head.values()) {
      addAllToSelection(keys, selection);
    }
    return selection;
  }

  /** Returns a {@link Selection} of all values less than the given value */
  public Selection lessThan(double value) {
    Selection selection = new BitmapBackedSelection();
    Double2ObjectSortedMap<IntArrayList> head = index.headMap(value);
    for (IntArrayList keys : head.values()) {
      addAllToSelection(keys, selection);
    }
    return selection;
  }
}
