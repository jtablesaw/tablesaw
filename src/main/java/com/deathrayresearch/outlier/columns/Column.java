package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Table;

import java.util.Comparator;

/**
 *
 */
public interface Column {

  int size();

  Table summary();

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


  Comparator<Integer> rowComparator();
}
