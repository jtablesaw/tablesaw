package tech.tablesaw.columns.strings;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/** A map that supports reversible key value pairs of int-String */
public class IntDictionaryMap extends DictionaryMap<Integer> {

  @Override
  protected Integer getDefaultKeyValue() {
    return Integer.MIN_VALUE;
  }

  @Override
  protected Integer getDefaultMissingValue() {
    return Integer.MAX_VALUE;
  }

  @Override
  protected Integer getMaxUnique() {
    return Integer.MAX_VALUE;
  }

  @Override
  protected int getByteSize() {
    return Integer.SIZE / Byte.SIZE;
  }


  /** Returns a new DictionaryMap that is a deep copy of the original */
  IntDictionaryMap(DictionaryMap original) throws NoKeysAvailableException {
    values = new IntArrayList();
    keyToValue = new Int2ObjectOpenHashMap<>();
    valueToKey = new Object2IntOpenHashMap<String>();
    keyToCount = new Int2IntOpenHashMap();

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

  /** Returns the number of elements (a.k.a. rows or cells) in the column */

  public IntArrayList values() {
    return (IntArrayList) values;
  }

  public Set<Map.Entry<Integer, Integer>> getKeyCountEntries() {
    return keyToCount.entrySet();
  }

  public Set<Map.Entry<Integer, String>> getKeyValueEntries() {
    return keyToValue.entrySet();
  }

  @Override
  public DictionaryMap promoteYourself() {
    if (canPromoteToText) {
      return new NullDictionaryMap(this);
    }
    return this;
  }

  public static class IntDictionaryBuilder {

    private AtomicReference<Integer> nextIndex;

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
      nextIndex = new AtomicReference<Integer>(value);
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

  @Override
  protected Integer getValueId() throws NoKeysAvailableException {

    int nextValue = nextIndex.updateAndGet((v)->v+1);
    if (nextValue >= getMaxUnique()) {
      String msg =
              String.format(
                      "String column can only contain %d unique values. Column has more.", getMaxUnique());
      throw new NoKeysAvailableException(msg);
    }
    return nextValue;
  }
}
