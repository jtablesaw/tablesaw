package tech.tablesaw.plotly.components.change;

import com.google.common.base.Preconditions;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import tech.tablesaw.plotly.components.Component;

public class ChangeLine extends Component {

  private static final int DEFAULT_WIDTH = 2;
  private static final String DEFAULT_COLOR = "#3D9970";

  private final String color;
  private final int width;

  private ChangeLine(LineBuilder lineBuilder) {

    color = lineBuilder.color;
    width = lineBuilder.width;
  }

  @Override
  public String asJavascript() {
    Writer writer = new StringWriter();
    PebbleTemplate compiledTemplate;

    try {
      compiledTemplate = engine.getTemplate("changeLine_template.html");
      compiledTemplate.evaluate(writer, getContext());
    } catch (PebbleException | IOException e) {
      e.printStackTrace();
    }
    return writer.toString();
  }

  private Map<String, Object> getContext() {
    Map<String, Object> context = new HashMap<>();
    if (!color.equals(DEFAULT_COLOR)) context.put("color", color);
    if (width != DEFAULT_WIDTH) context.put("width", width);
    return context;
  }

  public static LineBuilder builder() {
    return new LineBuilder();
  }

  public static class LineBuilder {

    private String color = DEFAULT_COLOR;
    private int width = DEFAULT_WIDTH;

    /** Sets the color of line bounding the box(es). */
    public LineBuilder color(String color) {
      this.color = color;
      return this;
    }

    /**
     * Sets the width (in px) of line bounding the box(es).
     *
     * @param width greater than or equal to 0
     */
    public LineBuilder width(int width) {
      Preconditions.checkArgument(width >= 0);
      this.width = width;
      return this;
    }

    public ChangeLine build() {
      return new ChangeLine(this);
    }
  }
}
