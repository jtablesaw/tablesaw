package tech.tablesaw.columns.strings;

import com.google.common.base.Preconditions;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.booleans.BooleanColumnType;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Interface implemented by the objects that perform the dictionary encoding of the Strings in
 * StringColumn, as well as the primitive values that represent the individual instances of the
 * String in the column.
 */
public abstract class DictionaryMap<T extends Number> implements StringReduceUtils, StringFilters {

  protected List<T> values;
  protected Map<T, String> keyToValue;
  protected Map<String, T> valueToKey;
  protected Map<T, Integer> keyToCount;

  protected boolean canPromoteToText = Boolean.TRUE;

  protected AtomicReference<T> nextIndex = new AtomicReference<>(getDefaultKeyValue());

  private final Comparator<T> reverseDictionarySortComparator =
          (i, i1) ->
                  Comparator.<String>reverseOrder().compare(keyToValue.get(i), keyToValue.get(i1));

  private final Comparator<T> dictionarySortComparator =
          (i, i1) -> keyToValue.get(i).compareTo(keyToValue.get(i1));

  public void sortDescending() {
    values = values.stream().sorted(reverseDictionarySortComparator).collect(Collectors.toList());
  }

  public void sortAscending() {
    values = values.stream().sorted(dictionarySortComparator).collect(Collectors.toList());
  }

  /** Returns the int that represents the string at rowNumber */
  public int getKeyAtIndex(int rowNumber) {
    T value = values.get(rowNumber);
    if (value != null) return value.intValue();
    else return getDefaultKeyValue().intValue();
  }

  public String getValueForKey(T key) {
    return keyToValue.get(key);
  }

  public int size() {
    return values.size();
  }

  public String getValueForIndex(int rowIndex) {
    T k = values.get(rowIndex);
    return keyToValue.get(k);
  }

  public int countOccurrences(String value) {
    T key = valueToKey.get(value);
    return key == null ? 0 : keyToCount.get(key);
  }

  public Set<String> asSet() {
    return new HashSet<>(categories());
  }

  private Set<String> categories() {
    return valueToKey.keySet();
  }

  /** Returns the number of unique values at or before the given index */
  public int uniqueValuesAt(int index) {
    int result = 0;
    List<String> uniqueValues = new ArrayList<>();
    for (int i = 0; i <= index; i++) {
      String value = getValueForIndex(i);
      int uniqueIndex = uniqueValues.indexOf(value);
      if (uniqueIndex < 0) {
        uniqueValues.add(value);
        result++;
      }
    }
    return result;
  }

  public int[] asIntArray() {
    int[] result = new int[size()];
    List<String> uniqueValues = new ArrayList<>();
    for (int i = 0; i < size(); i++) {
      String value = getValueForIndex(i);
      int uniqueIndex = uniqueValues.indexOf(value);
      if (uniqueIndex < 0) {
        uniqueValues.add(value);
        uniqueIndex = uniqueValues.size() - 1;
      }
      result[i] = uniqueIndex;
    }
    return result;
  }

  public int getKeyForIndex(int i) {
    return values.get(i).intValue();
  }

  public int firstIndexOf(String value) {
    T key = valueToKey.get(value);
    return key == null ? -1 : values.indexOf(key);
  }

  public String[] asObjectArray() {
    final String[] output = new String[size()];
    for (int i = 0; i < size(); i++) {
      output[i] = getValueForIndex(i);
    }
    return output;
  }

  public Selection selectIsIn(String... strings) {
    Set<T> keys = new HashSet<>();
    for (String string : strings) {
      if (valueToKey.containsKey(string)) {
        keys.add(valueToKey.get(string));
      }
    }

    Selection results = new BitmapBackedSelection();
    for (int i = 0; i < values.size(); i++) {
      if (keys.contains(values.get(i))) {
        results.add(i);
      }
    }
    return results;
  }

  public Selection selectIsIn(Collection<String> strings) {
    Set<T> keys = new HashSet<>();
    for (String string : strings) {
      if (valueToKey.containsKey(string)) {
        keys.add(valueToKey.get(string));
      }
    }

    Selection results = new BitmapBackedSelection();
    for (int i = 0; i < values.size(); i++) {
      if (keys.contains(values.get(i))) {
        results.add(i);
      }
    }
    return results;
  }

  public void append(String value) throws NoKeysAvailableException {
    T key = null;
    if (value == null || StringColumnType.missingValueIndicator().equals(value)) {
      key = getDefaultMissingValue();
      value = StringColumnType.missingValueIndicator();
    }

    if (valueToKey.containsKey(value)) {
      key = valueToKey.get(value);
      keyToCount.put(key, keyToCount.get(key) + 1);
    } else {
      if (key == null) key = getValueId();
      put(key, value);
      keyToCount.put(key, 1);
    }
    values.add(key);
  }

  public void set(int rowIndex, String value) throws NoKeysAvailableException {
    T key;
    if (value == null || StringColumnType.missingValueIndicator().equals(value)) {
      key = getDefaultMissingValue();
      put(key, StringColumnType.missingValueIndicator());
    }else if (valueToKey.containsKey(value)) {
      key = valueToKey.get(value);
      keyToCount.put(key, keyToCount.get(key) + 1);
    } else {
      key = getValueId();
      put(key, value);
      keyToCount.put(key, 1);
    }
    T oldKey = values.get(rowIndex);
    if (keyToValue.containsKey(oldKey)) {
      String oldValue = keyToValue.get(oldKey);
      if (keyToCount.get(oldKey) == 1) {
        keyToCount.remove(oldKey);
        keyToValue.remove(oldKey);
        valueToKey.remove(oldValue);
      } else {
        keyToCount.put(oldKey, keyToCount.get(oldKey) - 1);
      }
    }
    values.set(rowIndex, key);
  }

  public void clear() {
    nextIndex = new AtomicReference<>((getDefaultKeyValue()));
    values.clear();
    keyToValue.clear();
    valueToKey.clear();
    keyToCount.clear();
  }

  public int countUnique() {
    return keyToValue.keySet().size();
  }

  public Table countByCategory(String columnName) {
    Table t = Table.create("Column: " + columnName);
    StringColumn categories = StringColumn.create("Category");
    IntColumn counts = IntColumn.create("Count");
    // Now uses the keyToCount map
    for (Map.Entry<T, Integer> entry : keyToCount.entrySet()) {
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
    T key = valueToKey.get(string);
    addValuesToSelection(results, key);
    return results;
  }

  /**
   * Given a key matching some string, add to the selection the index of every record that matches
   * that key
   */
  private void addValuesToSelection(Selection results, T key) {
    if (key != null) {
      int i = 0;
      for (T next : values) {
        if (key == next) {
          results.add(i);
        }
        i++;
      }
    }
  }

  public Selection isNotEqualTo(String string) {
    Selection selection = new BitmapBackedSelection();
    selection.addRange(0, size());
    selection.andNot(isEqualTo(string));
    return selection;
  }

  @Override
  public String get(int index) {
    return getValueForIndex(index);
  }

  @Override
  public Selection isIn(String... strings) {
    return selectIsIn(strings);
  }

  @Override
  public Selection isIn(Collection<String> strings) {
    return selectIsIn(strings);
  }

  @Override
  public Selection isNotIn(String... strings) {
    Selection results = new BitmapBackedSelection();
    results.addRange(0, size());
    results.andNot(isIn(strings));
    return results;
  }

  @Override
  public Selection isNotIn(Collection<String> strings) {
    Selection results = new BitmapBackedSelection();
    results.addRange(0, size());
    results.andNot(isIn(strings));
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
  public List<BooleanColumn> getDummies() {
    List<BooleanColumn> results = new ArrayList<>();

    // createFromCsv the necessary columns
    for (Map.Entry<T, String> entry : keyToValue.entrySet()) {
      BooleanColumn column = BooleanColumn.create(entry.getValue());
      results.add(column);
    }

    // iterate over the values, updating the dummy variable columns as appropriate
    for (T next : values) {
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
  public byte[] asBytes(int rowNumber) {
    return ByteBuffer.allocate(getByteSize()).put((byte) getKeyForIndex(rowNumber)).array();
  }

  /** Returns the count of missing values in this column */
  public int countMissing() {
    Integer val = keyToCount.get(getDefaultMissingValue());
    return val == null ? 0 : val;
  }

  @Override
  public Iterator<String> iterator() {
    return new Iterator<>() {

      private final Iterator<T> valuesIt = values.iterator();

      @Override
      public boolean hasNext() {
        return valuesIt.hasNext();
      }

      @Override
      public String next() {
        return getValueForKey(valuesIt.next());
      }
    };
  }

  public void appendMissing() {
    try {
      append(StringColumnType.missingValueIndicator());
    } catch (NoKeysAvailableException e) {
      // This can't happen because missing value key is the first one allocated
      throw new IllegalStateException(e);
    }
  }

  public boolean isMissing(int rowNumber) {
    return getKeyForIndex(rowNumber) == getDefaultMissingValue().intValue();
  }

  public abstract DictionaryMap promoteYourself();

  public int nextKeyWithoutIncrementing() {
    return nextIndex.get().intValue();
  }

  public boolean canPromoteToText() {
    return canPromoteToText;
  }
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(values, keyToValue, valueToKey, keyToCount, reverseDictionarySortComparator, dictionarySortComparator);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ByteDictionaryMap that = (ByteDictionaryMap) o;

    boolean isValuesEqual = com.google.common.base.Objects.equal(values, that.values);
    boolean isKeyToValueEqual = com.google.common.base.Objects.equal(keyToValue, that.keyToValue);
    boolean isValueToKeyEqual = com.google.common.base.Objects.equal(valueToKey, that.valueToKey);
    boolean isKeyToCountEqual = com.google.common.base.Objects.equal(keyToCount, that.keyToCount);
    boolean isNextIndexEqual = com.google.common.base.Objects.equal(nextIndex.get(), that.nextIndex.get());

    return isValuesEqual
            && isKeyToValueEqual
            && isValueToKeyEqual
            && isKeyToCountEqual
            && isNextIndexEqual;
  }

  protected void put(T key, String value) {
    keyToValue.put(key, value);
    valueToKey.put(value, key);
  }

  protected abstract T getDefaultKeyValue();

  protected abstract T getDefaultMissingValue();

  protected abstract T getMaxUnique();

  protected abstract int getByteSize();

  protected abstract T getValueId() throws NoKeysAvailableException;

  public static abstract class DictionaryMapBuilder<E extends DictionaryMap<N>, N extends Number> {
    protected AtomicReference<N> nextIndex;

    // The list of keys that represents the contents of string column in user order
    protected List<N> values;

    // we maintain 3 maps, one from strings to keys, one from keys to strings, and one from key to
    // count of values
    protected Map<N, String> keyToValue;

    // the inverse of the above keyToValue map
    protected Map<String, N> valueToKey;

    // the map with counts
    protected Map<N, Integer> keyToCount;

    protected boolean canPromoteToText = true;

    public DictionaryMapBuilder<E, N> setNextIndex(N value) {
      nextIndex = new AtomicReference<N>(value);
      return this;
    }

    public DictionaryMapBuilder<E, N> setValues(N[] values) {
      this.values = List.of(values);
      return this;
    }

    public DictionaryMapBuilder<E, N> setKeyToValue(Map<N, String> keyToValue) {
      this.keyToValue = keyToValue;
      return this;
    }

    public DictionaryMapBuilder<E, N> setValueToKey(Map<String, N> valueToKey) {
      this.valueToKey = valueToKey;
      return this;
    }

    public DictionaryMapBuilder<E, N> setKeyToCount(Map<N, Integer> keyToCount) {
      this.keyToCount = keyToCount;
      return this;
    }

    public DictionaryMapBuilder<E, N> setCanPromoteToText(boolean canPromoteToText) {
      this.canPromoteToText = canPromoteToText;
      return this;
    }

    public E build() {
      Preconditions.checkNotNull(nextIndex);
      Preconditions.checkNotNull(keyToCount);
      Preconditions.checkNotNull(keyToValue);
      Preconditions.checkNotNull(valueToKey);
      Preconditions.checkNotNull(values);

      return createTarget();
    }

    protected abstract E createTarget();
  }
}
