package com.github.lwhite1.tablesaw.table;

import com.github.lwhite1.tablesaw.aggregator.NumericReduceFunction;
import com.github.lwhite1.tablesaw.api.Table;
import com.github.lwhite1.tablesaw.columns.CategoryColumn;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.columns.FloatColumn;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import org.roaringbitmap.RoaringBitmap;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A group of tables formed by performing splitting operations on an original table
 */
public class ViewGroup implements Iterable<TemporaryView> {

  private static final String SPLIT_STRING = "~~~";
  private static final Splitter SPLITTER = Splitter.on(SPLIT_STRING);


  private final Table sortedOriginal;

  private List<TemporaryView> subTables = new ArrayList<>();

  // the name(s) of the column(s) we're splitting the table on
  private String[] splitColumnNames;

  public ViewGroup(Table original, Column... columns) {
    splitColumnNames = new String[columns.length];
    for (int i = 0; i < columns.length; i++) {
      splitColumnNames[i] = columns[i].name();
    }
    this.sortedOriginal = original.sortOn(splitColumnNames);
    splitOn(splitColumnNames);
  }

  public static ViewGroup create(Table original, String... columnsNames) {
    List<Column> columns = original.columns(columnsNames);
    return new ViewGroup(original, columns.toArray(new Column[columns.size()]));
  }

  /**
   * Splits the sortedOriginal table into sub-tables, grouping on the columns whose names are given in splitColumnNames
   */
  private void splitOn(String... columnNames) {

    List<Column> columns = sortedOriginal.columns(columnNames);
    int byteSize = 0;
    {
      for (Column c : columns) {
        byteSize += c.byteSize();
      }
    }

    byte[] currentKey = null;
    String currentStringKey = null;
    TemporaryView view;

    RoaringBitmap bitmap = new RoaringBitmap();

    for (int row = 0; row < sortedOriginal.rowCount(); row++) {

      ByteBuffer byteBuffer = ByteBuffer.allocate(byteSize);
      String newStringKey = "";

      for (int col = 0; col < columnNames.length; col++) {
        if (col > 0)
          newStringKey = newStringKey + SPLIT_STRING;

        Column c = sortedOriginal.column(columnNames[col]);
        String groupKey = sortedOriginal.get(sortedOriginal.columnIndex(c), row);
        newStringKey = newStringKey + groupKey;
        byteBuffer.put(c.asBytes(row));
      }
      byte[] newKey = byteBuffer.array();
      if (row == 0) {
        currentKey = newKey;
        currentStringKey = newStringKey;
      }
      if (!Arrays.equals(newKey, currentKey)) {
        currentKey = newKey;
        view = new TemporaryView(sortedOriginal, bitmap);
        view.setName(currentStringKey);
        currentStringKey = newStringKey;
        subTables.add(view);
        bitmap = new RoaringBitmap();
        bitmap.add(row);
      } else {
        bitmap.add(row);
      }
    }
    if (!bitmap.isEmpty()) {
      view = new TemporaryView(sortedOriginal, bitmap);
      view.setName(currentStringKey);
      subTables.add(view);
    }
  }

  public List<TemporaryView> getSubTables() {
    return subTables;
  }

  @VisibleForTesting
  public Table getSortedOriginal() {
    return sortedOriginal;
  }

  public int size() {
    return subTables.size();
  }

  /**
   private SubTable splitGroupingColumn(SubTable subTable, List<Column> columnNames) {

   List<Column> newColumns = new ArrayList<>();

   for (Column column : columnNames) {
   Column newColumn = column.emptyCopy();
   newColumns.add(newColumn);
   }
   // iterate through the rows in the table and split each of the grouping columns into multiple columns
   for (int row = 0; row < subTable.rowCount(); row++) {
   List<String> strings = SPLITTER.splitToList(subTable.name());
   for (int col = 0; col < newColumns.size(); col++) {
   newColumns.get(col).addCell(strings.get(col));
   }
   }
   for (Column c : newColumns) {
   subTable.addColumn(c);
   }
   return subTable;
   }
   */

  public Table reduce(String numericColumnName, NumericReduceFunction function) {
    Preconditions.checkArgument(!subTables.isEmpty());
    Table t = new Table(sortedOriginal.name() + " summary");
    CategoryColumn groupColumn = new CategoryColumn("Group", subTables.size());
    FloatColumn resultColumn = new FloatColumn(function.functionName(), subTables.size());
    t.addColumn(groupColumn);
    t.addColumn(resultColumn);

    for (TemporaryView subTable : subTables) {
      double result = subTable.reduce(numericColumnName, function);
      groupColumn.add(subTable.name().replace(SPLIT_STRING, " * "));
      resultColumn.add((float) result);
    }
    return t;
  }


  /**
 * Returns an iterator over elements of type {@code T}.
 *
 * @return an Iterator.
 */
  @Override
  public Iterator<TemporaryView> iterator() {
    return subTables.iterator();
  }
}
