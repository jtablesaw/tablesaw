package tech.tablesaw.columns.booleans;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import tech.tablesaw.api.BooleanColumn;

class BooleanMapUtilsTest {
  private BooleanColumn singleFalse = BooleanColumn.create("", false);
  private BooleanColumn singleTrue = BooleanColumn.create("", true);

  @Test
  void testAnd() {
    BooleanColumn actual = singleTrue.and(singleFalse);
    assertEquals(singleFalse.get(0), actual.get(0));
  }

  @Test
  void testOr() {
    BooleanColumn actual = singleFalse.or(singleTrue);
    assertEquals(singleTrue.get(0), actual.get(0));
  }

  @Test
  void testAndNot() {
    BooleanColumn actual = singleTrue.andNot(singleFalse);
    assertEquals(singleTrue.get(0), actual.get(0));
  }

  @Test
  void testAndNot2() {
    BooleanColumn actual = singleFalse.andNot(singleTrue);
    assertEquals(singleFalse.get(0), actual.get(0));
  }
}
