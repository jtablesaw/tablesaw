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

import java.util.SortedMap;

/** An index for double-precision 64-bit IEEE 754 floating point columns. */
public class DoubleIndex extends Index {

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


  @Override
  protected <T> IntArrayList getIndexList(T value) {
    return index.get(value);
  }

  /** Returns a {@link Selection} of all values at least as large as the given value */

  @Override
  protected <T> SortedMap<T, IntArrayList> GTgetTailMap(T value) {
    Double doubleValue = (Double) value;
    return (SortedMap<T, IntArrayList>) index.tailMap((double) (doubleValue + 0.000001));
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> aLgetTailMap(T value) {
    return (SortedMap<T, IntArrayList>) index.tailMap((double) (value));
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> aMgetheadMap(T value) {
    Double doubleValue = (Double) value;
    return (SortedMap<T, IntArrayList>) index.headMap((double) (doubleValue + 0.000001));
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> LTgetheadMap(T value) {
    return (SortedMap<T, IntArrayList>) index.headMap((double) (value));
  }


}
