package com.deathrayresearch.outlier.util;

import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;

import java.util.Collection;
import java.util.Set;

/**
 * A map that supports reversible key value pairs of short->String
 * <p>
 * TODO(lwhite): Generify this class if it turns out to be useful
 */
public class DictionaryMap {

  private final Short2ObjectMap<String> keyToValue = new Short2ObjectOpenHashMap<>();

  private final Object2ShortMap<String> valueToKey = new Object2ShortOpenHashMap<>();

  public void put(short key, String value) {
    keyToValue.put(key, value);
    valueToKey.put(value, key);
  }

  public String get(short key) {
    return keyToValue.get(key);
  }

  public short get(String value) {
    return valueToKey.get(value);
  }

  public void remove(short key) {
    String value = keyToValue.remove(key);
    valueToKey.remove(value);
  }

  public void remove(String value) {
    short key = valueToKey.remove(value);
    keyToValue.remove(key);
  }

  public void clear() {
    keyToValue.clear();
    valueToKey.clear();
  }

  public boolean contains(String stringValue) {
    return valueToKey.containsKey(stringValue);
  }

  public short size() {
    return (short) valueToKey.size();
  }

  public Set<String> categories() {
    return valueToKey.keySet();
  }

  public Collection<Short> values() {
    return valueToKey.values();
  }

  public Short2ObjectMap<String> keyToValueMap() {
    return keyToValue;
  }
}
