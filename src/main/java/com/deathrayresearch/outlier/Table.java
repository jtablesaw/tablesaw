package com.deathrayresearch.outlier;

import org.roaringbitmap.RoaringBitmap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public class Table implements Relation {

  private final String id = UUID.randomUUID().toString();

  private String name;

  private final List<Column> columnList = new ArrayList<>();

  public Table(String name) {
    this.name = name;
  }

  @Override
  public void addColumn(Column column) {
    columnList.add(column);
  }

  @Override
  public Column column(int columnIndex) {
    return columnList.get(columnIndex);
  }

  @Override
  public int columnCount() {
    return columnList.size();
  }

  @Override
  public int rowCount() {
    int result = 0;
    if (!columnList.isEmpty()) {
      result = columnList.get(0).size();
    }
    return result;
  }

  @Override
  public List<Column> getColumns() {
    return columnList;
  }

  public int columnIndex(String columnName) {
    int columnIndex = -1;
    for (int i = 0; i < columnList.size(); i++) {
      if (columnList.get(i).name().equalsIgnoreCase(columnName)) {
        columnIndex = i;
        break;
      }
    }
    if (columnIndex == -1) {
      throw new IllegalArgumentException(String.format("Column %s is not present in table %s", columnName , name));
    }
    return columnIndex;
  }

  @Override
  public Column column(String columnName) {
    int columnIndex = -1;
    int actualIndex = 0;
    for (Column column : columnList) {
      // TODO(lwhite): Consider caching the uppercase name and doing equals() instead of equalsIgnoreCase()
      if (column.name().equalsIgnoreCase(columnName)) {
        columnIndex = actualIndex;
        break;
      }
      actualIndex++;
    }
    if (columnIndex == -1) {
      throw new RuntimeException(String.format("Column %s does not exist in table %s", columnName, name));
    }
    return column(columnIndex);
  }

  @Override
  public String name() {
    return name;
  }

  public Relation selectIf(RoaringBitmap roaringBitmap) {
    Relation table = emptyCopy();

    while (roaringBitmap.getIntIterator().hasNext()) {
      for (Column c : columnList) {

      }
    }
    return table;
  }

  @Override
  public String get(int c, int r) {
    Column column = column(c);
    return String.valueOf(column.getString(r));
  }

  /**
   * Returns a table with the same columns as this table, but no data
   */
  @Override
  public Relation emptyCopy() {
    Relation copy = new Table(name);
    for (Column column : columnList) {
      copy.addColumn(column.emptyCopy());
    }
    return copy;
  }

  @Override
  public void clear() {
    for (Column column : columnList) {
      column.clear();
    }
  }

  public FloatColumn floatColumn(String columnName) {
    return (FloatColumn) column(columnName);
  }

  public FloatColumn floatColumn(int columnIndex) {
    return (FloatColumn) column(columnIndex);
  }

  public FloatColumn fColumn(String columnName) {
    return floatColumn(columnName);
  }

  public FloatColumn fColumn(int columnIndex) {
    return floatColumn(columnIndex);
  }

  public void print() {
    for (Column c : columnList) {
      for (int r = 0; r < rowCount(); r++) {
        System.out.println(c.getString(r));
      }
    }
  }

  public String id() {
    return id;
  }
}
