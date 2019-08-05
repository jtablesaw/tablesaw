package tech.tablesaw.plotly.components;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import tech.tablesaw.plotly.Utils;

public class Gradient extends Component {

  /** Defines the gradient type */
  public enum Type {
    RADIAL("radial"),
    HORIZONTAL("horizontal"),
    VERTICAL("vertical"),
    NONE("none");

    private final String value;

    Type(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  private final Type type;
  private final String[] color;

  private Gradient(GradientBuilder builder) {
    this.type = builder.type;
    color = builder.color;
  }

  @Override
  public String asJavascript() {
    Writer writer = new StringWriter();
    PebbleTemplate compiledTemplate;

    try {
      compiledTemplate = engine.getTemplate("gradient_template.html");
      compiledTemplate.evaluate(writer, getContext());
    } catch (PebbleException | IOException e) {
      e.printStackTrace();
    }
    return writer.toString();
  }

  private Map<String, Object> getContext() {
    Map<String, Object> context = new HashMap<>();
    context.put("type", type);
    if (color != null && color.length > 0) {
      if (color.length > 1) {
        context.put("color", Utils.dataAsString(color));
      } else {
        context.put("color", Utils.quote(color[0]));
      }
    }

    return context;
  }

  public static GradientBuilder builder() {
    return new GradientBuilder();
  }

  public static class GradientBuilder {

    private Type type = Type.NONE;
    private String[] color;

    public GradientBuilder type(Type type) {
      this.type = type;
      return this;
    }

    /** Sets the marker color to a single value */
    public GradientBuilder color(String color) {
      this.color = new String[1];
      this.color[0] = color;
      return this;
    }
    /** Sets the marker color to an array of color values */
    public GradientBuilder color(String[] color) {
      this.color = color;
      return this;
    }

    public Gradient build() {
      return new Gradient(this);
    }
  }
}
