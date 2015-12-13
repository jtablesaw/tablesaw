package com.deathrayresearch.outlier;

import java.util.List;

/**
 * A tablular data structure like a table in a relational database, but not formally implementing the relational algebra
 */
public interface Relation {

  /**
   * Returns the column at columnIndex (0-based)
   * @param columnIndex an integer >= 0 and < number of columns in the relation
   * @return the column at the given index
   */
  Column column(int columnIndex);

  /**
   * Returns the number of columns in the relation
   */
  int columnCount();

  /**
   * Returns the number of rows in the relation
   */
  int rowCount();

  /**
   * Returns a list of all the columns in the relation
   */
  List<Column> getColumns();

  /**
   * Returns the index of the column with the given name
   */
  int columnIndex(String col);

  /**
   * Returns a String representing the value found at column index c and row index r
   */
  String get(int c, int r);

  /**
   * Adds the given column to the end of this relation.
   *
   * The index of the new column in the table will be one less than the number of columns
   */
  void addColumn(Column column);

  /**
   * Returns the column with the given columnName
   */
  Column column(String columnName);

  /**
   * Returns the name of this relation
   */
  String name();

  /**
   * Returns a copy of this relation with no data, but with the same name and column structure
   */
  Relation emptyCopy();

  /**
   * Clears all the dat in the relation, leaving the structure intact
   */
  void clear();

  /**
   * Returns the unique identifier for this relation
   */
  String id();
}
