package tech.tablesaw.plotly.components.threeD;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import tech.tablesaw.plotly.components.Component;

class CameraComponent extends Component {

  private final double x;
  private final double y;
  private final double z;

  CameraComponent(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  @Override
  public String asJavascript() {
    Writer writer = new StringWriter();
    PebbleTemplate compiledTemplate;

    try {
      compiledTemplate = engine.getTemplate("xyz_template.html");

      compiledTemplate.evaluate(writer, getContext());
    } catch (PebbleException | IOException e) {
      e.printStackTrace();
    }
    return writer.toString();
  }

  private Map<String, Object> getContext() {
    Map<String, Object> context = new HashMap<>();
    context.put("x", x);
    context.put("y", y);
    context.put("z", z);
    return context;
  }
}
