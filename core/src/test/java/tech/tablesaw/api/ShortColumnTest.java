package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.numbers.ShortParser;
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

  @Test
  public void testCustomParser() {
    // Just do enough to ensure the parser is wired up correctly
    ShortParser customParser = new ShortParser(ColumnType.SHORT);
    customParser.setMissingValueStrings(Arrays.asList("not here"));
    shortColumn.setParser(customParser);

    shortColumn.appendCell("not here");
    assertTrue(shortColumn.isMissing(shortColumn.size() - 1));
    shortColumn.appendCell("5");
    assertFalse(shortColumn.isMissing(shortColumn.size() - 1));
  }
}
