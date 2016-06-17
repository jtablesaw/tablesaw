package com.github.lwhite1.tablesaw.index;

import com.github.lwhite1.tablesaw.columns.IntColumn;
import com.google.common.base.Stopwatch;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.roaringbitmap.RoaringBitmap;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class IntIndex {

  private final Int2ObjectAVLTreeMap<IntArrayList> index = new Int2ObjectAVLTreeMap<>();

  public IntIndex(IntColumn column) {
    for (int i = 0; i < column.size(); i++) {
      int value = column.get(i);
      IntArrayList recordIds = index.get(value);
      if (recordIds == null) {
        recordIds = new IntArrayList();
        recordIds.add(i);
        index.put(value, recordIds);
      } else {
        recordIds.add(i);
      }
    }
  }

/*
  public IntIndex(IntColumn column, boolean ignore) {
    Int2ObjectAVLTreeMap<RoaringBitmap> index = new Int2ObjectAVLTreeMap<>();
    for (int i = 0; i < column.size(); i++) {
      int value = column.get(i);
      RoaringBitmap recordIds = index.get(value);
      if (recordIds == null) {
        recordIds = new RoaringBitmap();
        recordIds.add(i);
        index.put(value, recordIds);
      } else {
        recordIds.add(i);
      }
    }
  }
*/

/*
  public IntIndex(IntColumn column, int unused) {

    int[][] sorted = new int[column.size()][2];
    int[] data = column.data().toIntArray();

    for (int i = 0; i < column.size(); i++) {
      sorted[i][0] = i;
      sorted[i][1] = data[i];
    }
    Arrays.parallelSort(sorted, intArrayComparator);

    for (int i = 0; i < sorted.length; i++) {
      int value = sorted[i][1];
      int tableKey = sorted[i][0];
      IntArrayList recordIds = index.get(value);
      if (recordIds == null) {
        recordIds = new IntArrayList();
        recordIds.add(tableKey);
        index.put(value, recordIds);
      } else {
        recordIds.add(tableKey);
      }
    }
  }
*/

/*
  public IntIndex(IntColumn column, char unused) {
    System.out.println("starting to instantiate index");
    Stopwatch stopwatch = Stopwatch.createStarted();

    int[][] sorted = new int[column.size()][2];
    int[] data = column.data().toIntArray();

    for (int i = 0; i < column.size(); i++) {
      sorted[i][0] = i;
      sorted[i][1] = data[i];
    }

    System.out.println("Index prep " + stopwatch.elapsed(TimeUnit.SECONDS));
    stopwatch.reset().start();

    Arrays.sort(sorted, intArrayComparator);

    System.out.println("Index Sort " + stopwatch.elapsed(TimeUnit.SECONDS));
    stopwatch.reset().start();


    int last = Integer.MIN_VALUE;
    int value = Integer.MIN_VALUE;
    IntArrayList recordIds = new IntArrayList();

    for (int i = 0; i < sorted.length; i++) {
      // get the current values
      int tableKey = sorted[i][0];
      value = sorted[i][1];

      // if the value has changed, store the record, update LAST, and get a new collection
      if (i != 0 && value != last) {
        index.put(last, recordIds);
        recordIds = new IntArrayList();
        last = value;
      }
      recordIds.add(tableKey);
    }
    if (value != Integer.MIN_VALUE && !recordIds.isEmpty()) {
      index.put(value, recordIds);
    }
    System.out.println("Index Build " + stopwatch.elapsed(TimeUnit.SECONDS));
  }
*/

  private final static Comparator<int[]> intArrayComparator = new Comparator<int[]>() {
    public int compare(int[] a, int[] b) {
      return Integer.compare(a[1], b[1]);
    }
  };


  /**
   * Returns a bitmap containing row numbers of all cells matching the given int
   * @param value This is a 'key' from the index perspective, meaning it is a value from the standpoint of the column
   */
  public RoaringBitmap get(int value) {
    RoaringBitmap roaringBitmap = new RoaringBitmap();
    IntArrayList list = index.get(value);
    addAllToBitmap(list, roaringBitmap);
    return roaringBitmap;
  }

  public RoaringBitmap atLeast(int value) {
    RoaringBitmap roaringBitmap = new RoaringBitmap();
    Int2ObjectSortedMap<IntArrayList> tail = index.tailMap(value);
    for (IntArrayList keys : tail.values()) {
      addAllToBitmap(keys, roaringBitmap);
    }
    return roaringBitmap;
  }

  public RoaringBitmap greaterThan(int value) {
    RoaringBitmap roaringBitmap = new RoaringBitmap();
    Int2ObjectSortedMap<IntArrayList> tail = index.tailMap(value + 1);
    for (IntArrayList keys : tail.values()) {
      addAllToBitmap(keys, roaringBitmap);
    }
    return roaringBitmap;
  }

  public RoaringBitmap atMost(int value) {
    RoaringBitmap roaringBitmap = new RoaringBitmap();
    Int2ObjectSortedMap<IntArrayList> head = index.headMap(value + 1);  // we add 1 to get values equal to the arg
    for (IntArrayList keys : head.values()) {
      addAllToBitmap(keys, roaringBitmap);
    }
    return roaringBitmap;
  }

  public RoaringBitmap lessThan(int value) {
    RoaringBitmap roaringBitmap = new RoaringBitmap();
    Int2ObjectSortedMap<IntArrayList> head = index.headMap(value);  // we add 1 to get values equal to the arg
    for (IntArrayList keys : head.values()) {
      addAllToBitmap(keys, roaringBitmap);
    }
    return roaringBitmap;
  }

  private static void addAllToBitmap(IntArrayList tableKeys, RoaringBitmap bitmap) {
    for (int i : tableKeys) {
      bitmap.add(i);
    }
  }
}