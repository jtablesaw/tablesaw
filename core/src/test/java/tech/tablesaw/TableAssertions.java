package tech.tablesaw;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tech.tablesaw.api.Table;

public class TableAssertions {
  private TableAssertions() {}
  /**
   * Make sure each row in each table match
   *
   * @param compareWith the table that was sorted using some external means e.g. excel. i.e known
   *     good data
   * @param sortedTable the table that was sorted with Tablesaw
   */
  public static void assertTablesEquals(Table compareWith, Table sortedTable) {
    assertEquals(
        sortedTable.rowCount(), compareWith.rowCount(), "both tables have the same number of rows");
    int maxRows = sortedTable.rowCount();
    int numberOfColumns = sortedTable.columnCount();
    for (int rowIndex = 0; rowIndex < maxRows; rowIndex++) {
      for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
        assertEquals(
            sortedTable.get(rowIndex, columnIndex),
            compareWith.get(rowIndex, columnIndex),
            "cells[" + rowIndex + ", " + columnIndex + "] do not match");
      }
    }
  }
}
