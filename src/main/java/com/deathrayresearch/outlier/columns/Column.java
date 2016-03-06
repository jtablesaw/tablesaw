package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Relation;
import com.google.common.base.MoreObjects;
import it.unimi.dsi.fastutil.ints.IntComparator;

/**
 *
 */
public interface Column {

  int size();

  Relation summary();

  int countUnique();

  String name();

  ColumnType type();

  boolean hasNext();

  String getString(int row);

  Column emptyCopy();

  void clear();

  Column sortAscending();

  Column sortDescending();

  boolean isEmpty();

  void addCell(String stringValue);

  /**
   * Returns a unique string that identifies this column
   */
  String id();

  /**
   * Returns a String containing the column's metadata in json format
   */
  String metadata();


  IntComparator rowComparator();

  default String first() {
    return getString(0);
  }

  default String last() {
    return getString(size() - 1);
  }
}
