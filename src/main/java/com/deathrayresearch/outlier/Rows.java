package com.deathrayresearch.outlier;

import com.deathrayresearch.outlier.columns.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import javax.annotation.concurrent.Immutable;

/**
 * A static utility class for row operations
 */
@Immutable
public class Rows {

  // Don't instantiate
  private Rows() {}

  public static void copyRowsToTable(IntArrayList rows, Table oldTable, Table newTable) {

    for (int columnIndex = 0; columnIndex < oldTable.columnCount(); columnIndex++) {
      ColumnType columnType = oldTable.column(columnIndex).type();
      switch (columnType) {
        case FLOAT:
          copy(rows, (FloatColumn) oldTable.column(columnIndex), (FloatColumn) newTable.column(columnIndex));
          break;
        case INTEGER:
          copy(rows, (IntColumn) oldTable.column(columnIndex), (IntColumn) newTable.column(columnIndex));
          break;
        case CAT:
          copy(rows, (CategoryColumn) oldTable.column(columnIndex), (CategoryColumn) newTable.column(columnIndex));
          break;
        case BOOLEAN:
          copy(rows, (BooleanColumn) oldTable.column(columnIndex), (BooleanColumn) newTable.column(columnIndex));
          break;
        case TEXT:
          copy(rows, (TextColumn) oldTable.column(columnIndex), (TextColumn) newTable.column(columnIndex));
          break;
        case LOCAL_DATE:
          copy(rows, (LocalDateColumn) oldTable.column(columnIndex), (LocalDateColumn) newTable.column(columnIndex));
          break;
        case LOCAL_DATE_TIME:
          copy(rows, (LocalDateTimeColumn) oldTable.column(columnIndex), (LocalDateTimeColumn) newTable.column(columnIndex));
          break;
        case LOCAL_TIME:
          copy(rows, (LocalTimeColumn) oldTable.column(columnIndex), (LocalTimeColumn) newTable.column(columnIndex));
          break;
        case PERIOD:
          copy(rows, (PeriodColumn) oldTable.column(columnIndex), (PeriodColumn) newTable.column(columnIndex));
          break;
      }
    }
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

  private static void copy(IntArrayList rows, TextColumn oldColumn, TextColumn newColumn) {
    for (int index : rows) {
      newColumn.add(oldColumn.get(index));
    }
  }

  private static void copy(IntArrayList rows, IntColumn oldColumn, IntColumn newColumn) {
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

  private static void copy(IntArrayList rows, PeriodColumn oldColumn, PeriodColumn newColumn) {
    for (int index : rows) {
      newColumn.add(oldColumn.getInt(index));
    }
  }
}
