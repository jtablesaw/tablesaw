package tech.tablesaw.index;


import it.unimi.dsi.fastutil.bytes.Byte2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.util.SortedMap;

/**
 * A marker interface for all index types
 *
 * <p>Indexes are implemented as maps where each entry connects a column value to the set of row
 * indexes corresponding to the value
 */
public abstract class Index{

    public <T>Selection get(T value) {
        Selection selection = new BitmapBackedSelection();
        IntArrayList list = getIndexList(value);
        if (list != null) {
            addAllToSelection(list, selection);
        }
        return selection;
    }

    /** Returns a {@link Selection} of all values greater than the given value */
    public <T> Selection greaterThan(T value) {

        SortedMap<T, IntArrayList> tail = GTgetTailMap(value);
        return addMapToSelection(tail);
    }

    public <T> Selection atLeast(T value) {
        SortedMap<T, IntArrayList> tail = aLgetTailMap(value);

        return addMapToSelection(tail);
    }

    /** Returns a {@link Selection} of all values at most as large as the given value */
    public <T>Selection atMost(T value) {
        SortedMap<T,IntArrayList> head = aMgetheadMap(value);

        return addMapToSelection(head);
    }

    /** Returns a {@link Selection} of all values less than the given value */
    public <T> Selection lessThan(T value) {
        SortedMap<T,IntArrayList> head = LTgetheadMap(value);

        return addMapToSelection(head);
    }

    private <T> Selection addMapToSelection(SortedMap<T, IntArrayList> map) {
        Selection selection = new BitmapBackedSelection();
        for (IntArrayList keys : map.values()) {
            addAllToSelection(keys, selection);
        }
        return selection;
    }

    protected abstract <T> SortedMap<T, IntArrayList> GTgetTailMap(T value);
    protected abstract <T> SortedMap<T, IntArrayList> aLgetTailMap(T value);
    protected abstract <T> SortedMap<T, IntArrayList> aMgetheadMap(T value);
    protected abstract <T> SortedMap<T, IntArrayList> LTgetheadMap(T value);
    protected abstract <T> IntArrayList getIndexList(T value);


    public void addAllToSelection(IntArrayList tableKeys, Selection selection) {
        for (int i : tableKeys) {
            selection.add(i);
        }
    }
}
