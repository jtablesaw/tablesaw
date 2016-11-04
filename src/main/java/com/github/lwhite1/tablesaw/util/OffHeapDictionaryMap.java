package com.github.lwhite1.tablesaw.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import org.mapdb.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class OffHeapDictionaryMap implements Dictionary {
  private static final String basePath = "/Users/apple/depot/tablesaw2/mapdb/";

  static {
    init();
  }

  private final HTreeMap<String, Integer> uniqueValues;
  private final BTreeMap<Integer, String> reverseIndex;

  public OffHeapDictionaryMap(String columnId) {
    DB db = DBMaker.tempFileDB()
        .fileMmapEnableIfSupported()
        .closeOnJvmShutdown()
        .concurrencyScale(16)
        .make();
    uniqueValues = db.hashMap("uniqueValues")
        .keySerializer(Serializer.STRING)
        .valueSerializer(Serializer.INTEGER_PACKED)
        .counterEnable()
        .createOrOpen();
    reverseIndex = db.treeMap("reverseIndex")
        .keySerializer(Serializer.INTEGER_PACKED)
        .valueSerializer(Serializer.STRING)
        .counterEnable()
        .createOrOpen();
  }

  private static void init() {
    Path path = Paths.get(basePath);
    if (!Files.exists(path)) {
      try {
        Files.createDirectories(path);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void put(int key, String value) {
    uniqueValues.put(value, key);
    reverseIndex.put(key, value);
  }

  @Override public String get(int key) { return reverseIndex.get(key); }
  @Override public int get(String value) { return uniqueValues.getOrDefault(value, -1); }

  @Override
  public void remove(int key) {
    if (!reverseIndex.containsKey(key)) return;

    String remove = reverseIndex.remove(key);
    uniqueValues.remove(remove);
  }

  @Override
  public void remove(String value) {
    if (uniqueValues.containsKey(value)) {
      int remove = uniqueValues.remove(value);
      reverseIndex.remove(remove);
    }
  }

  @Override
  public void clear() {
    uniqueValues.clear();
    reverseIndex.clear();
  }

  @Override public boolean contains(String value) { return uniqueValues.containsKey(value); }
  @Override public int size() { return uniqueValues.size(); }
  @Override public Set<String> categories() { return uniqueValues.keySet(); }

  @Override
  public String[] categoryArray() {
    Collection<String> values = reverseIndex.values();
    return values.toArray(new String[size()]);
  }

  @Override
  public IntCollection values() {
    Collection<Integer> values = uniqueValues.values();
    return new IntArrayList(values);
  }

  @Override public Map<Integer, String> keyToValueMap() { return reverseIndex; }
  @Override public Map<String, Integer> valueToKeyMap() { return uniqueValues; }
}
