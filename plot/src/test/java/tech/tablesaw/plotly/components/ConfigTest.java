package tech.tablesaw.plotly.components;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ConfigTest {

  @Test
  public void testJavascript() {
    {
      Config config = Config.builder().build();
      assertTrue(config.asJavascript().startsWith("var config"));
    }
    {
      Config config = Config.builder().displayModeBar(true).build();
      assertTrue(config.asJavascript().contains("\"displayModeBar\" : true"));
    }
    {
      Config config = Config.builder().displayModeBar(false).build();
      assertTrue(config.asJavascript().contains("\"displayModeBar\" : false"));
    }
    {
      Config config = Config.builder().build();
      assertFalse(config.asJavascript().contains("displayModeBar"));
    }
    {
      Config config = Config.builder().responsive(true).build();
      assertTrue(config.asJavascript().contains("\"responsive\" : true"));
    }
    {
      Config config = Config.builder().responsive(false).build();
      assertTrue(config.asJavascript().contains("\"responsive\" : false"));
    }
    {
      Config config = Config.builder().build();
      assertFalse(config.asJavascript().contains("responsive"));
    }
    {
      Config config = Config.builder().displayLogo(true).build();
      assertTrue(config.asJavascript().contains("\"displaylogo\" : true"));
    }
    {
      Config config = Config.builder().displayLogo(false).build();
      assertTrue(config.asJavascript().contains("\"displaylogo\" : false"));
    }
    {
      Config config = Config.builder().build();
      assertFalse(config.asJavascript().contains("displaylogo"));
    }
  }
}
