package tech.tablesaw.plotly.components;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ConfigTest {

  @Test
  public void testJavascript() {
    {
      Config config = Config.builder().displayModeBar(true).build();
      assertTrue(config.asJavascript().contains("displayModeBar: true"));
    }
    {
      Config config = Config.builder().displayModeBar(false).build();
      assertTrue(config.asJavascript().contains("displayModeBar: false"));
    }
    {
      Config config = Config.builder().build();
      assertFalse(config.asJavascript().contains("displayModeBar"));
    }
  }
}
