package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import tech.tablesaw.selection.Selection;

class ShortColumnTest {

  private final short[] shortColumnValues = {4, 5, 9, 33, 121, 77};
  private final ShortColumn shortColumn = ShortColumn.create("sc", shortColumnValues);

  @Test
  void isIn() {
    Selection result = shortColumn.isIn(4, 40);
    assertEquals(1, result.size());
    assertTrue(shortColumn.where(result).contains((short) 4));
  }

  @Test
  void isNotIn() {
    Selection result = shortColumn.isNotIn(4, 40);
    assertEquals(5, result.size());
    assertTrue(shortColumn.where(result).contains((short) 5));
  }
}
