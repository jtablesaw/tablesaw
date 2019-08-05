package tech.tablesaw.plotly.components;

import com.mitchellbosecke.pebble.PebbleEngine;

public abstract class Component {

  protected final PebbleEngine engine = TemplateUtils.getNewEngine();

  public abstract String asJavascript();

  @Override
  public String toString() {
    return asJavascript();
  }
}
