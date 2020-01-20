package tech.tablesaw;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

public class TableAssertions {
  private TableAssertions() {}
  /**
   * Make sure each row in each table match
   *
   * @param expected the table that was sorted using some external means e.g. excel. i.e known good
   *     data
   * @param actual the table that was sorted with Tablesaw
   */
  public static void assertTablesEquals(Table expected, Table actual) {
    assertEquals(
        expected.rowCount(), actual.rowCount(), "both tables have the same number of rows");
    int maxRows = actual.rowCount();
    int numberOfColumns = actual.columnCount();
    for (int rowIndex = 0; rowIndex < maxRows; rowIndex++) {
      for (int columnIndex = 0; columnIndex < numberOfColumns; columnIndex++) {
        assertEquals(
            expected.get(rowIndex, columnIndex),
            actual.get(rowIndex, columnIndex),
            "cells[" + rowIndex + ", " + columnIndex + "] do not match");
      }
    }
  }

  /**
   * Compares a table with expected column data. Avoids the requirement to construct the table of
   * expected results
   *
   * @param expectedName Expected table name
   * @param expectedColumns Expected columns including their label and values
   * @param actual Actual table
   */
  public static void assertTableEquals(
      String expectedName, Object[][] expectedColumns, Table actual) {
    assertEquals(expectedName, actual.name(), "Names should match");

    if (expectedColumns.length == 0) {
      // empty table
      assertEquals(0, actual.rowCount(), "Expected an empty table");
      assertEquals(0, actual.columnCount(), "Expected a table with no columns");
    } else {
      assertEquals(expectedColumns.length, actual.columnCount(), "Has same number of columns");

      for (int i = 0; i < expectedColumns.length; i++) {
        Column<?> actualColumn = actual.column(i);
        Object[] expectedColumn = expectedColumns[i];
        assertEquals(expectedColumn[0], actualColumn.name(), "Column names match");
        for (int j = 0; j < expectedColumn.length - 1; j++) {
          assertEquals(
              expectedColumn[j + 1],
              actualColumn.get(j),
              "cells[" + j + ", " + i + "] do not match");
        }
      }
    }
  }
}
