package tech.tablesaw.plotly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class UtilsTest {

  @Test
  public void testEscapeQuote() {
    String s = Utils.dataAsString(new String[] {"Bobby\"s tables"});
    assertTrue(s.contains("\\"), s);
    String s2 = Utils.dataAsString(new String[] {"Hello\\"});
    assertEquals("[\"Hello\\\\\"]", s2);
  }
}
