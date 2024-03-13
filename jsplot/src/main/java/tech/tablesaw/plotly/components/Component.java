package tech.tablesaw.plotly.components;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.Map;

public abstract class Component {

  protected static final ObjectMapper mapper = new ObjectMapper();

  static {
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.setSerializationInclusion(Include.NON_NULL);
    mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);
  }

  private final PebbleEngine engine = TemplateUtils.getNewEngine();

  protected PebbleEngine getEngine() {
    return engine;
  }

  @Deprecated
  public abstract String asJavascript();

  @Deprecated
  protected abstract Map<String, Object> getContext();

  protected Map<String, Object> getJSONContext() {
    return null;
  }

  public String asJSON() {
    StringWriter w = new StringWriter();
    try {
      mapper.writeValue(w, getJSONContext());
    } catch (IOException ioe) {
      throw new UncheckedIOException(ioe);
    }
    return w.toString();
  }

  protected String asJavascript(String filename) {
    Writer writer = new StringWriter();
    PebbleTemplate compiledTemplate;

    try {
      compiledTemplate = getEngine().getTemplate(filename);
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
