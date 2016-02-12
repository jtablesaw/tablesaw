package com.deathrayresearch.outlier.splitter;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 */
public class Split implements Iterable<Map.Entry<String, Splitter>> {


  private final LinkedHashMap<String, Splitter> sortOrder = new LinkedHashMap<>();

  public static Split on(String columnName, Splitter order) {
    return new Split(columnName, order);
  }

  public static Split first(String columnName, Splitter order) {
    return on(columnName, order);
  }

  public Split(String state, Splitter order) {
    next(state, order);
  }

  public Split next(String columnName, Splitter order) {
    sortOrder.put(columnName, order);
    return this;
  }

  /**
   * Returns an iterator over elements of type {@code T}.
   *
   * @return an Iterator.
   */
  @Override
  public Iterator<Map.Entry<String, Splitter>> iterator() {
    return sortOrder.entrySet().iterator();
  }
}
