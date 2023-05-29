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

import java.util.SortedMap;

/** An index for byte columns (BooleanColumn) */
public class ByteIndex extends Index{

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



  @Override
  protected <T> IntArrayList getIndexList(T value) {
    return index.get(value);
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> GTgetTailMap(T value) {
    Byte byteValue = (Byte) value;
    return (SortedMap<T, IntArrayList>) index.tailMap((byte) (byteValue + 1));
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> aLgetTailMap(T value) {
    return (SortedMap<T, IntArrayList>) index.tailMap((byte) (value));
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> aMgetheadMap(T value) {
    Byte byteValue = (Byte) value;
    return (SortedMap<T, IntArrayList>) index.headMap((byte) (byteValue + 1));

  }
  @Override
  protected <T> SortedMap<T, IntArrayList> LTgetheadMap(T value) {
    return (SortedMap<T, IntArrayList>) index.headMap((byte) (value));
  }

}
