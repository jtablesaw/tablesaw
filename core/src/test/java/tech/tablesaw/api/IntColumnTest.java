package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import tech.tablesaw.selection.Selection;

class IntColumnTest {

  private final int[] intColumnValues = {4, 5, 9, 33, 121, 77};
  private final IntColumn intColumn = IntColumn.create("sc", intColumnValues);

  @Test
  void isIn() {
    Selection result = intColumn.isIn(4, 40);
    assertEquals(1, result.size());
    assertTrue(intColumn.where(result).contains(4));
  }

  @Test
  void isNotIn() {
    Selection result = intColumn.isNotIn(4, 40);
    assertEquals(5, result.size());
    assertTrue(intColumn.where(result).contains(5));
  }
}
