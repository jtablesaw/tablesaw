package tech.tablesaw.plotly.components;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class Config extends Component {

  private final Boolean displayModeBar;

  private Config(Builder builder) {
    this.displayModeBar = builder.displayModeBar;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public String asJavascript() {
    Writer writer = new StringWriter();
    PebbleTemplate compiledTemplate;

    try {
      compiledTemplate = engine.getTemplate("config_template.html");
      compiledTemplate.evaluate(writer, getContext());
    } catch (PebbleException | IOException e) {
      e.printStackTrace();
    }
    return writer.toString();
  }

  private Map<String, Object> getContext() {
    Map<String, Object> context = new HashMap<>();
    context.put("displayModeBar", displayModeBar);
    return context;
  }

  public static class Builder {

    Boolean displayModeBar;

    private Builder() {}

    public Builder displayModeBar(boolean displayModeBar) {
      this.displayModeBar = displayModeBar;
      return this;
    }

    public Config build() {
      return new Config(this);
    }
  }
}
