package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.columns.CategoryColumn;
import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.PeriodColumn;
import com.deathrayresearch.outlier.sorting.Sort;
import com.deathrayresearch.outlier.splitter.functions.Average;
import com.deathrayresearch.outlier.store.TableMetadata;
import com.deathrayresearch.outlier.util.IntComparatorChain;
import com.deathrayresearch.outlier.util.IntSort;
import com.deathrayresearch.outlier.util.ReverseIntComparator;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.deathrayresearch.outlier.sorting.Sort.Order;

/**
 *
 */
public class Table implements Relation {

  private String id = UUID.randomUUID().toString();

  private String name;

  private final List<Column> columnList = new ArrayList<>();

  public Table(String name) {
    this.name = name;
  }

  public Table(TableMetadata metadata) {
    this.name = metadata.getName();
    this.id = metadata.getId();
  }

  @Override
  public void addColumn(Column column) {
    columnList.add(column);
  }

  @Override
  public void setName(String name) {
    this.name = name;
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
  public List<Column> columns() {
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
      throw new IllegalArgumentException(String.format("Column %s is not present in table %s", columnName, name));
    }
    return columnIndex;
  }

  public int columnIndex(Column column) {
    int columnIndex = -1;
    for (int i = 0; i < columnList.size(); i++) {
      if (columnList.get(i).equals(column)) {
        columnIndex = i;
        break;
      }
    }
    if (columnIndex == -1) {
      throw new IllegalArgumentException(String.format("Column %s is not present in table %s", column.name(), name));
    }
    return columnIndex;
  }

  @Override
  public String name() {
    return name;
  }

  /**
   * Returns a List of the names of all the columns in this table
   */
  public List<String> columnNames() {
    List<String> names = new ArrayList<>(columnList.size());
    for (Column column : columnList) {
      names.add(column.name());
    }
    return names;
  }

  @Override
  public int row(int r) {
    return r;
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

  public String id() {
    return id;
  }

  /**
   * Returns a new table containing the first {@code nrows} of data in this table
   */
  public View head(int nRows) {
    return new View(this, Math.min(nRows, rowCount()));
  }

  /**
   * Returns a sort Key that can be used for simple or chained comparator sorting
   * <p>
   * You can extend the sort key by using .next() to fill more columns to the sort order
   */
  private Sort first(String columnName, Sort.Order order) {
    return Sort.on(columnName, order);
  }

  /**
   * Returns a copy of this table sorted on the given column names, applied in order, ascending
   */
  public Table sortOn(String... columnNames) {
    Sort key = null;
    for (String s : columnNames) {
      if (key == null) {
        key = first(s, Order.ASCEND);
      } else {
        key.next(s, Order.ASCEND);
      }
    }
    return sortOn(key);
  }

  public Table sortAscendingOn(String... columnNames) {
    return this.sortOn(columnNames);
  }

  /**
   * Returns a copy of this table sorted on the given column names, applied in order, descending
   */
  public Table sortDescendingOn(String... columnNames) {
    Sort key = null;
    for (String s : columnNames) {
      if (key == null) {
        key = first(s, Order.DESCEND);
      } else {
        key.next(s, Order.DESCEND);
      }
    }
    return sortOn(key);
  }

  /**
   * Returns a copy of this table sorted on the given columns
   * <p>
   * The columns are sorted in reverse order if they value matching the name is {@code true}
   */
  public Table sortOn(Sort key) {
    Preconditions.checkArgument(!key.isEmpty());
    Iterator<Map.Entry<String, Sort.Order>> entries = key.iterator();
    Map.Entry<String, Sort.Order> sort = entries.next();

    IntComparator comparator;
    if (sort.getValue() == Order.ASCEND) {
      comparator = rowComparator(sort.getKey(), false);
    } else {
      comparator = rowComparator(sort.getKey(), true);
    }

    if (key.size() == 1) {
      return sortOn(comparator);
    }

    IntComparatorChain chain = new IntComparatorChain(comparator);
    while (entries.hasNext()) {
      sort = entries.next();
      if (sort.getValue() == Order.ASCEND) {
        chain.addComparator(rowComparator(sort.getKey(), false));
      } else {
        chain.addComparator(rowComparator(sort.getKey(), true));
      }
    }
    return sortOn(chain);
  }

  /**
   * Returns a copy of this table sorted using the given comparator
   */
  public Table sortOn(IntComparator rowComparator) {
    Table newTable = (Table) emptyCopy();
/*    int[] integers = new int[rowCount()];
    for (int i = 0; i < rowCount(); i++) {
      integers[i] = i;
    }*/


    Integer[] integers = new Integer[rowCount()];
    for (int i = 0; i < rowCount(); i++) {
      integers[i] = i;
    }

    Arrays.parallelSort(integers, rowComparator);
    IntArrayList newRows = new IntArrayList(rowCount());
    newRows.addAll(Arrays.asList(integers).subList(0, rowCount()));

    Rows.copyRowsToTable(newRows, this, newTable);
    return newTable;
  }

  private IntArrayList rows() {
    IntArrayList rowIndexes = new IntArrayList(rowCount());
    for (int i = 0; i < rowCount(); i++) {
      rowIndexes.add(i);
    }
    return rowIndexes;
  }

  /**
   * Returns a comparator for the column matching the specified name
   *
   * @param columnName The name of the column to sort
   * @param reverse    {@code true} if the column should be sorted in reverse
   */
  private IntComparator rowComparator(String columnName, Boolean reverse) {

    Column column = this.column(columnName);
    IntComparator rowComparator = column.rowComparator();

    if (reverse) {
      return ReverseIntComparator.reverse(rowComparator);
    } else {
      return rowComparator;
    }
  }

  public Query select() {
    return new Query(this);
  }

  public Query select(String ... columnName) {
    return new Query(this, columnName);
  }

  /**
   * Removes the given column
   */
  @Override
  public void removeColumn(Column column) {
    columnList.remove(column);
  }

  public Average average(String summarizedColumnName) {
    return new Average(this, summarizedColumnName);
  }

  public CategoryColumn categoryColumn(String pdDistrict) {
    return (CategoryColumn) column(pdDistrict);
  }

  public PeriodColumn periodColumn(int i) {
    return (PeriodColumn) column(i);
  }
}
