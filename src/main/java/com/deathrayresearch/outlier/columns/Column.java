package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Relation;
import it.unimi.dsi.fastutil.ints.IntComparator;

/**
 *
 */
public interface Column {

  int size();

  Relation summary();

  int countUnique();

  /**
   * Returns a column of the same type as the receiver, containing only the unique values of the receiver
   */
  Column unique();

  String name();

  void setName(String name);

  ColumnType type();

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

  void appendColumnData(Column column);
}
