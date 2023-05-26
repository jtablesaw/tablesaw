package tech.tablesaw.columns.strings;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.bytes.Byte2IntOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;

import java.util.Map;
import java.util.Set;

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

  @Override
  protected Byte getValueId() throws NoKeysAvailableException {
    Byte nextValue = nextIndex.updateAndGet((v)-> (byte) (v+1));
    if (nextValue.intValue() >= getMaxUnique().intValue()) {
      String msg =
              String.format(
                      "String column can only contain %d unique values. Column has more.", getMaxUnique().intValue());
      throw new NoKeysAvailableException(msg);
    }
    return nextValue;
  }

  public static class ByteDictionaryBuilder extends DictionaryMapBuilder<ByteDictionaryMap, Byte> {
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
