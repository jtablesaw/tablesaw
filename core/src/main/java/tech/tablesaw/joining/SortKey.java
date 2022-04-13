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
class SortKey implements Iterable<ColumnIndexPair> {

  /** Describes how the tables are to be sorted */
  private final ArrayList<ColumnIndexPair> sortOrder = new ArrayList<>();

  private SortKey(ColumnIndexPair pair) {
    next(pair);
  }

  /**
   * Returns a new SortKey defining the first sort (for the first join column)
   *
   * @param pair The details of the sort, i.e. what type of column and the index of the columns in
   *     the respective tables.
   */
  public static SortKey on(ColumnIndexPair pair) {
    return new SortKey(pair);
  }

  /**
   * Returns a new SortKey defining an additional sort clause
   *
   * @param pair The details of the sort, i.e. what type of column and the index of the columns in
   *     the respective tables.
   */
  public SortKey next(ColumnIndexPair pair) {
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
  public static SortKey create(List<ColumnIndexPair> pairs) {
    SortKey key = null;

    for (ColumnIndexPair pair : pairs) {
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
    Iterator<ColumnIndexPair> entries = key.iterator();
    ColumnIndexPair sort = entries.next();
    Comparator<Row> comparator = comparator(sort);

    RowComparatorChain chain = new RowComparatorChain(comparator);
    while (entries.hasNext()) {
      sort = entries.next();
      chain.addComparator(comparator(sort));
    }
    return chain;
  }

  /** Returns a comparator for a given ColumnIndexPair */
  private static Comparator<Row> comparator(ColumnIndexPair pair) {
    if (pair.type.equals(ColumnType.INTEGER)) {
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
    } else if (pair.type.equals(ColumnType.LOCAL_TIME)) {
      return (r11, r21) -> {
        int b1 = r11.getPackedTime(pair.left);
        int b2 = r21.getPackedTime(pair.right);
        return Integer.compare(b1, b2);
      };
    } else if (pair.type.equals(ColumnType.LONG)) {
      return (r11, r21) -> {
        long b1 = r11.getLong(pair.left);
        long b2 = r21.getLong(pair.right);
        return Long.compare(b1, b2);
      };
    } else if (pair.type.equals(ColumnType.LOCAL_DATE_TIME)) {
      return (r11, r21) -> {
        long b1 = r11.getPackedDateTime(pair.left);
        long b2 = r21.getPackedDateTime(pair.right);
        return Long.compare(b1, b2);
      };
    } else if (pair.type.equals(ColumnType.INSTANT)) {
      return (r11, r21) -> {
        long b1 = r11.getPackedInstant(pair.left);
        long b2 = r21.getPackedInstant(pair.right);
        return Long.compare(b1, b2);
      };
    } else if (pair.type.equals(ColumnType.DOUBLE)) {
      return (r11, r21) -> {
        double b1 = r11.getDouble(pair.left);
        double b2 = r21.getDouble(pair.right);
        return Double.compare(b1, b2);
      };
    } else if (pair.type.equals(ColumnType.FLOAT)) {
      return (r11, r21) -> {
        float b1 = r11.getFloat(pair.left);
        float b2 = r21.getFloat(pair.right);
        return Float.compare(b1, b2);
      };
    } else if (pair.type.equals(ColumnType.BOOLEAN)) {
      return (r11, r21) -> {
        byte b1 = r11.getBooleanAsByte(pair.left);
        byte b2 = r21.getBooleanAsByte(pair.right);
        return Byte.compare(b1, b2);
      };
    } else if (pair.type.equals(ColumnType.STRING)) {
      return (r11, r21) -> {
        String b1 = r11.getString(pair.left);
        String b2 = r21.getString(pair.right);
        return b1.compareTo(b2);
      };
    }
    throw new RuntimeException("Unhandled ColumnType in SortKey.");
  }

  /** Returns the iterator for the SortKey */
  @Override
  public Iterator<ColumnIndexPair> iterator() {
    return sortOrder.iterator();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("order", sortOrder).toString();
  }
}
