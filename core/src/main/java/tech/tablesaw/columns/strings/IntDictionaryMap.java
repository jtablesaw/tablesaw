package tech.tablesaw.columns.strings;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.Map;
import java.util.Set;

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

  public static class IntDictionaryBuilder extends DictionaryMapBuilder<IntDictionaryMap, Integer> {
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
