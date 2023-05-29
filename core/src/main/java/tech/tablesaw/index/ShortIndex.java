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
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.shorts.Short2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectSortedMap;
import tech.tablesaw.api.ShortColumn;
import tech.tablesaw.columns.numbers.ShortColumnType;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.util.SortedMap;

/** An index for {@link ShortColumn} */
public class ShortIndex extends Index{

  private final Short2ObjectAVLTreeMap<IntArrayList> index;

  /** Constructs an index for the given column */
  public ShortIndex(ShortColumn column) {
    Preconditions.checkArgument(
        column.type().equals(ShortColumnType.instance()),
        "Short indexing only allowed on SHORT numeric columns");
    int sizeEstimate = Integer.min(1_000_000, column.size() / 100);
    Short2ObjectOpenHashMap<IntArrayList> tempMap = new Short2ObjectOpenHashMap<>(sizeEstimate);
    for (int i = 0; i < column.size(); i++) {
      short value = column.getShort(i);
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
    index = new Short2ObjectAVLTreeMap<>(tempMap);
  }



  /**
   * Returns a bitmap containing row numbers of all cells matching the given int
   *
   * @param value This is a 'key' from the index perspective, meaning it is a value from the
   *     standpoint of the column
   */
  @Override
  protected <T> IntArrayList getIndexList(T value) {
    return index.get(value);
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> GTgetTailMap(T value) {
    Short shortValue = (Short) value;
    return (SortedMap<T, IntArrayList>) index.tailMap((short) (shortValue + 1));
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> aLgetTailMap(T value) {
    return (SortedMap<T, IntArrayList>) index.tailMap((short) value);
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> aMgetheadMap(T value) {
    Short shortValue = (Short) value;
    return (SortedMap<T, IntArrayList>) index.headMap((short) (shortValue + 1));
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> LTgetheadMap(T value) {
    return (SortedMap<T, IntArrayList>) index.headMap((short) value);
  }

}
