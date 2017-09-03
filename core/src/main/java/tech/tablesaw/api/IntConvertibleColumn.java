package tech.tablesaw.api;

import java.util.Set;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

public interface IntConvertibleColumn {

    int[] toIntArray();

    default Set<Integer> asSet() {
      return new IntOpenHashSet(toIntArray());
    }

}
