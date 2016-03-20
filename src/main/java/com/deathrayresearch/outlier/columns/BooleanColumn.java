package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.io.TypeUtils;
import com.deathrayresearch.outlier.mapper.BooleanMapUtils;
import com.deathrayresearch.outlier.store.ColumnMetadata;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.booleans.BooleanComparator;
import it.unimi.dsi.fastutil.booleans.BooleanOpenHashSet;
import it.unimi.dsi.fastutil.booleans.BooleanSet;
import it.unimi.dsi.fastutil.ints.IntComparator;
import org.roaringbitmap.IntIterator;
import org.roaringbitmap.RoaringBitmap;

import java.util.HashMap;
import java.util.Map;

/**
 * A column in a base table that contains float values
 */
public class BooleanColumn extends AbstractColumn implements BooleanMapUtils {

  private static int DEFAULT_ARRAY_SIZE = 128;

  private BooleanArrayList data;

  public static BooleanColumn create(String name) {
    return new BooleanColumn(name);
  }

  public BooleanColumn(ColumnMetadata metadata) {
    super(metadata);
    data = new BooleanArrayList(DEFAULT_ARRAY_SIZE);
  }

  private BooleanColumn(String name) {
    super(name);
    data = new BooleanArrayList(DEFAULT_ARRAY_SIZE);
  }

  private BooleanColumn(String name, int initialSize) {
    super(name);
    data = new BooleanArrayList(initialSize);
  }

  private BooleanColumn(String name, BooleanArrayList values) {
    super(name);
    data = values;
  }

  public BooleanColumn(String name, RoaringBitmap hits, int columnSize) {
    super(name);
    if (columnSize == 0) {
      return;
    }
    BooleanArrayList data = BooleanArrayList.wrap(new boolean[columnSize]);
    IntIterator it = hits.getIntIterator();
    while(it.hasNext()) {
      data.set(it.next(), true);
    }
    this.data = data;
  }

  public int size() {
    return data.size();
  }

  @Override
  public Table summary() {

    Map<Boolean, Integer> counts = new HashMap<>(3);
    counts.put(true, 0);
    counts.put(false, 0);

    for (boolean next : data) {
      counts.put(next, counts.get(next) + 1);
    }

    Table table = new Table(name());

    BooleanColumn booleanColumn = BooleanColumn.create("Value");
    IntColumn countColumn = IntColumn.create("Count");
    table.addColumn(booleanColumn);
    table.addColumn(countColumn);

    for (Map.Entry<Boolean, Integer> entry : counts.entrySet()) {
      booleanColumn.add(entry.getKey());
      countColumn.add(entry.getValue());
    }
    return table;
  }

  @Override
  public int countUnique() {
    BooleanSet count = new BooleanOpenHashSet(3);
    for (boolean next : data) {
      count.add(next);
    }
    return count.size();
  }

  @Override
  public BooleanColumn unique() {
    BooleanSet count = new BooleanOpenHashSet(3);
    for (boolean next : data) {
      count.add(next);
    }
    BooleanArrayList list = new BooleanArrayList(count);
    return new BooleanColumn(name() + " Unique values", list);
  }

  @Override
  public ColumnType type() {
    return ColumnType.BOOLEAN;
  }

  public void add(boolean f) {
    data.add(f);
  }

  @Override
  public String getString(int row) {
    return String.valueOf(get(row));
  }

  @Override
  public BooleanColumn emptyCopy() {
    return new BooleanColumn(name());
  }

  @Override
  public void clear() {
    data.clear();
  }

  private BooleanColumn copy() {
    BooleanColumn copy = emptyCopy();
    for (boolean b : data) {
      copy.add(b);
    }
    return copy;
  }

  @Override
  public BooleanColumn sortAscending() {
    BooleanColumn copy = copy();
    BooleanArrays.mergeSort(copy.data.elements());
    return copy;
  }

  @Override
  public Column sortDescending() {
    BooleanColumn copy = copy();
    BooleanArrays.mergeSort(copy.data.elements(), reverseBooleanComparator);
    return copy;
  }

  BooleanComparator reverseBooleanComparator =  new BooleanComparator() {

    @Override
    public int compare(Boolean o1, Boolean o2) {
      return Boolean.compare(o2, o1);
    }

    @Override
    public int compare(boolean o1, boolean o2) {
      return Boolean.compare(o2, o1);
    }
  };

  public static boolean convert(String stringValue) {
    if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
      return (boolean) ColumnType.BOOLEAN.getMissingValue();
    } else if (TypeUtils.TRUE_STRINGS.contains(stringValue)) {
      return true;
    } else if (TypeUtils.FALSE_STRINGS.contains(stringValue)) {
      return false;
    } else {
      throw new IllegalArgumentException("Attempting to convert non-boolean value " +
          stringValue + " to Boolean");
    }
  }

  public void addCell(String object) {
    try {
      add(convert(object));
    } catch (NullPointerException e) {
      throw new RuntimeException(name() + ": "
          + String.valueOf(object) + ": "
          + e.getMessage());
    }
  }

  public boolean get(int i) {
    return data.getBoolean(i);
  }

  @Override
  public boolean isEmpty() {
    return data.isEmpty();
  }

  public static BooleanColumn create(String fileName, BooleanArrayList bools) {
    BooleanColumn booleanColumn = new BooleanColumn(fileName, bools.size());
    booleanColumn.data = bools;
    return booleanColumn;
  }

  public int countTrue() {
    int count = 0;
    for (boolean b : data) {
      if (b) {
        count++;
      }
    }
    return count;
  }

  public int countFalse() {
    int count = 0;
    for (boolean b : data) {
      if (!b) {
        count++;
      }
    }
    return count;
  }

  public RoaringBitmap isFalse() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (boolean next : data) {
      if (!next) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public RoaringBitmap isTrue() {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    for (boolean next : data) {
      if (next) {
        results.add(i);
      }
      i++;
    }
    return results;
  }

  public BooleanArrayList data() {
    return data;
  }

  public void set(int i, boolean b) {
    data.set(i, b);
  }

  @Override
  public IntComparator rowComparator() {
    return comparator;
  }

  @Override
  public void appendColumnData(Column column) {
    Preconditions.checkArgument(column.type() == this.type());
    BooleanColumn booleanColumn = (BooleanColumn) column;
    for (int i = 0; i < booleanColumn.size(); i++) {
      add(booleanColumn.get(i));
    }
  }

  IntComparator comparator = new IntComparator() {

    @Override
    public int compare(Integer r1, Integer r2) {
      return compare((int) r1, (int) r2);
    }

    @Override
    public int compare(int r1, int r2) {
      boolean f1 = get(r1);
      boolean f2 = get(r2);
      return Boolean.compare(f1, f2);
    }
  };

  public String print() {
    StringBuilder builder = new StringBuilder();
    builder.append(title());
    for (boolean next : data) {
      builder.append(String.valueOf(next));
      builder.append('\n');
    }
    return builder.toString();
  }

  @Override
  public String toString() {
    return "Boolean column: " + name();
  }
}
