package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.Column;

/** Contract tests shared by all nullable list-valued columns. */
abstract class AbstractListColumnTest<T, C extends Column<List<T>>> {

  protected abstract C create(String name);

  protected abstract T value(int value);

  protected abstract void appendObject(C column, Object value);

  protected abstract void assertInvalidObject(C column, Object value);

  protected abstract Object invalidElement();

  protected List<T> values(int first, int second) {
    return Arrays.asList(value(first), value(second));
  }

  @Test
  void storesValuesAndProtectsThemFromMutation() {
    C column = create("values");
    List<T> input = new ArrayList<>(values(1, 2));

    column.append(input);
    input.set(0, value(99));

    assertEquals(values(1, 2), column.get(0));
    assertNotSame(input, column.get(0));
    assertThrows(UnsupportedOperationException.class, () -> column.get(0).add(value(3)));
  }

  @Test
  void handlesEmptyListsAndMissingRows() {
    C column = create("values");
    column.append(Collections.emptyList()).appendMissing().append(values(1, 2));

    assertEquals(3, column.size());
    assertEquals(1, column.countMissing());
    assertFalse(column.isMissing(0));
    assertTrue(column.isMissing(1));
    assertArrayEquals(new int[] {1}, column.isMissing().toArray());
    assertArrayEquals(new int[] {0, 2}, column.isNotMissing().toArray());
    assertEquals(Collections.emptyList(), column.get(0));
    assertNull(column.get(1));
  }

  @Test
  void handlesRepeatedEmptyAndMissingRows() {
    C column = create("values");
    column.append(Collections.emptyList()).append(Collections.emptyList());
    column.appendMissing().appendMissing().append(values(1, 2));

    assertEquals(5, column.size());
    assertEquals(2, column.countMissing());
    assertArrayEquals(new int[] {2, 3}, column.isMissing().toArray());
    assertEquals(Collections.emptyList(), column.get(0));
    assertEquals(Collections.emptyList(), column.get(1));
    assertEquals(3, column.countUnique());
  }

  @Test
  void comparesEqualValuesAcrossSeparateColumns() {
    C left = create("left");
    C right = create("right");
    left.append(Collections.emptyList()).append(Collections.emptyList());
    right.append(Collections.emptyList()).append(Collections.emptyList());
    left.appendMissing().append(values(1, 2));
    right.appendMissing().append(values(1, 2));

    for (int row = 0; row < left.size(); row++) {
      assertTrue(left.type().compare(row, left, row, right));
    }

    right.set(1, values(9, 9));
    right.set(2, values(9, 9));
    assertFalse(left.type().compare(1, left, 1, right));
    assertFalse(left.type().compare(2, left, 2, right));
  }

  @Test
  void sortsRepeatedEmptyMissingAndValueRowsAsDistinctGroups() {
    C column = create("values");
    column.appendMissing().append(Collections.emptyList()).appendMissing();
    column.append(Collections.emptyList()).append(values(1, 2));

    column.sortAscending();
    assertEquals(Collections.emptyList(), column.get(0));
    assertEquals(Collections.emptyList(), column.get(1));
    assertEquals(values(1, 2), column.get(2));
    assertNull(column.get(3));
    assertNull(column.get(4));
  }

  @Test
  void supportsObjectAppendAndRejectsInvalidValues() {
    C column = create("values");
    appendObject(column, values(1, 2));
    appendObject(column, null);

    assertEquals(values(1, 2), column.get(0));
    assertNull(column.get(1));
    assertInvalidObject(column, "not a list");
    assertInvalidObject(column, Arrays.asList(value(1), invalidElement()));
  }

  @Test
  void parsesAndRendersRows() {
    C source = create("values");
    source.append(values(1, 2)).append(Collections.emptyList()).appendMissing();
    C parsed = create("values");
    parsed.appendCell(source.getString(0));
    parsed.appendCell(source.getString(1));
    parsed.appendCell("");

    assertEquals(source.get(0), parsed.get(0));
    assertEquals(Collections.emptyList(), parsed.get(1));
    assertEquals(Collections.emptyList(), parsed.get(2));
    assertEquals(source.getUnformattedString(0), source.getString(0));
  }

  @Test
  void supportsSelectionCopyLagAndRemovingMissingRows() {
    C column = create("values");
    column.append(values(1, 2)).appendMissing().append(values(3, 4));

    C selected = cast(column.where(column.isNotMissing()));
    C withoutMissing = cast(column.removeMissing());
    C copy = cast(column.copy());
    C lagged = cast(column.lag(1));

    assertEquals(2, selected.size());
    assertEquals(2, withoutMissing.size());
    assertEquals(column.asList(), copy.asList());
    assertNull(lagged.get(0));
    assertEquals(values(1, 2), lagged.get(1));
    assertNull(lagged.get(2));
  }

  @Test
  void supportsUniqueIndexesHashesAndObjectViews() {
    C column = create("values");
    column.append(values(1, 2)).append(values(1, 2)).appendMissing();

    assertEquals(0, column.indexOf(values(1, 2)));
    assertEquals(1, column.lastIndexOf(values(1, 2)));
    assertEquals(2, cast(column.unique()).size());
    Set<List<T>> expected = new LinkedHashSet<>();
    expected.add(values(1, 2));
    expected.add(null);
    assertEquals(expected, column.asSet());
    assertEquals(column.get(0).hashCode(), column.valueHash(0));
    assertTrue(column.equals(0, 1));
    assertArrayEquals(column.asList().toArray(), column.asObjectArray());
  }

  @Test
  void supportsStringAndByteViewsAndSummary() {
    C column = create("values");
    column.append(values(1, 2)).appendMissing();

    assertEquals(column.getString(0), column.asStringColumn().get(0));
    assertTrue(StringColumn.valueIsMissing(column.asStringColumn().get(1)));
    assertEquals(2, column.summary().rowCount());
    assertArrayEquals(column.getString(0).getBytes(), column.asBytes(0));
  }

  @Test
  void sortsPopulatedEmptyAndMissingRows() {
    C column = create("values");
    column
        .append(values(3, 4))
        .appendMissing()
        .append(Collections.emptyList())
        .append(values(1, 2));

    column.sortAscending();
    assertEquals(Collections.emptyList(), column.get(0));
    assertEquals(values(1, 2), column.get(1));
    assertEquals(values(3, 4), column.get(2));
    assertNull(column.get(3));

    column.sortDescending();
    assertNull(column.get(0));
    assertEquals(values(3, 4), column.get(1));
    assertEquals(values(1, 2), column.get(2));
    assertEquals(Collections.emptyList(), column.get(3));
  }

  @SuppressWarnings("unchecked")
  private C cast(Column<List<T>> column) {
    return (C) column;
  }
}
