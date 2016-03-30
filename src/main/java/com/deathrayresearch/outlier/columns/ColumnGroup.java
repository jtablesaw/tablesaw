package com.deathrayresearch.outlier.columns;

/**
 * A group of columns derived from a single base column, each containing a subset of the original values
 */
public abstract class ColumnGroup {

  public String name() {
    return getOriginal().name();
  }

  public ColumnType type() {
    return getOriginal().type();
  }

  public abstract Column getOriginal();

  /**
   * Returns the number of groups();
   */
  public abstract int groups();

  /**
   * Returns the total number of elements in all groups
   */
  public int size() {
    return getOriginal().size();
  }
}
