package tech.tablesaw.index;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import tech.tablesaw.selection.Selection;

/**
 * A marker interface for all index types
 *
 * <p>Indexes are implemented as maps where each entry connects a column value to the set of row
 * indexes corresponding to the value
 */
public interface Index {

    default void addAllToSelection(IntArrayList tableKeys, Selection selection) {
        for (int i : tableKeys) {
            selection.add(i);
        }
    }

}
