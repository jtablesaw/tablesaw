package com.github.lwhite1.tablesaw;

import com.github.lwhite1.tablesaw.columns.BooleanColumn;
import com.github.lwhite1.tablesaw.columns.CategoryColumn;
import com.github.lwhite1.tablesaw.columns.Column;
import com.github.lwhite1.tablesaw.columns.FloatColumn;
import com.github.lwhite1.tablesaw.columns.IntColumn;
import com.github.lwhite1.tablesaw.columns.LocalDateColumn;
import com.github.lwhite1.tablesaw.columns.LocalDateTimeColumn;
import com.github.lwhite1.tablesaw.columns.LocalTimeColumn;
import com.github.lwhite1.tablesaw.columns.LongColumn;
import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.columns.ShortColumn;
import com.github.lwhite1.tablesaw.util.ReversingIntComparator;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import org.roaringbitmap.RoaringBitmap;

import javax.annotation.concurrent.Immutable;

/**
 * A static utility class for row operations
 */
@Immutable
public class Rows {

  // Don't instantiate
  private Rows() {
  }

  static void copyRowsToTable(IntArrayList rows, Table oldTable, Table newTable) {

    for (int columnIndex = 0; columnIndex < oldTable.columnCount(); columnIndex++) {
      ColumnType columnType = oldTable.column(columnIndex).type();
      switch (columnType) {
        case FLOAT:
          copy(rows, (FloatColumn) oldTable.column(columnIndex), (FloatColumn) newTable.column(columnIndex));
          break;
        case INTEGER:
          copy(rows, (IntColumn) oldTable.column(columnIndex), (IntColumn) newTable.column(columnIndex));
          break;
        case SHORT_INT:
          copy(rows, (ShortColumn) oldTable.column(columnIndex), (ShortColumn) newTable.column(columnIndex));
          break;
        case LONG_INT:
          copy(rows, (LongColumn) oldTable.column(columnIndex), (LongColumn) newTable.column(columnIndex));
          break;
        case CATEGORY:
          copy(rows, (CategoryColumn) oldTable.column(columnIndex), (CategoryColumn) newTable.column(columnIndex));
          break;
        case BOOLEAN:
          copy(rows, (BooleanColumn) oldTable.column(columnIndex), (BooleanColumn) newTable.column(columnIndex));
          break;
        case LOCAL_DATE:
          copy(rows, (LocalDateColumn) oldTable.column(columnIndex), (LocalDateColumn) newTable.column(columnIndex));
          break;
        case LOCAL_DATE_TIME:
          copy(rows, (LocalDateTimeColumn) oldTable.column(columnIndex), (LocalDateTimeColumn) newTable.column
              (columnIndex));
          break;
        case LOCAL_TIME:
          copy(rows, (LocalTimeColumn) oldTable.column(columnIndex), (LocalTimeColumn) newTable.column(columnIndex));
          break;
        default:
          throw new RuntimeException("Unhandled column type in case statement");
      }
    }
  }

  public static void copyRowsToTable(RoaringBitmap rows, Table oldTable, Table newTable) {
    int[] r = rows.toArray();
    IntArrayList rowArray = new IntArrayList(r);
    copyRowsToTable(rowArray, oldTable, newTable);
  }

  public static void head(int rowCount, Table oldTable, Table newTable) {
    IntArrayList rows = new IntArrayList(rowCount);
    for (int i = 0; i < rowCount; i++) {
      rows.add(i);
    }
    copyRowsToTable(rows, oldTable, newTable);
  }

  private static void copy(IntArrayList rows, FloatColumn oldColumn, FloatColumn newColumn) {
    for (int index : rows) {
      newColumn.add(oldColumn.get(index));
    }
  }

  private static void copy(IntArrayList rows, CategoryColumn oldColumn, CategoryColumn newColumn) {
    for (int index : rows) {
      newColumn.add(oldColumn.get(index));
    }
  }

  private static void copy(IntArrayList rows, BooleanColumn oldColumn, BooleanColumn newColumn) {
    for (int index : rows) {
      newColumn.add(oldColumn.get(index));
    }
  }

  private static void copy(IntArrayList rows, IntColumn oldColumn, IntColumn newColumn) {
    for (int index : rows) {
      newColumn.add(oldColumn.get(index));
    }
  }

  private static void copy(IntArrayList rows, ShortColumn oldColumn, ShortColumn newColumn) {
    for (int index : rows) {
      newColumn.add(oldColumn.get(index));
    }
  }

  private static void copy(IntArrayList rows, LongColumn oldColumn, LongColumn newColumn) {
    for (int index : rows) {
      newColumn.add(oldColumn.get(index));
    }
  }

  private static void copy(IntArrayList rows, LocalDateTimeColumn oldColumn, LocalDateTimeColumn newColumn) {
    for (int index : rows) {
      newColumn.add(oldColumn.getLong(index));
    }
  }

  private static void copy(IntArrayList rows, LocalDateColumn oldColumn, LocalDateColumn newColumn) {
    for (int index : rows) {
      newColumn.add(oldColumn.getInt(index));
    }
  }

  private static void copy(IntArrayList rows, LocalTimeColumn oldColumn, LocalTimeColumn newColumn) {
    for (int index : rows) {
      newColumn.add(oldColumn.getInt(index));
    }
  }

  /**
   * Returns a comparator for the column matching the specified name
   */
  private IntComparator rowComparator(Column column, boolean reverse) {

    IntComparator rowComparator = column.rowComparator();

    if (reverse) {
      return ReversingIntComparator.reverse(rowComparator);
    } else {
      return rowComparator;
    }
  }
}
