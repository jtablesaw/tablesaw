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

import java.time.Instant;
import java.time.LocalDateTime;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectSortedMap;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.columns.datetimes.PackedLocalDateTime;
import tech.tablesaw.columns.instant.PackedInstant;
import tech.tablesaw.columns.temporal.TemporalColumn;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/**
 * An index for eight-byte long and long backed columns (datetime)
 */
public class LongIndex implements Index {

    private final Long2ObjectAVLTreeMap<IntArrayList> index;

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

    public LongIndex(IntColumn column) {
        int sizeEstimate = Integer.min(1_000_000, column.size() / 100);
        Long2ObjectOpenHashMap<IntArrayList> tempMap = new Long2ObjectOpenHashMap<>(sizeEstimate);
        for (int i = 0; i < column.size(); i++) {
            long value = column.getInt(i);
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

    private static void addAllToSelection(IntArrayList tableKeys, Selection selection) {
        for (int i : tableKeys) {
            selection.add(i);
        }
    }

    /**
     * Returns a bitmap containing row numbers of all cells matching the given long
     *
     * @param value This is a 'key' from the index perspective, meaning it is a value from the standpoint of the column
     */
    public Selection get(long value) {
        Selection selection = new BitmapBackedSelection();
        IntArrayList list = index.get(value);
        if (list != null) {
            addAllToSelection(list, selection);
        }
        return selection;
    }

    public Selection get(Instant value) {
        return get(PackedInstant.pack(value));
    }

    public Selection get(LocalDateTime value) {
        return get(PackedLocalDateTime.pack(value));
    }

    public Selection atLeast(long value) {
        Selection selection = new BitmapBackedSelection();
        Long2ObjectSortedMap<IntArrayList> tail = index.tailMap(value);
        for (IntArrayList keys : tail.values()) {
            addAllToSelection(keys, selection);
        }
        return selection;
    }

    public Selection atLeast(Instant value) {
        return atLeast(PackedInstant.pack(value));
    }

    public Selection atLeast(LocalDateTime value) {
        return atLeast(PackedLocalDateTime.pack(value));
    }

    public Selection greaterThan(long value) {
        Selection selection = new BitmapBackedSelection();
        Long2ObjectSortedMap<IntArrayList> tail = index.tailMap(value + 1);
        for (IntArrayList keys : tail.values()) {
            addAllToSelection(keys, selection);
        }
        return selection;
    }

    public Selection greaterThan(Instant value) {
        return greaterThan(PackedInstant.pack(value));
    }

    public Selection greaterThan(LocalDateTime value) {
        return greaterThan(PackedLocalDateTime.pack(value));
    }

    public Selection atMost(long value) {
        Selection selection = new BitmapBackedSelection();
        Long2ObjectSortedMap<IntArrayList> head = index.headMap(value + 1);  // we add 1 to get values equal to the arg
        for (IntArrayList keys : head.values()) {
            addAllToSelection(keys, selection);
        }
        return selection;
    }

    public Selection atMost(Instant value) {
        return atMost(PackedInstant.pack(value));
    }

    public Selection atMost(LocalDateTime value) {
        return atMost(PackedLocalDateTime.pack(value));
    }

    public Selection lessThan(long value) {
        Selection selection = new BitmapBackedSelection();
        Long2ObjectSortedMap<IntArrayList> head = index.headMap(value);  // we add 1 to get values equal to the arg
        for (IntArrayList keys : head.values()) {
            addAllToSelection(keys, selection);
        }
        return selection;
    }

    public Selection lessThan(Instant value) {
        return lessThan(PackedInstant.pack(value));
    }

    public Selection lessThan(LocalDateTime value) {
        return lessThan(PackedLocalDateTime.pack(value));
    }
}