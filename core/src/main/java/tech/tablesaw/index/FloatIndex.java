package tech.tablesaw.index;

import it.unimi.dsi.fastutil.floats.Float2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.util.BitmapBackedSelection;
import tech.tablesaw.util.Selection;

/**
 * An index for four-byte floating point columns
 */
public class FloatIndex {

    private final Float2ObjectAVLTreeMap<IntArrayList> index;

    public FloatIndex(FloatColumn column) {
        int sizeEstimate = Integer.min(1_000_000, column.size() / 100);
        Float2ObjectOpenHashMap<IntArrayList> tempMap = new Float2ObjectOpenHashMap<>(sizeEstimate);
        for (int i = 0; i < column.size(); i++) {
            float value = column.get(i);
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

    private static void addAllToSelection(IntArrayList tableKeys, Selection selection) {
        for (int i : tableKeys) {
            selection.add(i);
        }
    }

    /**
     * Returns a bitmap containing row numbers of all cells matching the given int
     *
     * @param value This is a 'key' from the index perspective, meaning it is a value from the standpoint of the column
     */
    public Selection get(float value) {
        Selection selection = new BitmapBackedSelection();
        IntArrayList list = index.get(value);
        addAllToSelection(list, selection);
        return selection;
    }

    public Selection atLeast(float value) {
        Selection selection = new BitmapBackedSelection();
        Float2ObjectSortedMap<IntArrayList> tail = index.tailMap(value);
        for (IntArrayList keys : tail.values()) {
            addAllToSelection(keys, selection);
        }
        return selection;
    }

    public Selection greaterThan(float value) {
        Selection selection = new BitmapBackedSelection();
        Float2ObjectSortedMap<IntArrayList> tail = index.tailMap(value + 0.000001f);
        for (IntArrayList keys : tail.values()) {
            addAllToSelection(keys, selection);
        }
        return selection;
    }

    public Selection atMost(float value) {
        Selection selection = new BitmapBackedSelection();
        Float2ObjectSortedMap<IntArrayList> head = index.headMap(value + 0.000001f);  // we add 1 to get values equal
        // to the arg
        for (IntArrayList keys : head.values()) {
            addAllToSelection(keys, selection);
        }
        return selection;
    }

    public Selection lessThan(float value) {
        Selection selection = new BitmapBackedSelection();
        Float2ObjectSortedMap<IntArrayList> head = index.headMap(value);  // we add 1 to get values equal to the arg
        for (IntArrayList keys : head.values()) {
            addAllToSelection(keys, selection);
        }
        return selection;
    }
}