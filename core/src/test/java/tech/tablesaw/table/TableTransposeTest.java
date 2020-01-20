package tech.tablesaw.table;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static tech.tablesaw.TableAssertions.assertTableEquals;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.FloatColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.LongColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
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
            StringColumn.create("label", new String[] {"row1", "row2", "row3"}),
            DoubleColumn.create("value1", new double[] {1.0, 1.1, 1.2}),
            DoubleColumn.create("value2", new double[] {2.0, 2.1, 2.2}));
    Table result = testTable.transpose();

    assertTableEquals(
        TABLE_NAME,
        new Object[][] {
          {"row1", 1.0, 2.0},
          {"row2", 1.1, 2.1},
          {"row3", 1.2, 2.2},
        },
        result);
    assertTransposeIsReversible(testTable);
  }

  @Test
  void transposeWithMissingData() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("label", new String[] {"row1", "row2", "row3"}),
            DoubleColumn.create(
                "value1", new double[] {1.0, DoubleColumnType.missingValueIndicator(), 1.2}),
            DoubleColumn.create("value2", new double[] {2.0, 2.1, 2.2}));
    Table result = testTable.transpose();

    assertTableEquals(
        TABLE_NAME,
        new Object[][] {
          {"row1", 1.0, 2.0},
          {"row2", DoubleColumnType.missingValueIndicator(), 2.1},
          {"row3", 1.2, 2.2},
        },
        result);

    assertTransposeIsReversible(testTable);
  }

  @Test
  void transposeFloats() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("label", new String[] {"row1", "row2", "row3"}),
            FloatColumn.create("value1", new float[] {1.0f, 1.1f, 1.2f}),
            FloatColumn.create("value2", new float[] {2.0f, 2.1f, 2.2f}));
    assertTransposeIsReversible(testTable);
  }

  @Test
  void transposeIntegers() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("label", new String[] {"row1", "row2", "row3"}),
            IntColumn.create("value1", new int[] {1, 2, 3}),
            IntColumn.create("value2", new int[] {4, 5, 6}));
    Table result = testTable.transpose();

    assertTableEquals(
        TABLE_NAME,
        new Object[][] {
          {"row1", 1, 4},
          {"row2", 2, 5},
          {"row3", 3, 6}
        },
        result);

    assertTransposeIsReversible(testTable);
  }

  @Test
  void transposeLongs() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("label", new String[] {"row1", "row2", "row3"}),
            LongColumn.create("value1", new long[] {1, 2, 3}),
            LongColumn.create("value2", new long[] {4, 5, 6}));
    Table result = testTable.transpose();

    assertTableEquals(
        TABLE_NAME,
        new Object[][] {
          {"row1", 1L, 4L},
          {"row2", 2L, 5L},
          {"row3", 3L, 6L},
        },
        result);

    assertTransposeIsReversible(testTable);
  }

  @Test
  void transposeBooleans() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("label", new String[] {"row1", "row2", "row3"}),
            BooleanColumn.create("value1", new boolean[] {true, true, true}),
            BooleanColumn.create("value2", new boolean[] {false, false, false}));
    Table result = testTable.transpose();

    assertTableEquals(
        TABLE_NAME,
        new Object[][] {
          {"row1", true, false}, {"row2", true, false}, {"row3", true, false},
        },
        result);

    assertTransposeIsReversible(testTable);
  }

  @Test
  void transposeStrings() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("label", new String[] {"row1", "row2", "row3"}),
            StringColumn.create("fruit", new String[] {"apple", "banana", "pear"}),
            StringColumn.create("colour", new String[] {"red", "yellow", "green"}));
    Table result = testTable.transpose();

    assertTableEquals(
        TABLE_NAME,
        new Object[][] {
          {"row1", "apple", "red"},
          {"row2", "banana", "yellow"},
          {"row3", "pear", "green"},
        },
        result);

    assertTransposeIsReversible(testTable);
  }

  @Test
  void transposeMixedTypesThrowsException() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("label", new String[] {"row1", "row2", "row3"}),
            DoubleColumn.create("value1", new double[] {1.0, 1.1, 1.2}),
            StringColumn.create("colour", new String[] {"red", "yellow", "green"}));

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
  void transposeDoublesIncludeColumnHeadings() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("label", new String[] {"row1", "row2", "row3"}),
            DoubleColumn.create("value1", new double[] {1.0, 1.1, 1.2}),
            DoubleColumn.create("value2", new double[] {2.0, 2.1, 2.2}));
    Table result = testTable.transpose(true);

    assertTableEquals(
        TABLE_NAME,
        new Object[][] {
          {"label", "value1", "value2"},
          {"row1", 1.0, 2.0},
          {"row2", 1.1, 2.1},
          {"row3", 1.2, 2.2}
        },
        result);

    assertEquals(testTable.print(), result.transpose(true).print(), "Transpose is reversible");
  }

  private void assertTransposeIsReversible(Table testTable) {
    assertEquals(
        testTable.print(),
        testTable.transpose(true).transpose(true).print(),
        "Transpose is reversible");
  }
}
