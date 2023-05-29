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
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectSortedMap;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.SortedMap;

import tech.tablesaw.api.LongColumn;
import tech.tablesaw.columns.datetimes.PackedLocalDateTime;
import tech.tablesaw.columns.instant.PackedInstant;
import tech.tablesaw.columns.temporal.TemporalColumn;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/** An index for eight-byte long and long backed columns (datetime) */
public class LongIndex extends Index{

  private final Long2ObjectAVLTreeMap<IntArrayList> index;

  /** Constructs an index for the given column */
  public LongIndex(TemporalColumn<?> column) {
    int sizeEstimate = Integer.min(1_000_000, column.size() / 100);
    Long2ObjectOpenHashMap<IntArrayList> tempMap = new Long2ObjectOpenHashMap<>(sizeEstimate);
    for (int i = 0; i < column.size(); i++) {
      long value = column.getLongInternal(i);
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
    index = new Long2ObjectAVLTreeMap<>(tempMap);
  }

  /** Constructs an index for the given column */
  public LongIndex(LongColumn column) {
    int sizeEstimate = Integer.min(1_000_000, column.size() / 100);
    Long2ObjectOpenHashMap<IntArrayList> tempMap = new Long2ObjectOpenHashMap<>(sizeEstimate);
    for (int i = 0; i < column.size(); i++) {
      long value = column.getLong(i);
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
    index = new Long2ObjectAVLTreeMap<>(tempMap);
  }


  @Override
  protected <T> IntArrayList getIndexList(T value) {
    return index.get(value);
  }


  /** Returns a {@link Selection} of all values at least as large as the given value */
  public Selection atLeast(Instant value) {
    return atLeast(PackedInstant.pack(value));
  }

  /** Returns a {@link Selection} of all values at least as large as the given value */
  public Selection atLeast(LocalDateTime value) {
    return atLeast(PackedLocalDateTime.pack(value));
  }




  /** Returns a {@link Selection} of all values greater than the given value */
  public Selection greaterThan(Instant value) {
    return greaterThan(PackedInstant.pack(value));
  }

  /** Returns a {@link Selection} of all values greater than the given value */
  public Selection greaterThan(LocalDateTime value) {
    return greaterThan(PackedLocalDateTime.pack(value));
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> GTgetTailMap(T value) {
    Long longValue = (Long) value;
    return (SortedMap<T, IntArrayList>) index.tailMap((long) (longValue + 1));
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> aLgetTailMap(T value) {
    return (SortedMap<T, IntArrayList>) index.tailMap((long)value);
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> aMgetheadMap(T value) {
    Long longValue = (Long) value;
    return (SortedMap<T, IntArrayList>) index.headMap((long) (longValue + 1));
  }

  @Override
  protected <T> SortedMap<T, IntArrayList> LTgetheadMap(T value) {
    return (SortedMap<T, IntArrayList>) index.headMap((long)value);
  }



  /** Returns a {@link Selection} of all values at most as large as the given value */
  public Selection atMost(Instant value) {
    return atMost(PackedInstant.pack(value));
  }

  /** Returns a {@link Selection} of all values at most as large as the given value */
  public Selection atMost(LocalDateTime value) {
    return atMost(PackedLocalDateTime.pack(value));
  }



  /** Returns a {@link Selection} of all values less than the given value */
  public Selection lessThan(Instant value) {
    return lessThan(PackedInstant.pack(value));
  }

  /** Returns a {@link Selection} of all values less than the given value */
  public Selection lessThan(LocalDateTime value) {
    return lessThan(PackedLocalDateTime.pack(value));
  }

  public Selection get(Instant value) {
    return get(PackedInstant.pack(value));
  }
  public Selection get(LocalDateTime value) {
    return get(PackedLocalDateTime.pack(value));
  }
}
