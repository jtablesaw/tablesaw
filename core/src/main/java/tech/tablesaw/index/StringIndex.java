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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import tech.tablesaw.api.StringColumn;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/**
 * TODO: Implement range query methods? See (e.g) ShortIndex for examples An index for String and
 * Text columns
 */
public class StringIndex extends Index{

  private final Map<String, IntArrayList> index;

  /** Creates an index on the given AbstractStringColumn */
  public StringIndex(StringColumn column) {
    int sizeEstimate = Integer.min(1_000_000, column.size() / 100);
    Map<String, IntArrayList> tempMap = new HashMap<>(sizeEstimate);
    for (int i = 0; i < column.size(); i++) {
      String value = column.get(i);
      IntArrayList recordIds = tempMap.get(value);
      if (recordIds == null) {
        recordIds = new IntArrayList();
        recordIds.add(i);
        tempMap.put(value, recordIds);
      } else {
        recordIds.add(i);
      }
    }
    index = new HashMap<>(tempMap);
  }

  @Override
  protected <T> IntArrayList getIndexList(T value) {
    return index.get(value);
  }
  @Override
  protected <T> SortedMap<T, IntArrayList> GTgetTailMap(T value) {
    Double doubleValue = (Double) value;
    return null;
  }
  @Override
  protected <T> SortedMap<T, IntArrayList> aLgetTailMap(T value) {
    return null;
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> aMgetheadMap(T value) {
    return null;
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> LTgetheadMap(T value) {
    return null;
  }

}
