package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.columns.CategoryColumn;
import com.deathrayresearch.outlier.columns.Column;
import com.deathrayresearch.outlier.columns.IntColumn;
import com.deathrayresearch.outlier.columns.PeriodColumn;
import com.deathrayresearch.outlier.sorting.Sort;
import com.deathrayresearch.outlier.splitter.functions.Average;
import com.deathrayresearch.outlier.store.TableMetadata;
import com.deathrayresearch.outlier.util.IntComparatorChain;
import com.deathrayresearch.outlier.util.ReverseIntComparator;
import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.deathrayresearch.outlier.sorting.Sort.Order;

/**
 *
 */
public class Table implements Relation {

  private final String id;

  private String name;

  private final List<Column> columnList = new ArrayList<>();

  public Table(String name) {
    this.name = name;
    this.id = UUID.randomUUID().toString();
  }

  public Table(TableMetadata metadata) {
    this.name = metadata.getName();
    this.id = metadata.getId();
  }

  /**
   * Returns a new Table initialized with the given names and columns
   *
   * @param name    The name of the table
   * @param columns One or more columns, all of which must have either the same length or size 0
   */
  public Table(String name, Column ... columns) {
    this(name);
    for (Column column : columns) {
      this.addColumn(column);
    }
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

  // TODO(lwhite): Implement for views and add to relation api
  public List<Column> columns(String[] columnNames) {
    List<Column> columns = new ArrayList<>();
    for (String columnName : columnNames) {
      columns.add(column(columnName));
    }
    return columns;
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

  /**
   * Returns a List of the names of all the columns in this table
   */
  public String[] columnNameArray() {
    String[] names = new String[columnList.size()];
    for (int i = 0; i < columnList.size(); i++) {
      Column column = columnList.get(i);
      names[i] = column.name();
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
    return column.getString(r);
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
    View view = new View(this, Math.min(nRows, rowCount()));
    view.setName(name);
    return view;
  }

  /**
   * Returns a sort Key that can be used for simple or chained comparator sorting
   * <p>
   * You can extend the sort key by using .next() to fill more columns to the sort order
   */
  private static Sort first(String columnName, Sort.Order order) {
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
    Sort key = getSort(columnNames);
    return sortOn(key);
  }

  public static Sort getSort(String ... columnNames) {
    Sort key = null;
    for (String s : columnNames) {
      if (key == null) {
        key = first(s, Order.DESCEND);
      } else {
        key.next(s, Order.DESCEND);
      }
    }
    return key;
  }

  /**
   * Returns a copy of this table sorted on the given columns
   * <p>
   * The columns are sorted in reverse order if they value matching the name is {@code true}
   */
  public Table sortOn(Sort key) {
    Preconditions.checkArgument(!key.isEmpty());
    if (key.size() == 1) {
      IntComparator comparator = getComparator(key);
      return sortOn(comparator);
    }
    IntComparatorChain chain = getChain(key);
    return sortOn(chain);
  }

  public IntComparator getComparator(Sort key) {
    Iterator<Map.Entry<String, Sort.Order>> entries = key.iterator();
    Map.Entry<String, Sort.Order> sort = entries.next();
    IntComparator comparator;
    if (sort.getValue() == Order.ASCEND) {
      comparator = rowComparator(sort.getKey(), false);
    } else {
      comparator = rowComparator(sort.getKey(), true);
    }
    return comparator;
  }

  public IntComparatorChain getChain(Sort key) {
    Iterator<Map.Entry<String, Sort.Order>> entries = key.iterator();
    Map.Entry<String, Sort.Order> sort = entries.next();

    IntComparator comparator;
    if (sort.getValue() == Order.ASCEND) {
      comparator = rowComparator(sort.getKey(), false);
    } else {
      comparator = rowComparator(sort.getKey(), true);
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
    return chain;
  }

  /**
   * Returns a copy of this table sorted using the given comparator
   */
  public Table sortOn(IntComparator rowComparator) {
    Table newTable = (Table) emptyCopy();

    int[] newRows = rows();
    IntArrays.parallelQuickSort(newRows, rowComparator);

    Rows.copyRowsToTable(IntArrayList.wrap(newRows), this, newTable);
    return newTable;
  }

  public int[] rows() {
    int[] rowIndexes = new int[rowCount()];
    for (int i = 0; i < rowCount(); i++) {
      rowIndexes[i] = i;
    }
    return rowIndexes;
  }

  /**
   * Returns a comparator for the column matching the specified name
   *
   * @param columnName The name of the column to sort
   * @param reverse    {@code true} if the column should be sorted in reverse
   */
  private IntComparator rowComparator(String columnName, boolean reverse) {

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
   * Removes the given columns
   */
  @Override
  public void removeColumns(Column... columns) {
    for (Column c : columns)
    columnList.remove(c);
  }

  public Average average(String summarizedColumnName) {
    return new Average(this, summarizedColumnName);

  }


  public Table countBy(String byColumnName) {
    TableGroup group = new TableGroup(this, byColumnName);
    Table resultTable = new Table(name + " summary");
    CategoryColumn groupColumn = CategoryColumn.create("Group", group.size());
    IntColumn countColumn = IntColumn.create("Count", group.size());
    resultTable.addColumn(groupColumn);
    resultTable.addColumn(countColumn);

    for (SubTable subTable : group.getSubTables()) {
      int count = subTable.rowCount();
      String groupName = subTable.name();
      groupColumn.add(groupName);
      countColumn.add(count);
    }
    return resultTable;
  }

  private SubTable splitGroupingColumn(SubTable subTable, List<Column> columnNames) {
    ArrayList newColumns = new ArrayList();
    Iterator row = columnNames.iterator();

    Column c;
    while(row.hasNext()) {
      c = (Column)row.next();
      Column col = c.emptyCopy();
      newColumns.add(col);
    }

    for(int var7 = 0; var7 < subTable.rowCount(); ++var7) {
      String[] var8 = subTable.name().split("|||");

      for(int var9 = 0; var9 < newColumns.size(); ++var9) {
        ((Column)newColumns.get(var9)).addCell(var8[var9]);
      }
    }

    row = newColumns.iterator();

    while(row.hasNext()) {
      c = (Column)row.next();
      subTable.addColumn(c);
    }

    return subTable;
  }

  public Table sum(IntColumn sumColumn, Column byColumn) {
    TableGroup groupTable = new TableGroup(this, byColumn);
    Table resultTable = new Table(name + " summary");

    CategoryColumn groupColumn = CategoryColumn.create("Group", groupTable.size());
    IntColumn sumColumn1 = IntColumn.create("Sum", groupTable.size());

    resultTable.addColumn(groupColumn);
    resultTable.addColumn(sumColumn1);

    for (SubTable subTable : groupTable.getSubTables()) {
      long sum = subTable.intColumn(sumColumn.name()).sum();
      String groupName = subTable.name();
      groupColumn.add(groupName);
      sumColumn1.add((int) sum);
    }
    return resultTable;
  }

  public Table appendText(CategoryColumn sumColumn, CategoryColumn byColumn) {
    TableGroup groupTable = new TableGroup(this, byColumn);
    Table resultTable = new Table(name + " summary");

    CategoryColumn groupColumn = CategoryColumn.create("Group", groupTable.size());
    CategoryColumn sumColumn1 = CategoryColumn.create("Appended", groupTable.size());

    resultTable.addColumn(groupColumn);
    resultTable.addColumn(sumColumn1);

    for (SubTable subTable : groupTable.getSubTables()) {
      String sum = subTable.categoryColumn(sumColumn.name()).appendAll();
      String groupName = subTable.name();
      groupColumn.add(groupName);
      sumColumn1.add(sum);
    }
    return resultTable;
  }

  public Table sum(IntColumn sumColumn, String[] byColumnNames) {
    TableGroup groupTable = new TableGroup(this, byColumnNames);
    Table resultTable = new Table(name + " summary");

    CategoryColumn groupColumn = CategoryColumn.create("Group", groupTable.size());
    IntColumn sumColumn1 = IntColumn.create("Sum", groupTable.size());

    resultTable.addColumn(groupColumn);
    resultTable.addColumn(sumColumn1);

    for (SubTable subTable : groupTable.getSubTables()) {
      long sum = subTable.intColumn(sumColumn.name()).sum();
      String groupName = subTable.name();
      groupColumn.add(groupName);
      sumColumn1.add((int) sum);
    }

    return resultTable;
  }

  public CategoryColumn categoryColumn(String columnName) {
    return (CategoryColumn) column(columnName);
  }

  public CategoryColumn categoryColumn(int columnIndex) {
    return (CategoryColumn) column(columnIndex);
  }

  public PeriodColumn periodColumn(int i) {
    return (PeriodColumn) column(i);
  }

  public void append(Table tableToAppend) {
    for (Column column : columnList) {
      Column columnToAppend = tableToAppend.column(column.name());
      for (int i = 0; i < columnToAppend.size(); i++) {
        column.appendColumnData(columnToAppend);
      }
    }
  }
}
