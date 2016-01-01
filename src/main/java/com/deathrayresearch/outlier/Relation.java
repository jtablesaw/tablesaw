package com.deathrayresearch.outlier;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * A tablular data structure like a table in a relational database, but not formally implementing the relational algebra
 */
public interface Relation {

  /**
   * Returns the column at columnIndex (0-based)
   *
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
   * Returns the index of the given column
   */
  int columnIndex(Column col);

  /**
   * Returns a String representing the value found at column index c and row index r
   */
  String get(int c, int r);

  /**
   * Adds the given column to the end of this relation.
   * <p>
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

  List<String> columnNames();

  /**
   * Returns an array of column widths for printing tables
   */
  default int[] colWidths() {

    int cols = columnCount();
    int[] widths = new int[cols];

    List<String> columnNames = columnNames();
    for (int i = 0; i < columnCount(); i++) {
      widths[i] = columnNames.get(i).length();
    }

    // for (Row row : this) {
    for (int rowNum = 0; rowNum < rowCount(); rowNum++) {
      for (int colNum = 0; colNum < cols; colNum++) {
        widths[colNum]
            = Math.max(widths[colNum], StringUtils.length(get(colNum, rowNum)));
      }
    }
    return widths;
  }

  default String print() {
    StringBuilder buf = new StringBuilder();

    int[] colWidths = colWidths();
    buf.append(name()).append('\n');
    List<String> names = this.columnNames();

    for (int colNum = 0; colNum < columnCount(); colNum++) {
      buf.append(
          StringUtils.rightPad(
              StringUtils.defaultString(String.valueOf(names.get(colNum))), colWidths[colNum]));
      buf.append(' ');
    }
    buf.append('\n');

    for (int r = 0; r < rowCount(); r++) {
      for (int c = 0; c < columnCount(); c++) {
        String cell = StringUtils.rightPad(get(c, r), colWidths[c]);
        buf.append(cell);
        buf.append(' ');
      }
      buf.append('\n');
    }
    return buf.toString();
  }

  void removeColumn(String columnName);
  void removeColumn(int columnIndex);
  void removeColumn(Column column);
}
