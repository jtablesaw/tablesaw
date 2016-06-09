package com.deathrayresearch.outlier.columns;

import com.deathrayresearch.outlier.Relation;
import it.unimi.dsi.fastutil.ints.IntComparator;
import org.roaringbitmap.RoaringBitmap;

import java.util.List;

/**
 * The general interface for columns.
 *
 * Columns can either exist on their own or be a part of a table. All the data in a single column is of a particular
 * type.
 */
public interface Column {

  int size();

  Relation summary();

  default Column subset(RoaringBitmap rows) {
    Column c = this.emptyCopy();
    for (Integer row : rows) {
      c.addCell(getString(row));
    }
    return c;
  }

  int countUnique();

  Object max(int n);
  Object min(int n);

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

  void sortAscending();

  void sortDescending();

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

  void append(Column column);

  default Column first(int numRows) {
    Column col = emptyCopy();
    int rows = Math.min(numRows, size());
    for (int i = 0; i < rows; i++) {
      col.addCell(getString(i));
    }
    return col;
  }

  default Column last(int numRows) {
    Column col = emptyCopy();
    int rows = Math.min(numRows, size());
    for (int i = size() - rows; i < size(); i++) {
      col.addCell(getString(i));
    }
    return col;
  }

  String print();

  default String title() {
    return "Column: " + name() + '\n';
  }

  List<ChangeLog.ChangeLogEntry> getChangeLog();

  String comment();

  void setComment(String comment);
}
