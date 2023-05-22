package tech.tablesaw.columns.strings;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2IntOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
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

/** A map that supports reversible key value pairs of short-String */
public class ShortDictionaryMap extends DictionaryMap<Short> {

  @Override
  protected Short getDefaultKeyValue() {
    return Short.MIN_VALUE;
  }

  @Override
  protected Short getDefaultMissingValue() {
    return Short.MAX_VALUE;
  }

  @Override
  protected Short getMaxUnique() {
    return (short) (Short.MAX_VALUE - Short.MIN_VALUE);
  }

  @Override
  protected int getByteSize() {
    return Integer.SIZE / Byte.SIZE;
  }

  /** Returns a new DictionaryMap that is a deep copy of the original */
  ShortDictionaryMap(ByteDictionaryMap original) throws NoKeysAvailableException {
    values = new ShortArrayList();
    keyToValue = new Short2ObjectOpenHashMap<>();
    valueToKey = new Object2ShortOpenHashMap<String>();
    keyToCount = new Short2IntOpenHashMap();
    canPromoteToText = original.canPromoteToText();
    for (int i = 0; i < original.size(); i++) {
      String value = original.getValueForIndex(i);
      append(value);
    }
  }

  private ShortDictionaryMap(ShortDictionaryBuilder builder) {
    this.nextIndex = builder.nextIndex;
    this.keyToValue = builder.keyToValue;
    this.valueToKey = builder.valueToKey;
    this.keyToCount = builder.keyToCount;
    this.canPromoteToText = builder.canPromoteToText;
    this.values = builder.values;
  }

  public Set<Map.Entry<Short, String>> getKeyValueEntries() {
    return keyToValue.entrySet();
  }

  public Set<Map.Entry<Short, Integer>> getKeyCountEntries() {
    return keyToCount.entrySet();
  }

  public ShortArrayList values() {
    return (ShortArrayList) values;
  }

  @Override
  public DictionaryMap promoteYourself() {

    DictionaryMap dictionaryMap;

    if (canPromoteToText && countUnique() > size() * 0.5) {
      dictionaryMap = new NullDictionaryMap(this);
    } else {
      try {
        dictionaryMap = new IntDictionaryMap(this);
      } catch (NoKeysAvailableException e) {
        // this should never happen;
        throw new IllegalStateException(e);
      }
    }

    return dictionaryMap;
  }

  public static class ShortDictionaryBuilder {

    private AtomicInteger nextIndex;

    // The list of keys that represents the contents of string column in user order
    private ShortArrayList values;

    // we maintain 3 maps, one from strings to keys, one from keys to strings, and one from key to
    // count of values
    private Short2ObjectMap<String> keyToValue;

    // the inverse of the above keyToValue map
    private Object2ShortOpenHashMap<String> valueToKey;

    // the map with counts
    private Short2IntOpenHashMap keyToCount;

    private boolean canPromoteToText = true;

    public ShortDictionaryBuilder setNextIndex(int value) {
      nextIndex = new AtomicInteger(value);
      return this;
    }

    public ShortDictionaryBuilder setKeyToValue(Short2ObjectMap<String> keyToValue) {
      this.keyToValue = keyToValue;
      return this;
    }

    public ShortDictionaryBuilder setCanPromoteToText(boolean canPromoteToText) {
      this.canPromoteToText = canPromoteToText;
      return this;
    }

    public ShortDictionaryBuilder setValueToKey(Object2ShortOpenHashMap<String> valueToKey) {
      this.valueToKey = valueToKey;
      return this;
    }

    public ShortDictionaryBuilder setKeyToCount(Short2IntOpenHashMap keyToCount) {
      this.keyToCount = keyToCount;
      return this;
    }

    public ShortDictionaryBuilder setValues(short[] data) {
      this.values = new ShortArrayList(data);
      return this;
    }

    public ShortDictionaryMap build() {
      Preconditions.checkNotNull(nextIndex);
      Preconditions.checkNotNull(keyToCount);
      Preconditions.checkNotNull(keyToValue);
      Preconditions.checkNotNull(valueToKey);
      Preconditions.checkNotNull(values);
      return new ShortDictionaryMap(this);
    }
  }
}
