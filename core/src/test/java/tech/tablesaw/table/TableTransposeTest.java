package tech.tablesaw.table;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.columns.numbers.DoubleColumnType;

public class TableTransposeTest {
  private static final String TABLE_NAME = "Data";

  @Test
  void transposeEmptyTable() {
    Table empty = Table.create(TABLE_NAME);
    Table result = empty.transpose();
    assertEquals(empty.print(), result.transpose().print());
    assertTableEquals(TABLE_NAME, new Object[][] {}, result);
  }

  @Test
  void transposeDoubles() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            DoubleColumn.create("value1", new double[] {1.0, 1.1, 1.2}),
            DoubleColumn.create("value2", new double[] {2.0, 2.1, 2.2}));
    Table result = testTable.transpose();

    assertTableEquals(
        TABLE_NAME,
        new Object[][] {
          {"0", 1.0, 2.0},
          {"1", 1.1, 2.1},
          {"2", 1.2, 2.2},
        },
        result);
  }

  @Test
  void transposeWithMissingData() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            DoubleColumn.create(
                "value1", new double[] {1.0, DoubleColumnType.missingValueIndicator(), 1.2}),
            DoubleColumn.create("value2", new double[] {2.0, 2.1, 2.2}));
    Table result = testTable.transpose();

    assertTableEquals(
        TABLE_NAME,
        new Object[][] {
          {"0", 1.0, 2.0},
          {"1", DoubleColumnType.missingValueIndicator(), 2.1},
          {"2", 1.2, 2.2},
        },
        result);
  }

  @Test
  void transposeFloats() {
    float float_1 = 1.0f;
    float float_2 = 2.0f;
    Table testTable =
        Table.create(
            TABLE_NAME,
            FloatColumn.create("value1", new float[] {float_1}),
            FloatColumn.create("value2", new float[] {float_2}));
    Table result = testTable.transpose();

    assertTableEquals(
        TABLE_NAME,
        new Object[][] {
          {"0", float_1, float_2},
        },
        result);
  }

  @Test
  void transposeIntegers() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            IntColumn.create("value1", new int[] {1, 2, 3}),
            IntColumn.create("value2", new int[] {4, 5, 6}));
    Table result = testTable.transpose();

    assertTableEquals(
        TABLE_NAME,
        new Object[][] {
          {"0", 1, 4},
          {"1", 2, 5},
          {"2", 3, 6}
        },
        result);
  }

  @Test
  void transposeLongs() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            LongColumn.create("value1", new long[] {1, 2, 3}),
            LongColumn.create("value2", new long[] {4, 5, 6}));
    Table result = testTable.transpose();

    assertTableEquals(
        TABLE_NAME,
        new Object[][] {
          {"0", 1L, 4L},
          {"1", 2L, 5L},
          {"2", 3L, 6L},
        },
        result);
  }

  @Test
  void transposeBooleans() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            BooleanColumn.create("value1", new boolean[] {true, true, true}),
            BooleanColumn.create("value2", new boolean[] {false, false, false}));
    Table result = testTable.transpose();

    assertTableEquals(
        TABLE_NAME,
        new Object[][] {
          {"0", true, false}, {"1", true, false}, {"2", true, false},
        },
        result);
  }

  @Test
  void transposeStrings() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("fruit", new String[] {"apple", "banana", "pear"}),
            StringColumn.create("colour", new String[] {"red", "yellow", "green"}));
    Table result = testTable.transpose();

    assertTableEquals(
        TABLE_NAME,
        new Object[][] {
          {"0", "apple", "red"},
          {"1", "banana", "yellow"},
          {"2", "pear", "green"},
        },
        result);
  }

  @Test
  void transposeMixedTypesThrowsException() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("colour", new String[] {"red", "yellow", "green"}),
            DoubleColumn.create("value1", new double[] {1.0, 1.1, 1.2}));

    try {
      testTable.transpose();
      fail("Should throw an exception");
    } catch (IllegalArgumentException ex) {
      assertEquals(
          "Transpose currently only supports tables where value columns are of the same type",
          ex.getMessage());
    }
  }

  @Test
  void transposeMixedTypesThrowsExceptionFirstColumnAsHeadings() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("label", new String[] {"row1", "row2", "row3"}),
            DoubleColumn.create("value1", new double[] {1.0, 1.1, 1.2}),
            StringColumn.create("colour", new String[] {"red", "yellow", "green"}));

    try {
      testTable.transpose(false, true);
      fail("Should throw an exception");
    } catch (IllegalArgumentException ex) {
      assertEquals(
          "Transpose currently only supports tables where value columns are of the same type",
          ex.getMessage());
    }
  }

  @Test
  void transposeIncludeColumnHeadingsAsFirstColumn() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            DoubleColumn.create("value1", new double[] {1.0, 1.1, 1.2}),
            DoubleColumn.create("value2", new double[] {2.0, 2.1, 2.2}));
    Table result = testTable.transpose(true, false);

    assertTableEquals(
        TABLE_NAME,
        new Object[][] {
          {"0", "value1", "value2"},
          {"1", 1.0, 2.0},
          {"2", 1.1, 2.1},
          {"3", 1.2, 2.2}
        },
        result);
  }

  @Test
  void transposeUseFirstColumnForHeadings() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("label", new String[] {"row1", "row2", "row3"}),
            DoubleColumn.create("value1", new double[] {1.0, 1.1, 1.2}),
            DoubleColumn.create("value2", new double[] {2.0, 2.1, 2.2}));
    Table result = testTable.transpose(false, true);

    assertTableEquals(
        TABLE_NAME,
        new Object[][] {
          {"row1", 1.0, 2.0},
          {"row2", 1.1, 2.1},
          {"row3", 1.2, 2.2}
        },
        result);
  }

  @Test
  void transposeCanBeFullyReversible() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("label", new String[] {"row1", "row2", "row3"}),
            DoubleColumn.create("value1", new double[] {1.0, 1.1, 1.2}),
            DoubleColumn.create("value2", new double[] {2.0, 2.1, 2.2}));
    Table result = testTable.transpose(true, true);

    assertTableEquals(
        TABLE_NAME,
        new Object[][] {
          {"label", "value1", "value2"},
          {"row1", 1.0, 2.0},
          {"row2", 1.1, 2.1},
          {"row3", 1.2, 2.2}
        },
        result);

    assertEquals(
        testTable.print(), result.transpose(true, true).print(), "Transpose is reversible");
  }

  /**
   * Compares a table with expected column data. Avoids the requirement to construct the table of
   * expected results
   *
   * @param expectedName Expected table name
   * @param expectedColumns Expected columns including their label and values
   * @param actual Actual table
   */
  private static void assertTableEquals(
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
