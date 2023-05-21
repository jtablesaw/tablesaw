package tech.tablesaw.columns.strings;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.bytes.ByteComparator;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.booleans.BooleanColumnType;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

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

  protected AtomicInteger nextIndex = new AtomicInteger((int) getDefaultKeyValue());

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
    return (int) values.get(rowNumber);
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
    return keyToCount.get(valueToKey.get(value));
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
    return (int) values.get(i);
  }

  public int firstIndexOf(String value) {
    return values.indexOf(valueToKey.get(value));
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
    T key = getDefaultKeyValue();
    if (value == null || StringColumnType.missingValueIndicator().equals(value)) {
      key = getDefaultMissingValue();
      put(key, StringColumnType.missingValueIndicator());
    }

    if (valueToKey.containsKey(key)) {
      key = (T) getValueId();
      put(key, value);
    }
    values.add(key);
    keyToCount.put(key, 1);
  }

  public void set(int rowIndex, String stringValue) throws NoKeysAvailableException {
    String str = StringColumnType.missingValueIndicator();
    if (stringValue != null) {
      str = stringValue;
    }
    T valueId = getDefaultKeyValue();

    if (valueToKey.containsKey(str)) { // this is a new value not in dictionary
      valueId = (T) getValueId();
      put(valueId, str);
    }

    T oldKey = values.set(rowIndex, valueId);
    keyToCount.put(valueId, 1);
    if (keyToCount.put(oldKey, -1) == 1) {
      String obsoleteValue = keyToValue.remove(oldKey);
      valueToKey.remove(obsoleteValue);
      keyToCount.remove(oldKey);
    }
  }

  public void clear() {
    nextIndex = new AtomicInteger((Integer) getDefaultKeyValue());
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
    return ByteBuffer.allocate((Integer) getByteSize()).put((byte) getKeyForIndex(rowNumber)).array();
  }

  /** Returns the count of missing values in this column */
  public int countMissing() {
    return keyToCount.get(getDefaultMissingValue());
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
    return getKeyForIndex(rowNumber) == (int) getDefaultMissingValue();
  }

  public abstract DictionaryMap promoteYourself();

  public int nextKeyWithoutIncrementing() {
    return nextIndex.get();
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

  private Number getValueId() throws NoKeysAvailableException {
    int nextValue = nextIndex.incrementAndGet();
    if (nextValue >= Byte.MAX_VALUE) {
      String msg =
              String.format(
                      "String column can only contain %d unique values. Column has more.", getMaxUnique());
      throw new NoKeysAvailableException(msg);
    }
    return nextValue;
  }
}
