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

import it.unimi.dsi.fastutil.floats.Float2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.util.SortedMap;

/** An index for single-precision 32-bit IEEE 754 floating point columns. */
public class FloatIndex extends Index{

  private final Float2ObjectAVLTreeMap<IntArrayList> index;

  /** Constructs an index for the given column */
  public FloatIndex(FloatColumn column) {
    int sizeEstimate = Integer.min(1_000_000, column.size() / 100);
    Float2ObjectOpenHashMap<IntArrayList> tempMap = new Float2ObjectOpenHashMap<>(sizeEstimate);
    for (int i = 0; i < column.size(); i++) {
      float value = column.getFloat(i);
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
    index = new Float2ObjectAVLTreeMap<>(tempMap);
  }




  @Override
  protected <T> IntArrayList getIndexList(T value) {
    return index.get(value);
  }


  @Override
  protected <T> SortedMap<T, IntArrayList> GTgetTailMap(T value) {
    Float floatValue = (Float) value;
    return (SortedMap<T, IntArrayList>) index.tailMap((float) (floatValue + 0.000001f));
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> aLgetTailMap(T value) {
    return (SortedMap<T, IntArrayList>) index.tailMap((float) (value));
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> aMgetheadMap(T value) {
    Float floatValue = (Float) value;
    return (SortedMap<T, IntArrayList>) index.headMap((float) (floatValue + 0.000001f));
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> LTgetheadMap(T value) {
    return (SortedMap<T, IntArrayList>) index.headMap((float) (value));
  }

}
