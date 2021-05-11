package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.numbers.LongParser;
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

  @Test
  public void testCustomParser() {
    // Just do enough to ensure the parser is wired up correctly
    LongParser customParser = new LongParser(ColumnType.LONG);
    customParser.setMissingValueStrings(Arrays.asList("not here"));
    longColumn.setParser(customParser);

    longColumn.appendCell("not here");
    assertTrue(longColumn.isMissing(longColumn.size() - 1));
    longColumn.appendCell("5");
    assertFalse(longColumn.isMissing(longColumn.size() - 1));
  }
}
