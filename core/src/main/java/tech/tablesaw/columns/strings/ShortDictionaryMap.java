package tech.tablesaw.columns.strings;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2IntOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

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

  @Override
  protected Short getValueId() throws NoKeysAvailableException {

    Short nextValue = nextIndex.updateAndGet((v)-> (short) (v+1));
    if (nextValue.intValue() >= getMaxUnique().intValue()) {
      String msg =
              String.format(
                      "String column can only contain %d unique values. Column has more.", getMaxUnique().intValue());
      throw new NoKeysAvailableException(msg);
    }
    return nextValue;
  }

  public static class ShortDictionaryBuilder extends DictionaryMapBuilder<ShortDictionaryMap, Short> {
    @Override
    protected ShortDictionaryMap createTarget() {
      return new ShortDictionaryMap(this);
    }
  }
}
