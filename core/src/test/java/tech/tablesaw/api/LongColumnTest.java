package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import tech.tablesaw.selection.Selection;

class LongColumnTest {

  private final long[] longColumnValues = {4, 5, 9, 33, 121, 77};
  private final LongColumn longColumn = LongColumn.create("sc", longColumnValues);

  @Test
  void isIn() {
    Selection result = longColumn.isIn(4, 40);
    assertEquals(1, result.size());
    assertTrue(longColumn.where(result).contains(4L));
  }

  @Test
  void isNotIn() {
    Selection result = longColumn.isNotIn(4, 40);
    assertEquals(5, result.size());
    assertTrue(longColumn.where(result).contains(5L));
  }
}
