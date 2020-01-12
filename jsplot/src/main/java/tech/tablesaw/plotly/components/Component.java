package tech.tablesaw.plotly.components;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Map;

public abstract class Component {

  protected final PebbleEngine engine = TemplateUtils.getNewEngine();

  public abstract String asJavascript();

  protected abstract Map<String, Object> getContext();

  protected String asJavascript(String filename) {
    Writer writer = new StringWriter();
    PebbleTemplate compiledTemplate;

    try {
      compiledTemplate = engine.getTemplate(filename);
      compiledTemplate.evaluate(writer, getContext());
    } catch (PebbleException e) {
      throw new IllegalStateException(e);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return writer.toString();
  }

  @Override
  public String toString() {
    return asJavascript();
  }
}
