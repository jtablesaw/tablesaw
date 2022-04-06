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

/**
 * SortKey is basically a specification for a sort. It defines the sort required for a
 * merge-sort-join. The sort order is defined such that the tables being joined are both sorted
 * independently on the join columns. All columns being sorted are sorted in ascending order
 */
class SortKey implements Iterable<DataFrameJoiner.ColumnIndexPair> {

  /** Describes how the tables are to be sorted */
  private final ArrayList<DataFrameJoiner.ColumnIndexPair> sortOrder = new ArrayList<>();

  private SortKey(DataFrameJoiner.ColumnIndexPair pair) {
    next(pair);
  }

  /**
   * Returns a new SortKey defining the first sort (for the first join column)
   *
   * @param pair The details of the sort, i.e. what type of column and the index of the columns in
   *     the respective tables.
   */
  public static SortKey on(DataFrameJoiner.ColumnIndexPair pair) {
    return new SortKey(pair);
  }

  /**
   * Returns a new SortKey defining an additional sort clause
   *
   * @param pair The details of the sort, i.e. what type of column and the index of the columns in
   *     the respective tables.
   */
  public SortKey next(DataFrameJoiner.ColumnIndexPair pair) {
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

  /**
   * Returns a new SortKey for the given ColumnIndexPairs. A table being sorted on three columns,
   * will have three pairs in the SortKey
   */
  public static SortKey create(List<DataFrameJoiner.ColumnIndexPair> pairs) {
    SortKey key = null;

    for (DataFrameJoiner.ColumnIndexPair pair : pairs) {
      if (key == null) { // key will be null the first time through
        key = new SortKey(pair);
      } else {
        key.next(pair);
      }
    }
    return key;
  }

  /**
   * Returns a ComparatorChain consisting of one or more comparators as specified in the given
   * SortKey
   */
  static RowComparatorChain getChain(SortKey key) {
    Iterator<DataFrameJoiner.ColumnIndexPair> entries = key.iterator();
    DataFrameJoiner.ColumnIndexPair sort = entries.next();
    Comparator<Row> comparator = comparator(sort);

    RowComparatorChain chain = new RowComparatorChain(comparator);
    while (entries.hasNext()) {
      sort = entries.next();
      chain.addComparator(comparator(sort));
    }
    return chain;
  }

  /** Returns a comparator for a given ColumnIndexPair */
  private static Comparator<Row> comparator(DataFrameJoiner.ColumnIndexPair pair) {
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

  /** Returns the iterator for the SortKey */
  @Override
  public Iterator<DataFrameJoiner.ColumnIndexPair> iterator() {
    return sortOrder.iterator();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("order", sortOrder).toString();
  }
}
