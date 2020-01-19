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
    assertTableEquals(empty, result);
  }

  @Test
  void transposeDoubles() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            DoubleColumn.create("value1", 1.0, 1.1, 1.2),
            DoubleColumn.create("value2", 2.0, 2.1, 2.2));
    Table result = testTable.transpose();

    assertTableEquals(
        Table.create(
            TABLE_NAME,
            DoubleColumn.create("0", 1.0, 2.0),
            DoubleColumn.create("1", 1.1, 2.1),
            DoubleColumn.create("2", 1.2, 2.2)),
        result);
  }

  @Test
  void transposeWithMissingData() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            DoubleColumn.create("value1", 1.0, DoubleColumnType.missingValueIndicator(), 1.2),
            DoubleColumn.create("value2", 2.0, 2.1, 2.2));
    Table result = testTable.transpose();

    assertTableEquals(
        Table.create(
            TABLE_NAME,
            DoubleColumn.create("0", 1.0, 2.0),
            DoubleColumn.create("1", DoubleColumnType.missingValueIndicator(), 2.1),
            DoubleColumn.create("2", 1.2, 2.2)),
        result);
  }

  @Test
  void transposeFloats() {
    float float_1 = 1.0f;
    float float_2 = 2.0f;
    Table testTable =
        Table.create(
            TABLE_NAME,
            FloatColumn.create("value1", float_1),
            FloatColumn.create("value2", float_2));
    Table result = testTable.transpose();

    assertTableEquals(Table.create(TABLE_NAME, FloatColumn.create("0", float_1, float_2)), result);
  }

  @Test
  void transposeIntegers() {
    Table testTable =
        Table.create(
            TABLE_NAME, IntColumn.create("value1", 1, 2, 3), IntColumn.create("value2", 4, 5, 6));
    Table result = testTable.transpose();

    assertTableEquals(
        Table.create(
            TABLE_NAME,
            IntColumn.create("0", 1, 4),
            IntColumn.create("1", 2, 5),
            IntColumn.create("2", 3, 6)),
        result);
  }

  @Test
  void transposeLongs() {
    Table testTable =
        Table.create(
            TABLE_NAME, LongColumn.create("value1", 1, 2, 3), LongColumn.create("value2", 4, 5, 6));
    Table result = testTable.transpose();

    assertTableEquals(
        Table.create(
            TABLE_NAME,
            LongColumn.create("0", 1L, 4L),
            LongColumn.create("1", 2L, 5L),
            LongColumn.create("2", 3L, 6L)),
        result);
  }

  @Test
  void transposeBooleans() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            BooleanColumn.create("value1", true, true, true),
            BooleanColumn.create("value2", false, false, false));
    Table result = testTable.transpose();

    assertTableEquals(
        Table.create(
            TABLE_NAME,
            BooleanColumn.create("0", true, false),
            BooleanColumn.create("1", true, false),
            BooleanColumn.create("2", true, false)),
        result);
  }

  @Test
  void transposeStrings() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("fruit", "apple", "banana", "pear"),
            StringColumn.create("colour", "red", "yellow", "green"));
    Table result = testTable.transpose();

    assertTableEquals(
        Table.create(
            TABLE_NAME,
            StringColumn.create("0", "apple", "red"),
            StringColumn.create("1", "banana", "yellow"),
            StringColumn.create("2", "pear", "green")),
        result);
  }

  @Test
  void transposeMixedTypesThrowsException() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("colour", "red", "yellow", "green"),
            DoubleColumn.create("value1", 1.0, 1.1, 1.2));

    try {
      testTable.transpose();
      fail("Should throw an exception");
    } catch (IllegalArgumentException ex) {
      assertEquals(
          "This operation currently only supports tables where value columns are of the same type",
          ex.getMessage());
    }
  }

  @Test
  void transposeMixedTypesThrowsExceptionFirstColumnAsHeadings() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("label", "row1", "row2", "row3"),
            DoubleColumn.create("value1", 1.0, 1.1, 1.2),
            StringColumn.create("colour", "red", "yellow", "green"));

    try {
      testTable.transpose(false, true);
      fail("Should throw an exception");
    } catch (IllegalArgumentException ex) {
      assertEquals(
          "This operation currently only supports tables where value columns are of the same type",
          ex.getMessage());
    }
  }

  @Test
  void transposeIncludeColumnHeadingsAsFirstColumn() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            DoubleColumn.create("value1", 1.0, 1.1, 1.2),
            DoubleColumn.create("value2", 2.0, 2.1, 2.2));
    Table result = testTable.transpose(true, false);

    assertTableEquals(
        Table.create(
            TABLE_NAME,
            StringColumn.create("0", "value1", "value2"),
            DoubleColumn.create("1", 1.0, 2.0),
            DoubleColumn.create("2", 1.1, 2.1),
            DoubleColumn.create("3", 1.2, 2.2)),
        result);
  }

  @Test
  void transposeUseFirstColumnForHeadings() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("label", "row1", "row2", "row3"),
            DoubleColumn.create("value1", 1.0, 1.1, 1.2),
            DoubleColumn.create("value2", 2.0, 2.1, 2.2));
    Table result = testTable.transpose(false, true);

    assertTableEquals(
        Table.create(
            TABLE_NAME,
            DoubleColumn.create("row1", 1.0, 2.0),
            DoubleColumn.create("row2", 1.1, 2.1),
            DoubleColumn.create("row3", 1.2, 2.2)),
        result);
  }

  @Test
  void transposeCanBeFullyReversible() {
    Table testTable =
        Table.create(
            TABLE_NAME,
            StringColumn.create("label", "row1", "row2", "row3"),
            DoubleColumn.create("value1", 1.0, 1.1, 1.2),
            DoubleColumn.create("value2", 2.0, 2.1, 2.2));
    Table result = testTable.transpose(true, true);

    assertTableEquals(
        Table.create(
            TABLE_NAME,
            StringColumn.create("label", "value1", "value2"),
            DoubleColumn.create("row1", 1.0, 2.0),
            DoubleColumn.create("row2", 1.1, 2.1),
            DoubleColumn.create("row3", 1.2, 2.2)),
        result);

    assertEquals(
        testTable.print(), result.transpose(true, true).print(), "Transpose is reversible");
  }
}
