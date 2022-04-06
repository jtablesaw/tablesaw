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

package tech.tablesaw.joining;

import com.google.common.base.MoreObjects;
import java.util.*;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Row;

public class SortKey implements Iterable<DataFrameJoiner.IndexPair> {

  private final ArrayList<DataFrameJoiner.IndexPair> sortOrder = new ArrayList<>();

  private SortKey(DataFrameJoiner.IndexPair pair) {
    next(pair);
  }

  public static SortKey on(DataFrameJoiner.IndexPair pair) {
    return new SortKey(pair);
  }

  public SortKey next(DataFrameJoiner.IndexPair pair) {
    sortOrder.add(pair);
    return this;
  }

  /** Returns true if no order has been set */
  public boolean isEmpty() {
    return sortOrder.isEmpty();
  }

  /** Returns the number of columns used in this sort */
  public int size() {
    return sortOrder.size();
  }

  public static SortKey create(List<DataFrameJoiner.IndexPair> pairs) {
    SortKey key = null;

    for (DataFrameJoiner.IndexPair pair : pairs) {
      if (key == null) { // key will be null the first time through
        key = new SortKey(pair);
      } else {
        key.next(pair);
      }
    }
    return key;
  }

  public static RowComparatorChain getChain(SortKey key) {
    Iterator<DataFrameJoiner.IndexPair> entries = key.iterator();
    DataFrameJoiner.IndexPair sort = entries.next();
    Comparator<Row> comparator = comparator(sort);

    RowComparatorChain chain = new RowComparatorChain(comparator);
    while (entries.hasNext()) {
      sort = entries.next();
      chain.addComparator(comparator(sort));
    }
    return chain;
  }

  static Comparator<Row> comparator(DataFrameJoiner.IndexPair pair) {
    if (pair.type.equals(ColumnType.BOOLEAN)) {
      return (r11, r21) -> {
        boolean b1 = r11.getBoolean(pair.left);
        boolean b2 = r21.getBoolean(pair.right);
        return Boolean.compare(b1, b2);
      };
    } else if (pair.type.equals(ColumnType.INTEGER)) {
      return (r11, r21) -> {
        int b1 = r11.getInt(pair.left);
        int b2 = r21.getInt(pair.right);
        return Integer.compare(b1, b2);
      };
    } else if (pair.type.equals(ColumnType.LOCAL_DATE)) {
      return (r11, r21) -> {
        int b1 = r11.getPackedDate(pair.left);
        int b2 = r21.getPackedDate(pair.right);
        return Integer.compare(b1, b2);
      };
    }
    throw new RuntimeException("FINISH ME");
  }

  @Override
  public Iterator<DataFrameJoiner.IndexPair> iterator() {
    return sortOrder.iterator();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("order", sortOrder).toString();
  }

  public enum Order {
    ASCEND,
  }
}
