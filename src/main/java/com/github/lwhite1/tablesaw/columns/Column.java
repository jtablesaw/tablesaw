package com.github.lwhite1.tablesaw.columns;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.store.ColumnMetadata;
import it.unimi.dsi.fastutil.ints.IntComparator;
import org.roaringbitmap.RoaringBitmap;

/**
 * The general interface for columns.
 * <p>
 * Columns can either exist on their own or be a part of a table. All the data in a single column is of a particular
 * type.
 */
public interface Column {

  int size();

  Table summary();

  default Column subset(RoaringBitmap rows) {
    Column c = this.emptyCopy();
    for (Integer row : rows) {
      c.addCell(getString(row));
    }
    return c;
  }

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

  /**
   * Returns an empty copy of the receiver, with its internal storage initialized to the given row size
   */
  Column emptyCopy(int rowSize);

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

  ColumnMetadata columnMetadata();

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

  String comment();

  void setComment(String comment);

  default double[] toDoubleArray() {
    throw new UnsupportedOperationException("Method toDoubleArray() is not supported on non-numeric columns");
  }

  int columnWidth();

  RoaringBitmap isMissing();

  RoaringBitmap isNotMissing();

  /**
   * Returns the width of a cell in this column, in bytes
   */
  int byteSize();

  /**
   * Returns the contents of the cell at rowNumber as a byte[]
   */
  byte[] asBytes(int rowNumber);
}
