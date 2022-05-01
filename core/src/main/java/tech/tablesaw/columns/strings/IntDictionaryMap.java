package tech.tablesaw.columns.strings;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.booleans.BooleanColumnType;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/** A map that supports reversible key value pairs of int-String */
public class IntDictionaryMap implements DictionaryMap {

  // The maximum number of unique values or categories that I can hold. If the column has more
  // unique values,
  // use a TextColumn
  private static final int MAX_UNIQUE = Integer.MAX_VALUE;

  private static final int MISSING_VALUE = Integer.MAX_VALUE;

  private static final int DEFAULT_RETURN_VALUE = Integer.MIN_VALUE;

  private final boolean canPromoteToText = Boolean.TRUE;

  private final IntComparator reverseDictionarySortComparator =
      (i, i1) -> Comparator.<String>reverseOrder().compare(getValueForKey(i), getValueForKey(i1));

  private final IntComparator dictionarySortComparator =
      (i, i1) -> getValueForKey(i).compareTo(getValueForKey(i1));

  // holds a key for each element in the column. the key can be used to lookup the backing string
  // value
  private IntArrayList values = new IntArrayList();

  private AtomicInteger nextIndex = new AtomicInteger(DEFAULT_RETURN_VALUE);

  // we maintain 3 maps, one from strings to keys, one from keys to strings, and one from key to
  // count of values
  private Int2ObjectMap<String> keyToValue = new Int2ObjectOpenHashMap<>();

  private Object2IntOpenHashMap<String> valueToKey = new Object2IntOpenHashMap<>();

  private Int2IntOpenHashMap keyToCount = new Int2IntOpenHashMap();

  /** Returns a new DictionaryMap that is a deep copy of the original */
  IntDictionaryMap(DictionaryMap original) throws NoKeysAvailableException {
    valueToKey.defaultReturnValue(DEFAULT_RETURN_VALUE);
    keyToCount.defaultReturnValue(0);

    for (int i = 0; i < original.size(); i++) {
      String value = original.getValueForIndex(i);
      append(value);
    }
  }

  private IntDictionaryMap(IntDictionaryBuilder builder) {
    this.nextIndex = builder.nextIndex;
    this.keyToValue = builder.keyToValue;
    this.valueToKey = builder.valueToKey;
    this.keyToCount = builder.keyToCount;
    this.values = builder.values;
  }

  private void put(int key, String value) {
    keyToValue.put(key, value);
    valueToKey.put(value, key);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IntDictionaryMap that = (IntDictionaryMap) o;

    boolean a = Objects.equal(values, that.values);
    boolean b = Objects.equal(keyToValue, that.keyToValue);
    boolean c = Objects.equal(valueToKey, that.valueToKey);
    boolean d = Objects.equal(keyToCount, that.keyToCount);
    boolean e = Objects.equal(nextIndex.get(), that.nextIndex.get());
    return a && b && c && d && e;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        reverseDictionarySortComparator,
        dictionarySortComparator,
        values,
        nextIndex,
        keyToValue,
        valueToKey,
        keyToCount);
  }

  /** Returns the number of elements (a.k.a. rows or cells) in the column */
  @Override
  public int size() {
    return values.size();
  }

  public IntArrayList values() {
    return values;
  }

  private int getKeyForValue(String value) {
    return valueToKey.getInt(value);
  }

  public Int2IntMap.FastEntrySet getKeyCountEntries() {
    return keyToCount.int2IntEntrySet();
  }

  public ObjectSet<Int2ObjectMap.Entry<String>> getKeyValueEntries() {
    return keyToValue.int2ObjectEntrySet();
  }

  @Override
  public String getValueForIndex(int rowIndex) {
    int k = values.getInt(rowIndex);
    return getValueForKey(k);
  }

  @Override
  public int getKeyForIndex(int rowIndex) {
    return values.getInt(rowIndex);
  }

  private Set<String> categories() {
    return valueToKey.keySet();
  }

  private Int2ObjectMap<String> keyToValueMap() {
    return keyToValue;
  }

  @Override
  public void sortAscending() {
    int[] elements = values.toIntArray();
    IntArrays.parallelQuickSort(elements, dictionarySortComparator);
    this.values = new IntArrayList(elements);
  }

  /** {@inheritDoc} */
  @Override
  public int getKeyAtIndex(int rowNumber) {
    return values.getInt(rowNumber);
  }

  @Override
  public String getValueForKey(int key) {
    return keyToValue.get(key);
  }

  @Override
  public void sortDescending() {
    int[] elements = values.toIntArray();
    IntArrays.parallelQuickSort(elements, reverseDictionarySortComparator);
    this.values = new IntArrayList(elements);
  }

  public int countOccurrences(String value) {
    return keyToCount.get(getKeyForValue(value));
  }

  public Set<String> asSet() {
    return new HashSet<>(categories());
  }

  public int firstIndexOf(String value) {
    return values.indexOf(getKeyForValue(value));
  }

  @Override
  public String[] asObjectArray() {
    final String[] output = new String[size()];
    for (int i = 0; i < size(); i++) {
      output[i] = getValueForIndex(i);
    }
    return output;
  }

  @Override
  public int countUnique() {
    return keyToValueMap().size();
  }

  @Override
  public Selection selectIsIn(String... strings) {
    IntOpenHashSet keys = new IntOpenHashSet(strings.length);
    for (String string : strings) {
      int key = getKeyForValue(string);
      if (key != DEFAULT_RETURN_VALUE) {
        keys.add(key);
      }
    }

    Selection results = new BitmapBackedSelection();
    for (int i = 0; i < values.size(); i++) {
      if (keys.contains(values.getInt(i))) {
        results.add(i);
      }
    }
    return results;
  }

  @Override
  public Selection selectIsIn(Collection<String> strings) {
    IntOpenHashSet keys = new IntOpenHashSet(strings.size());
    for (String string : strings) {
      int key = getKeyForValue(string);
      if (key != DEFAULT_RETURN_VALUE) {
        keys.add(key);
      }
    }

    Selection results = new BitmapBackedSelection();
    for (int i = 0; i < values.size(); i++) {
      if (keys.contains(values.getInt(i))) {
        results.add(i);
      }
    }
    return results;
  }

  @Override
  public void append(String value) throws NoKeysAvailableException {
    int key;
    if (value == null || StringColumnType.missingValueIndicator().equals(value)) {
      key = MISSING_VALUE;
      put(key, StringColumnType.missingValueIndicator());
    } else {
      key = getKeyForValue(value);
    }
    if (key == DEFAULT_RETURN_VALUE) {
      key = getValueId();
      put(key, value);
    }
    values.add(key);
    keyToCount.addTo(key, 1);
  }

  private int getValueId() throws NoKeysAvailableException {
    int nextValue = nextIndex.incrementAndGet();
    if (nextValue >= Integer.MAX_VALUE) {
      String msg = String.format("String column can only contain %d unique values.", MAX_UNIQUE);
      throw new NoKeysAvailableException(msg);
    }
    return nextValue;
  }

  /**
   * Given a key matching some string, add to the selection the index of every record that matches
   * that key
   */
  private void addValuesToSelection(Selection results, int key) {
    if (key != DEFAULT_RETURN_VALUE) {
      int i = 0;
      for (int next : values) {
        if (key == next) {
          results.add(i);
        }
        i++;
      }
    }
  }

  @Override
  public void set(int rowIndex, String stringValue) throws NoKeysAvailableException {
    String str = StringColumnType.missingValueIndicator();
    if (stringValue != null) {
      str = stringValue;
    }
    int valueId = getKeyForValue(str);
    if (valueId == DEFAULT_RETURN_VALUE) {
      valueId = getValueId();
      put(valueId, str);
    }
    int oldKey = values.set(rowIndex, valueId);
    keyToCount.addTo(valueId, 1);
    if (keyToCount.addTo(oldKey, -1) == 1) {
      String obsoleteValue = keyToValue.remove(oldKey);
      valueToKey.removeInt(obsoleteValue);
      keyToCount.remove(oldKey);
    }
  }

  @Override
  public void clear() {
    nextIndex = new AtomicInteger(DEFAULT_RETURN_VALUE);
    values.clear();
    keyToValue.clear();
    valueToKey.clear();
    keyToCount.clear();
  }

  /** */
  @Override
  public Table countByCategory(String columnName) {
    Table t = Table.create("Column: " + columnName);
    StringColumn categories = StringColumn.create("Category");
    IntColumn counts = IntColumn.create("Count");
    // Now uses the keyToCount map
    for (Map.Entry<Integer, Integer> entry : keyToCount.int2IntEntrySet()) {
      categories.append(getValueForKey(entry.getKey()));
      counts.append(entry.getValue());
    }
    t.addColumns(categories);
    t.addColumns(counts);
    return t;
  }

  @Override
  public Selection isEqualTo(String string) {
    Selection results = new BitmapBackedSelection();
    int key = getKeyForValue(string);
    addValuesToSelection(results, key);
    return results;
  }

  /**
   * Returns a list of boolean columns suitable for use as dummy variables in, for example,
   * regression analysis, select a column of categorical data must be encoded as a list of columns,
   * such that each column represents a single category and indicates whether it is present (1) or
   * not present (0)
   *
   * @return a list of {@link BooleanColumn}
   */
  @Override
  public List<BooleanColumn> getDummies() {
    List<BooleanColumn> results = new ArrayList<>();

    // createFromCsv the necessary columns
    for (Int2ObjectMap.Entry<String> entry : keyToValueMap().int2ObjectEntrySet()) {
      BooleanColumn column = BooleanColumn.create(entry.getValue());
      results.add(column);
    }

    // iterate over the values, updating the dummy variable columns as appropriate
    for (int next : values) {
      String category = getValueForKey(next);
      for (BooleanColumn column : results) {
        if (category.equals(column.name())) {
          column.append(BooleanColumnType.BYTE_TRUE);
        } else {
          column.append(BooleanColumnType.BYTE_FALSE);
        }
      }
    }
    return results;
  }

  /** Returns the contents of the cell at rowNumber as a byte[] */
  @Override
  public byte[] asBytes(int rowNumber) {
    return ByteBuffer.allocate(byteSize()).putInt(getKeyForIndex(rowNumber)).array();
  }

  private int byteSize() {
    return 4;
  }

  /** Returns the count of missing values in this column */
  @Override
  public int countMissing() {
    return keyToCount.get(MISSING_VALUE);
  }

  @Override
  public Iterator<String> iterator() {
    return new Iterator<>() {

      private final IntListIterator valuesIt = values.iterator();

      @Override
      public boolean hasNext() {
        return valuesIt.hasNext();
      }

      @Override
      public String next() {
        return getValueForKey(valuesIt.nextInt());
      }
    };
  }

  @Override
  public void appendMissing() {
    try {
      append(StringColumnType.missingValueIndicator());
    } catch (NoKeysAvailableException e) {
      // This can't happen because missing value key is the first one allocated
      throw new IllegalStateException(e);
    }
  }

  @Override
  public boolean isMissing(int rowNumber) {
    return getKeyForIndex(rowNumber) == MISSING_VALUE;
  }

  @Override
  public DictionaryMap promoteYourself() {
    if (canPromoteToText) {
      return new NullDictionaryMap(this);
    }
    return this;
  }

  @Override
  public int nextKeyWithoutIncrementing() {
    return nextIndex.get();
  }

  @Override
  public boolean canPromoteToText() {
    return canPromoteToText;
  }

  public static class IntDictionaryBuilder {

    private AtomicInteger nextIndex;

    // The list of keys that represents the contents of string column in user order
    private IntArrayList values;

    // we maintain 3 maps, one from strings to keys, one from keys to strings, and one from key to
    // count of values
    private Int2ObjectMap<String> keyToValue;

    // the inverse of the above keyToValue map
    private Object2IntOpenHashMap<String> valueToKey;

    // the map with counts
    private Int2IntOpenHashMap keyToCount;

    public IntDictionaryBuilder setNextIndex(int value) {
      nextIndex = new AtomicInteger(value);
      return this;
    }

    public IntDictionaryBuilder setKeyToValue(Int2ObjectMap<String> keyToValue) {
      this.keyToValue = keyToValue;
      return this;
    }

    public IntDictionaryBuilder setValueToKey(Object2IntOpenHashMap<String> valueToKey) {
      this.valueToKey = valueToKey;
      return this;
    }

    public IntDictionaryBuilder setKeyToCount(Int2IntOpenHashMap keyToCount) {
      this.keyToCount = keyToCount;
      return this;
    }

    public IntDictionaryBuilder setValues(int[] data) {
      this.values = new IntArrayList(data);
      return this;
    }

    public IntDictionaryMap build() {
      Preconditions.checkNotNull(nextIndex);
      Preconditions.checkNotNull(keyToCount);
      Preconditions.checkNotNull(keyToValue);
      Preconditions.checkNotNull(valueToKey);
      Preconditions.checkNotNull(values);
      return new IntDictionaryMap(this);
    }
  }
}
