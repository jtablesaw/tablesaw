package tech.tablesaw.io.saw;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SawUtilsTest {

  @Test
  void makeNameRemovesAllPathSeparators() {
    assertEquals(".._.._x.saw", SawUtils.makeName("../../x"));
    assertEquals(".._.._x.saw", SawUtils.makeName("..\\..\\x"));
    assertEquals("mytable.saw", SawUtils.makeName("my table"));
  }
}
