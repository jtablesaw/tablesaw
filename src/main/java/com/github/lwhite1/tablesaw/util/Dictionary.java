package com.github.lwhite1.tablesaw.util;

import it.unimi.dsi.fastutil.ints.IntCollection;

import java.util.Map;
import java.util.Set;

public interface Dictionary {
  void put(int key, String value);
  String  get(int key);
  int get(String value);
  void remove(int key);
  void remove(String value);
  void clear();
  boolean contains(String value);
  int size();
  Set<String> categories();
  String[] categoryArray();
  IntCollection values();
  Map<Integer, String> keyToValueMap();
  Map<String, Integer> valueToKeyMap();

}
