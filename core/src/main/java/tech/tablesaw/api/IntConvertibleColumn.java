package tech.tablesaw.api;

import java.util.Set;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

public interface IntConvertibleColumn {

    int[] toIntArray();

    default Set<Integer> asIntegerSet() {
      return new IntOpenHashSet(toIntArray());
    }

}
