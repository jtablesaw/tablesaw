package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;
import com.deathrayresearch.outlier.filter.text.StringFilters;
import com.deathrayresearch.outlier.io.TypeUtils;
import com.deathrayresearch.outlier.mapper.StringMapUtils;
import com.deathrayresearch.outlier.util.DictionaryMap;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import org.roaringbitmap.RoaringBitmap;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A column in a base table that contains float values
 */
public class CategoryColumn extends AbstractColumn implements StringMapUtils, StringFilters {

  public static final String MISSING_VALUE = (String) ColumnType.CAT.getMissingValue();

  private static int DEFAULT_ARRAY_SIZE = 128;

  // For internal iteration. What element are we looking at right now
  private int pointer = 0;

  // The number of elements, which may be less than the size of the array
  private int N = 0;

  // initialize the unique value id number to the smallest possible short to maximize range
  private AtomicInteger id = new AtomicInteger(0);

  // holds a key for each row in the table. the key can be used to lookup the backing string value
  private ShortList values;

  // a bidirectional map of keys to backing string values.
  private DictionaryMap lookupTable = new DictionaryMap();

  public static CategoryColumn create(String name) {
    return create(name, DEFAULT_ARRAY_SIZE);
  }

  public static CategoryColumn create(String name, int size) {
    return new CategoryColumn(name);
  }

  public CategoryColumn(String name) {
    super(name);
    values = new ShortArrayList(DEFAULT_ARRAY_SIZE);
  }

  @Override
  public ColumnType type() {
    return ColumnType.CAT;
  }

  @Override
  public boolean hasNext() {
    return pointer + 1 < N;
  }

  public String next() {
    return lookupTable.get(values.getShort(pointer++));
  }

  // TODO(lwhite): implement?
  private void resize() {
  }

  /**
   * Removes (most) extra space (empty elements) from the data array
   */
  public void compact() {
    //TODO(lwhite): Implement?
  }


  @Override
  public String getString(int row) {
    return get(row);
  }

  @Override
  public CategoryColumn emptyCopy() {
    return new CategoryColumn(name());
  }

  // TODO(lwhite): Implement?
  public void reset() {
  }

  // TODO(lwhite): review if reference assignment of data (values, lookupTable) is appropriate or copy needed
  private CategoryColumn copy() {
    CategoryColumn copy = emptyCopy();
    copy.lookupTable = this.lookupTable;
    copy.values = this.values;
    return copy;
  }

  // TODO(lwhite): Implement sorting
  @Override
  public Column sortAscending() {
    CategoryColumn copy = this.copy();
    //Arrays.sort(copy.data);
    return copy;
  }

  // TODO(lwhite): Implement sorting
  @Override
  public Column sortDescending() {
    CategoryColumn copy = this.copy();
/*
    Arrays.sort(copy.data);
    Primitive.sort(copy.data, (d1, d2) -> Float.compare(d2, d1), false);
*/
    return copy;
  }

  /**
   * Returns the number of elements (a.k.a. rows or cells) in the column
   */

  @Override
  public int size() {
    return values.size();
  }

  /**
   * Returns the value at rowIndex in this column. The index is zero-based.
   *
   * @throws IndexOutOfBoundsException if the given rowIndex is not in the column
   */
  public String get(int rowIndex) {
    short k = values.getShort(rowIndex);
    return lookupTable.get(k);
  }

  public short getKey(int index) {
    return values.getShort(index);
  }

  @Override
  public Table summary() {
    Table t = new Table(name());
    CategoryColumn categories = CategoryColumn.create("Category");
    IntColumn counts = IntColumn.create("Count");

    Object2IntOpenHashMap<String> valueToKey = new Object2IntOpenHashMap<>();

    while (this.hasNext()) {
      String category = this.next();
      if (valueToKey.containsKey(category)) {
        valueToKey.addTo(category, 1);
      } else {
        valueToKey.put(category, 1);
      }
    }
    for (Object2IntOpenHashMap.Entry<String> entry : valueToKey.object2IntEntrySet()) {
      categories.add(entry.getKey());
      counts.add(entry.getIntValue());
    }
    t.addColumn(categories);
    t.addColumn(counts);
    return t;
  }

  @Override
  public void clear() {
    values.clear();
    lookupTable.clear();
  }

  public void set(int rowIndex, String stringValue) {
    boolean b = lookupTable.contains(stringValue);
    short valueId;
    if (!b) {
      valueId = (short) id.getAndIncrement();
      lookupTable.put(valueId, stringValue);
    } else {
      valueId = lookupTable.get(stringValue);
    }

    values.set(rowIndex, valueId);
  }

  @Override
  public int countUnique() {
    return lookupTable.size();
  }

  public void add(String stringValue) {
    boolean b = lookupTable.contains(stringValue);
    short valueId;
    if (!b) {
      valueId = (short) id.getAndIncrement();
      lookupTable.put(valueId, stringValue);
    } else {
      valueId = lookupTable.get(stringValue);
    }
    values.add(valueId);
    N++;
  }

  public final IntComparator rowComparator = new IntComparator() {

    @Override
    public int compare(int i, int i1) {
      return getString(i).compareTo(getString(i1));
    }

    @Override
    public int compare(Integer o1, Integer o2) {
      System.out.println("Comparaing on objects in CatColumn");
      return getString(o1).compareTo(getString(o2));
    }
  };

  public static String convert(String stringValue) {
    if (Strings.isNullOrEmpty(stringValue) || TypeUtils.MISSING_INDICATORS.contains(stringValue)) {
      return (String) ColumnType.CAT.getMissingValue();
    }
    return stringValue;
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

  @Override
  public IntComparator rowComparator() {
    return rowComparator;
  }

  @Override
  public boolean isEmpty() {
    return values.isEmpty();
  }

  public RoaringBitmap isEqualTo(String string) {
    RoaringBitmap results = new RoaringBitmap();
    int i = 0;
    while (hasNext()) {
      if (string.equals(next())) {
        results.add(i);
      }
      i++;
    }
    reset();
    return results;
  }

  public int getInt(int rowNumber) {
    return values.get(rowNumber);
  }

  public Collection<? extends String> valueSet() {
    return lookupTable.categories();
  }

  public DictionaryMap dictionaryMap() {
    return lookupTable;
  }
}
