/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.tablesaw.table;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntComparator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import tech.tablesaw.aggregate.NumericAggregateFunction;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.selection.Selection;
import tech.tablesaw.sorting.Sort;
import tech.tablesaw.sorting.SortUtils;
import tech.tablesaw.sorting.comparators.IntComparatorChain;

/**
 * A TableSlice is a facade around a Relation that acts as a filter. Requests for data are forwarded
 * to the underlying table. A TableSlice can be sorted independently of the underlying table.
 *
 * <p>A TableSlice is only good until the structure of the underlying table changes.
 */
public class TableSlice extends Relation {

  /** The physical table object backing this slice */
  private final Table table;

  /** The name of the slice */
  private String name;

  /** The Selection that defines which rows in the table are present in this slice */
  @Nullable private Selection selection;

  /** An array of row indices defining the presentation order of the slice */
  @Nullable private int[] sortOrder = null;

  /**
   * Returns a new View constructed from the given table, containing only the rows represented by
   * the bitmap
   */
  public TableSlice(Table table, Selection rowSelection) {
    this.name = table.name();
    this.selection = rowSelection;
    this.table = table;
  }

  /**
   * Returns a new view constructed from the given table. The view can be sorted independently of
   * the table.
   */
  public TableSlice(Table table) {
    this.name = table.name();
    this.selection = null;
    this.table = table;
  }

  /** {@inheritDoc} */
  @Override
  public Column<?> column(int columnIndex) {
    Column<?> col = table.column(columnIndex);
    if (isSorted()) {
      return col.subset(sortOrder);
    } else if (hasSelection()) {
      return col.where(selection);
    }
    return col;
  }

  /** {@inheritDoc} */
  @Override
  public Column<?> column(String columnName) {
    return column(table.columnIndex(columnName));
  }

  /** {@inheritDoc} */
  @Override
  public int columnCount() {
    return table.columnCount();
  }

  /** {@inheritDoc} */
  @Override
  public int rowCount() {
    if (hasSelection()) {
      return selection.size();
    }
    return table.rowCount();
  }

  /** {@inheritDoc} */
  @Override
  public List<Column<?>> columns() {
    List<Column<?>> columns = new ArrayList<>();
    for (int i = 0; i < columnCount(); i++) {
      columns.add(column(i));
    }
    return columns;
  }

  /** {@inheritDoc} */
  @Override
  public int columnIndex(Column<?> column) {
    return table.columnIndex(column);
  }

  /** {@inheritDoc} */
  @Override
  public Object get(int r, int c) {
    return table.get(mappedRowNumber(r), c);
  }

  /** {@inheritDoc} */
  @Override
  public String name() {
    return name;
  }

  /** Returns the backing table for this slice */
  public Table getTable() {
    return table;
  }

  /** Clears all rows from this View, leaving the structure in place */
  @Override
  public void clear() {
    sortOrder = null;
    selection = Selection.with();
  }

  /** Removes the sort from this View. */
  public void removeSort() {
    this.sortOrder = null;
  }

  /**
   * Removes the selection from this view, leaving it with the same number of rows as the underlying
   * source table.
   */
  public void removeSelection() {
    this.selection = null;
  }

  /** {@inheritDoc} */
  @Override
  public List<String> columnNames() {
    return table.columnNames();
  }

  /** {@inheritDoc} */
  @Override
  public TableSlice addColumns(Column<?>... column) {
    throw new UnsupportedOperationException(
        "Class TableSlice does not support the addColumns operation");
  }

  /** {@inheritDoc} */
  @Override
  public TableSlice removeColumns(Column<?>... columns) {
    throw new UnsupportedOperationException(
        "Class TableSlice does not support the removeColumns operation");
  }

  /** {@inheritDoc} */
  @Override
  public Table first(int nRows) {
    int count = 0;
    PrimitiveIterator.OfInt it = sourceRowNumberIterator();
    Table copy = table.emptyCopy();
    while (it.hasNext() && count < nRows) {
      int row = it.nextInt();
      copy.append(table.row(row));
      count++;
    }
    return copy;
  }

  /** {@inheritDoc} */
  @Override
  public TableSlice setName(String name) {
    this.name = name;
    return this;
  }

  /** Returns the data in this slice as a new Table */
  public Table asTable() {
    Table table = Table.create(this.name());
    for (Column<?> column : this.columns()) {
      table.addColumns(column);
    }
    return table;
  }

  /**
   * IntIterator of source table row numbers that are present in this view. This can be used to in
   * combination with the source table to iterate over the cells of a column in a sorted order
   * without copying the column.
   *
   * @return an int iterator of row numbers in the source table that are present in this view.
   */
  protected PrimitiveIterator.OfInt sourceRowNumberIterator() {
    if (this.isSorted()) {
      return Arrays.stream(sortOrder).iterator();
    } else if (this.hasSelection()) {
      return selection.iterator();
    }
    return Selection.withRange(0, table.rowCount()).iterator();
  }

  /**
   * Returns the result of applying the given function to the specified column
   *
   * @param numberColumnName The name of a numeric column in this table
   * @param function A numeric reduce function
   * @return the function result
   * @throws IllegalArgumentException if numberColumnName doesn't name a numeric column in this
   *     table
   */
  public double reduce(String numberColumnName, NumericAggregateFunction function) {
    NumericColumn<?> column = table.numberColumn(numberColumnName);
    if (hasSelection()) {
      return function.summarize(column.where(selection));
    }
    return function.summarize(column);
  }

  /**
   * Iterate over the underlying rows in the source table. If you set one of the rows while
   * iterating it will change the row in the source table.
   */
  @Override
  public Iterator<Row> iterator() {

    return new Iterator<Row>() {

      private final Row row = new Row(TableSlice.this);

      @Override
      public Row next() {
        return row.next();
      }

      @Override
      public boolean hasNext() {
        return row.hasNext();
      }
    };
  }

  private boolean hasSelection() {
    return selection != null;
  }

  private boolean isSorted() {
    return sortOrder != null;
  }

  /**
   * Maps the view row number to the row number on the underlying source table.
   *
   * @param rowNumber the row number in the view.
   * @return the matching row number in the underlying table.
   */
  public int mappedRowNumber(int rowNumber) {
    if (isSorted()) {
      return sortOrder[rowNumber];
    } else if (hasSelection()) {
      return selection.get(rowNumber);
    }
    return rowNumber;
  }

  /**
   * Sort this view in place without modifying or copying the underlying source table. Unlike {@link
   * Table#sortOn(Sort)} which returns a copy of the table, this method sorts the view in place.
   *
   * @param key to sort on.
   */
  public void sortOn(Sort key) {
    Preconditions.checkArgument(!key.isEmpty());
    if (key.size() == 1) {
      IntComparator comparator = SortUtils.getComparator(table, key);
      this.sortOrder = sortOn(comparator);
    } else {
      IntComparatorChain chain = SortUtils.getChain(table, key);
      this.sortOrder = sortOn(chain);
    }
  }

  /** Returns an array of integers representing the source table indexes in sorted order. */
  private int[] sortOn(IntComparator rowComparator) {
    int[] newRows;
    if (hasSelection()) {
      newRows = this.selection.toArray();
    } else {
      newRows = IntStream.range(0, table.rowCount()).toArray();
    }
    IntArrays.parallelQuickSort(newRows, rowComparator);
    return newRows;
  }
}
