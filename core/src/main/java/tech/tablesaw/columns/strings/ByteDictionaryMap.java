package tech.tablesaw.columns.strings;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.bytes.Byte2IntMap;
import it.unimi.dsi.fastutil.bytes.Byte2IntOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteListIterator;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
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
public class ByteDictionaryMap implements DictionaryMap {

  // The maximum number of unique values or categories that I can hold. If the column has more
  // unique values,
  // use a TextColumn
  private static final int MAX_UNIQUE = Byte.MAX_VALUE - Byte.MIN_VALUE;

  private static final byte MISSING_VALUE = Byte.MAX_VALUE;

  private static final byte DEFAULT_RETURN_VALUE = Byte.MIN_VALUE;

  private boolean canPromoteToText = Boolean.TRUE;

  private final ByteComparator reverseDictionarySortComparator =
      (i, i1) ->
          Comparator.<String>reverseOrder().compare(getValueForByteKey(i), getValueForByteKey(i1));

  private final ByteComparator dictionarySortComparator =
      (i, i1) -> getValueForByteKey(i).compareTo(getValueForByteKey(i1));

  // holds a key for each element in the column. the key can be used to lookup the backing string
  // value
  private ByteArrayList values = new ByteArrayList();

  private AtomicInteger nextIndex = new AtomicInteger(DEFAULT_RETURN_VALUE);

  // we maintain 3 maps, one from strings to keys, one from keys to strings, and one from key to
  // count of values
  private Byte2ObjectMap<String> keyToValue = new Byte2ObjectOpenHashMap<>();

  // the inverse of the keyToValue map
  private Object2ByteOpenHashMap<String> valueToKey = new Object2ByteOpenHashMap<>();

  // the map with counts
  private Byte2IntOpenHashMap keyToCount = new Byte2IntOpenHashMap();

  /** {@inheritDoc} */
  @Override
  public int getKeyAtIndex(int rowNumber) {
    return values.getByte(rowNumber);
  }

  public ByteDictionaryMap() {
    valueToKey.defaultReturnValue(DEFAULT_RETURN_VALUE);
    keyToCount.defaultReturnValue(0);
  }

  public ByteDictionaryMap(boolean canPromoteToText) {
    valueToKey.defaultReturnValue(DEFAULT_RETURN_VALUE);
    keyToCount.defaultReturnValue(0);
    this.canPromoteToText = canPromoteToText;
  }

  private ByteDictionaryMap(ByteDictionaryBuilder builder) {
    this.nextIndex = builder.nextIndex;
    this.keyToValue = builder.keyToValue;
    this.valueToKey = builder.valueToKey;
    this.keyToCount = builder.keyToCount;
    this.values = builder.values;
  }

  private void put(byte key, String value) {
    keyToValue.put(key, value);
    valueToKey.put(value, key);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ByteDictionaryMap that = (ByteDictionaryMap) o;

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

  public ByteArrayList values() {
    return values;
  }

  private byte getKeyForValue(String value) {
    return valueToKey.getByte(value);
  }

  public ObjectSet<Byte2ObjectMap.Entry<String>> getKeyValueEntries() {
    return keyToValue.byte2ObjectEntrySet();
  }

  public Byte2IntMap.FastEntrySet getKeyCountEntries() {
    return keyToCount.byte2IntEntrySet();
  }

  @Override
  public String getValueForIndex(int rowIndex) {
    byte k = values.getByte(rowIndex);
    return getValueForKey(k);
  }

  @Override
  public int getKeyForIndex(int rowIndex) {
    return values.getByte(rowIndex);
  }

  private Set<String> categories() {
    return valueToKey.keySet();
  }

  private Byte2ObjectMap<String> keyToValueMap() {
    return keyToValue;
  }

  @Override
  public void sortAscending() {
    byte[] elements = values.toByteArray();
    ByteArrays.parallelQuickSort(elements, dictionarySortComparator);
    this.values = new ByteArrayList(elements);
  }

  @Override
  public String getValueForKey(int key) {
    return keyToValue.get((byte) key);
  }

  private String getValueForByteKey(byte key) {
    return keyToValue.get(key);
  }

  @Override
  public void sortDescending() {
    byte[] elements = values.toByteArray();
    ByteArrays.parallelQuickSort(elements, reverseDictionarySortComparator);
    this.values = new ByteArrayList(elements);
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
  public Selection selectIsIn(String... strings) {
    ByteOpenHashSet keys = new ByteOpenHashSet();
    for (String string : strings) {
      byte key = getKeyForValue(string);
      if (key != DEFAULT_RETURN_VALUE) {
        keys.add(key);
      }
    }

    Selection results = new BitmapBackedSelection();
    for (int i = 0; i < values.size(); i++) {
      if (keys.contains(values.getByte(i))) {
        results.add(i);
      }
    }
    return results;
  }

  @Override
  public Selection selectIsIn(Collection<String> strings) {
    ByteOpenHashSet keys = new ByteOpenHashSet();

    for (String string : strings) {
      byte key = getKeyForValue(string);
      if (key != DEFAULT_RETURN_VALUE) {
        keys.add(key);
      }
    }

    Selection results = new BitmapBackedSelection();
    for (int i = 0; i < values.size(); i++) {
      if (keys.contains(values.getByte(i))) {
        results.add(i);
      }
    }
    return results;
  }

  @Override
  public void append(String value) throws NoKeysAvailableException {
    byte key;
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

  private byte getValueId() throws NoKeysAvailableException {
    int nextValue = nextIndex.incrementAndGet();
    if (nextValue >= Byte.MAX_VALUE) {
      String msg =
          String.format(
              "String column can only contain %d unique values. Column has more.", MAX_UNIQUE);
      throw new NoKeysAvailableException(msg);
    }
    return (byte) nextValue;
  }

  /**
   * Given a key matching some string, add to the selection the index of every record that matches
   * that key
   */
  private void addValuesToSelection(Selection results, byte key) {
    if (key != DEFAULT_RETURN_VALUE) {
      int i = 0;
      for (byte next : values) {
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
    byte valueId = getKeyForValue(str);

    if (valueId == DEFAULT_RETURN_VALUE) { // this is a new value not in dictionary
      valueId = getValueId();
      put(valueId, str);
    }
    byte oldKey = values.set(rowIndex, valueId);
    keyToCount.addTo(valueId, 1);
    if (keyToCount.addTo(oldKey, -1) == 1) {
      String obsoleteValue = keyToValue.remove(oldKey);
      valueToKey.removeByte(obsoleteValue);
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

  @Override
  public int countUnique() {
    return keyToValueMap().size();
  }

  /** */
  @Override
  public Table countByCategory(String columnName) {
    Table t = Table.create("Column: " + columnName);
    StringColumn categories = StringColumn.create("Category");
    IntColumn counts = IntColumn.create("Count");
    // Now uses the keyToCount map
    for (Map.Entry<Byte, Integer> entry : keyToCount.byte2IntEntrySet()) {
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
    byte key = getKeyForValue(string);
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
    for (Byte2ObjectMap.Entry<String> entry : keyToValueMap().byte2ObjectEntrySet()) {
      BooleanColumn column = BooleanColumn.create(entry.getValue());
      results.add(column);
    }

    // iterate over the values, updating the dummy variable columns as appropriate
    for (byte next : values) {
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
    return ByteBuffer.allocate(byteSize()).put((byte) getKeyForIndex(rowNumber)).array();
  }

  private int byteSize() {
    return 1;
  }

  /** Returns the count of missing values in this column */
  @Override
  public int countMissing() {
    return keyToCount.get(MISSING_VALUE);
  }

  @Override
  public Iterator<String> iterator() {
    return new Iterator<>() {

      private final ByteListIterator valuesIt = values.iterator();

      @Override
      public boolean hasNext() {
        return valuesIt.hasNext();
      }

      @Override
      public String next() {
        return getValueForKey(valuesIt.nextByte());
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

    ShortDictionaryMap dictionaryMap;

    try {
      dictionaryMap = new ShortDictionaryMap(this);
    } catch (NoKeysAvailableException e) {
      // this should never happen;
      throw new IllegalStateException(e);
    }
    return dictionaryMap;
  }

  @Override
  public int nextKeyWithoutIncrementing() {
    return nextIndex.get();
  }

  @Override
  public boolean canPromoteToText() {
    return canPromoteToText;
  }

  public static class ByteDictionaryBuilder {

    private AtomicInteger nextIndex;

    // The list of keys that represents the contents of string column in user order
    private ByteArrayList values;

    // we maintain 3 maps, one from strings to keys, one from keys to strings, and one from key to
    // count of values
    private Byte2ObjectMap<String> keyToValue;

    // the inverse of the above keyToValue map
    private Object2ByteOpenHashMap<String> valueToKey;

    // the map with counts
    private Byte2IntOpenHashMap keyToCount;

    public ByteDictionaryBuilder setNextIndex(int value) {
      nextIndex = new AtomicInteger(value);
      return this;
    }

    public ByteDictionaryBuilder setKeyToValue(Byte2ObjectMap<String> keyToValue) {
      this.keyToValue = keyToValue;
      return this;
    }

    public ByteDictionaryBuilder setValueToKey(Object2ByteOpenHashMap<String> valueToKey) {
      this.valueToKey = valueToKey;
      return this;
    }

    public ByteDictionaryBuilder setKeyToCount(Byte2IntOpenHashMap keyToCount) {
      this.keyToCount = keyToCount;
      return this;
    }

    public ByteDictionaryBuilder setValues(byte[] bytes) {
      this.values = new ByteArrayList(bytes);
      return this;
    }

    public ByteDictionaryMap build() {
      Preconditions.checkNotNull(nextIndex);
      Preconditions.checkNotNull(keyToCount);
      Preconditions.checkNotNull(keyToValue);
      Preconditions.checkNotNull(valueToKey);
      Preconditions.checkNotNull(values);
      return new ByteDictionaryMap(this);
    }
  }
}
