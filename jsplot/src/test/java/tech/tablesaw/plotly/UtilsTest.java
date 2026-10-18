package tech.tablesaw.plotly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

  @Test
  public void testEscapeHtml() {
    String s = Utils.dataAsString(new String[] {"</script><script>alert(1)</script>"});
    assertFalse(s.contains("<"), s);
    assertFalse(s.contains(">"), s);
    assertEquals("[\"\\u003c/script\\u003e\\u003cscript\\u003ealert(1)\\u003c/script\\u003e\"]", s);
    assertEquals("[\"a \\u0026 b\"]", Utils.dataAsString(new String[] {"a & b"}));
  }
}
