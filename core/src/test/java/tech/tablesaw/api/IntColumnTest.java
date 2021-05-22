package tech.tablesaw.api;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import tech.tablesaw.columns.numbers.IntParser;
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

  @Test
  public void testCustomParser() {
    // Just do enough to ensure the parser is wired up correctly
    IntParser customParser = new IntParser(ColumnType.LONG);
    customParser.setMissingValueStrings(Arrays.asList("not here"));
    intColumn.setParser(customParser);

    intColumn.appendCell("not here");
    assertTrue(intColumn.isMissing(intColumn.size() - 1));
    intColumn.appendCell("5");
    assertFalse(intColumn.isMissing(intColumn.size() - 1));
  }
}
