package tech.tablesaw;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import tech.tablesaw.api.Row;
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

  public static void assertTableContents(Object[][] expected, Table actual) {

    if (expected.length == 0) {
      //empty table
      assertEquals(0, actual.rowCount(), "Expected an empty table");
      assertEquals(0, actual.columnCount(), "Expected a table with no columns");
    } else {
      assertEquals(
          expected.length - 1, actual.rowCount(), "Table does not have the same number of rows");

      assertArrayEquals(
          expected[0],
          actual.columnNames().toArray(),
          String.format(
              "Column names are not equal.\nexpected: %s\nactual:   %s",
              Arrays.toString(expected[0]), Arrays.toString(actual.columnNames().toArray())));

      int expectedRowIndex = 1;
      for (Row actualRow : actual) {
        Object[] expectedRowData = expected[expectedRowIndex];
        assertEquals(
            expectedRowData.length,
            actualRow.columnCount(),
            "Row has the expected number of columns");

        for (int colIndex = 0; colIndex < expectedRowData.length; colIndex++) {
          assertEquals(
              expectedRowData[colIndex],
              actualRow.getObject(colIndex),
              String.format("Value at row %d column %d not equal", expectedRowIndex - 1, colIndex));
        }
        expectedRowIndex++;
      }
    }
  }
}
