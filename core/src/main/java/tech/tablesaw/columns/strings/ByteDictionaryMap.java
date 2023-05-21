package tech.tablesaw.columns.strings;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.bytes.*;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.booleans.BooleanColumnType;
import tech.tablesaw.selection.BitmapBackedSelection;
import tech.tablesaw.selection.Selection;

/** A map that supports reversible key value pairs of int-String */
public class ByteDictionaryMap extends DictionaryMap<Byte> {

  public ByteDictionaryMap() {
    values = new ByteArrayList();
    keyToValue = new Byte2ObjectOpenHashMap<>();
    valueToKey = new Object2ByteOpenHashMap<String>();
    keyToCount = new Byte2IntOpenHashMap();
  }

  public ByteDictionaryMap(boolean canPromoteToText) {
    values = new ByteArrayList();
    keyToValue = new Byte2ObjectOpenHashMap<>();
    valueToKey = new Object2ByteOpenHashMap<String>();
    keyToCount = new Byte2IntOpenHashMap();
    this.canPromoteToText = canPromoteToText;
  }

  private ByteDictionaryMap(ByteDictionaryBuilder builder) {
    this.nextIndex = builder.nextIndex;
    this.keyToValue = builder.keyToValue;
    this.valueToKey = builder.valueToKey;
    this.keyToCount = builder.keyToCount;
    this.values = builder.values;
  }

  public ByteArrayList values() {
    return (ByteArrayList) values;
  }

  public Set<Map.Entry<Byte, String>> getKeyValueEntries() {
    return keyToValue.entrySet();
  }

  public Set<Map.Entry<Byte, Integer>> getKeyCountEntries() {
    return keyToCount.entrySet();
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
  protected Byte getDefaultKeyValue() {
    return Byte.MIN_VALUE;
  }

  @Override
  protected Byte getDefaultMissingValue() {
    return Byte.MAX_VALUE;
  }

  @Override
  protected Byte getMaxUnique() {
    return (byte) (Byte.MAX_VALUE - Byte.MIN_VALUE);
  }

  @Override
  protected int getByteSize() {
    return Byte.SIZE / Byte.SIZE;
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
