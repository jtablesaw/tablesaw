package tech.tablesaw;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tech.tablesaw.api.Table;

public class TableAssertions {
  private TableAssertions() {}
  /** Make sure each row in each table match */
  public static void assertTableEquals(Table expected, Table actual) {
    assertEquals(actual.rowCount(), expected.rowCount(), "tables should have same number of rows");
    assertEquals(
        actual.columnCount(), expected.columnCount(), "tables should have same number of columns");
    int maxRows = actual.rowCount();
    int numberOfColumns = actual.columnCount();
    for (int rowIndex = 0; rowIndex < maxRows; rowIndex++) {
      for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
        assertEquals(
            actual.get(rowIndex, columnIndex),
            expected.get(rowIndex, columnIndex),
            "cells[" + rowIndex + ", " + columnIndex + "] do not match");
      }
    }
  }
}
