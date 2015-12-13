package com.deathrayresearch.outlier;

import org.roaringbitmap.RoaringBitmap;

/**
 *
 */
public interface Column {

  int size();

  String name();

  ColumnType type();

  boolean hasNext();

  String getString(int row);

  Column emptyCopy();

  void clear();

  Column sortAscending();

  Column sortDescending();

  /**
   * Returns a unique string that identifies this column
   */
  String id();


  /**
   * Returns a String containing the column's metadata in json format
   */
  String metadata();
}
