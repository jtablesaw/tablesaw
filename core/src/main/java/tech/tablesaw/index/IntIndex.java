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
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.dates.PackedLocalDate;
import tech.tablesaw.columns.times.PackedLocalTime;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/** An index for four-byte integer and integer backed columns (date, String, time) */
public class IntIndex implements Index {

  private final Int2ObjectAVLTreeMap<IntArrayList> index;

  /** Constructs an index for the given column */
  public IntIndex(DateColumn column) {
    int sizeEstimate = Integer.min(1_000_000, column.size() / 100);
    Int2ObjectOpenHashMap<IntArrayList> tempMap = new Int2ObjectOpenHashMap<>(sizeEstimate);
    for (int i = 0; i < column.size(); i++) {
      int value = column.getIntInternal(i);
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
    index = new Int2ObjectAVLTreeMap<>(tempMap);
  }

  /** Constructs an index for the given column */
  public IntIndex(IntColumn column) {
    Preconditions.checkArgument(
        column.type().equals(ColumnType.INTEGER),
        "Int indexing only allowed on INTEGER numeric columns");
    int sizeEstimate = Integer.min(1_000_000, column.size() / 100);
    Int2ObjectOpenHashMap<IntArrayList> tempMap = new Int2ObjectOpenHashMap<>(sizeEstimate);
    for (int i = 0; i < column.size(); i++) {
      int value = column.getInt(i);
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
    index = new Int2ObjectAVLTreeMap<>(tempMap);
  }

  /** Constructs an index for the given column */
  public IntIndex(TimeColumn column) {
    int sizeEstimate = Integer.min(1_000_000, column.size() / 100);
    Int2ObjectOpenHashMap<IntArrayList> tempMap = new Int2ObjectOpenHashMap<>(sizeEstimate);
    for (int i = 0; i < column.size(); i++) {
      int value = column.getIntInternal(i);
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
    index = new Int2ObjectAVLTreeMap<>(tempMap);
  }

  private static void addAllToSelection(IntArrayList tableKeys, Selection selection) {
    for (int i : tableKeys) {
      selection.add(i);
    }
  }

  /**
   * Returns a bitmap {@link Selection} containing row numbers of all cells matching the given int
   *
   * @param value This is a 'key' from the index perspective, meaning it is a value from the
   *     standpoint of the column
   */
  public Selection get(int value) {
    Selection selection = new BitmapBackedSelection();
    IntArrayList list = index.get(value);
    if (list != null) {
      addAllToSelection(list, selection);
    }
    return selection;
  }

  /** Returns the {@link Selection} of all values exactly equal to the given value */
  public Selection get(LocalTime value) {
    return get(PackedLocalTime.pack(value));
  }

  /** Returns the {@link Selection} of all values exactly equal to the given value */
  public Selection get(LocalDate value) {
    return get(PackedLocalDate.pack(value));
  }

  /** Returns a {@link Selection} of all values at least as large as the given value */
  public Selection atLeast(int value) {
    Selection selection = new BitmapBackedSelection();
    Int2ObjectSortedMap<IntArrayList> tail = index.tailMap(value);
    for (IntArrayList keys : tail.values()) {
      addAllToSelection(keys, selection);
    }
    return selection;
  }

  /** Returns a {@link Selection} of all values at least as large as the given value */
  public Selection atLeast(LocalTime value) {
    return atLeast(PackedLocalTime.pack(value));
  }

  /** Returns a {@link Selection} of all values at least as large as the given value */
  public Selection atLeast(LocalDate value) {
    return atLeast(PackedLocalDate.pack(value));
  }

  /** Returns a {@link Selection} of all values greater than the given value */
  public Selection greaterThan(int value) {
    Selection selection = new BitmapBackedSelection();
    Int2ObjectSortedMap<IntArrayList> tail = index.tailMap(value + 1);
    for (IntArrayList keys : tail.values()) {
      addAllToSelection(keys, selection);
    }
    return selection;
  }

  /** Returns a {@link Selection} of all values greater than the given value */
  public Selection greaterThan(LocalTime value) {
    return greaterThan(PackedLocalTime.pack(value));
  }

  /** Returns a {@link Selection} of all values greater than the given value */
  public Selection greaterThan(LocalDate value) {
    return greaterThan(PackedLocalDate.pack(value));
  }

  /** Returns a {@link Selection} of all values at most as large as the given value */
  public Selection atMost(int value) {
    Selection selection = new BitmapBackedSelection();
    Int2ObjectSortedMap<IntArrayList> head =
        index.headMap(value + 1); // we add 1 to get values equal to the arg
    for (IntArrayList keys : head.values()) {
      addAllToSelection(keys, selection);
    }
    return selection;
  }

  /** Returns a {@link Selection} of all values at most as large as the given value */
  public Selection atMost(LocalTime value) {
    return atMost(PackedLocalTime.pack(value));
  }

  /** Returns a {@link Selection} of all values at most as large as the given value */
  public Selection atMost(LocalDate value) {
    return atMost(PackedLocalDate.pack(value));
  }

  /** Returns a {@link Selection} of all values less than the given value */
  public Selection lessThan(int value) {
    Selection selection = new BitmapBackedSelection();
    Int2ObjectSortedMap<IntArrayList> head =
        index.headMap(value); // we add 1 to get values equal to the arg
    for (IntArrayList keys : head.values()) {
      addAllToSelection(keys, selection);
    }
    return selection;
  }

  /** Returns a {@link Selection} of all values less than the given value */
  public Selection lessThan(LocalTime value) {
    return lessThan(PackedLocalTime.pack(value));
  }

  /** Returns a {@link Selection} of all values less than the given value */
  public Selection lessThan(LocalDate value) {
    return lessThan(PackedLocalDate.pack(value));
  }
}
