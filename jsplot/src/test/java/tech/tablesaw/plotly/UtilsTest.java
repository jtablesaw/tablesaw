package tech.tablesaw.plotly;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilsTest {

  @Test
  public void testEscapeQuote() {
    String s = Utils.dataAsString(new String[] {"Bobby's tables"});
    Assertions.assertTrue(s.contains("\\"));
  }
}
