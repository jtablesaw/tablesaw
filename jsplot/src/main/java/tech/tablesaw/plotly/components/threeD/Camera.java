package tech.tablesaw.plotly.components.threeD;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import tech.tablesaw.plotly.components.Component;

public class Camera extends Component {

  private final Center center;
  private final Up up;
  private final Eye eye;

  private Camera(CameraBuilder builder) {
    this.eye = builder.eye;
    this.up = builder.up;
    this.center = builder.center;
  }

  private Map<String, Object> getContext() {
    Map<String, Object> context = new HashMap<>();
    context.put("up", up);
    context.put("eye", eye);
    context.put("center", center);
    return context;
  }

  @Override
  public String asJavascript() {
    Writer writer = new StringWriter();
    PebbleTemplate compiledTemplate;
    try {
      compiledTemplate = engine.getTemplate("camera_template.html");

      compiledTemplate.evaluate(writer, getContext());
    } catch (PebbleException | IOException e) {
      e.printStackTrace();
    }
    return writer.toString();
  }

  public CameraBuilder cameraBuilder() {
    return new CameraBuilder();
  }

  public static class CameraBuilder {

    private Center center;
    private Up up;
    private Eye eye;

    public CameraBuilder xAxis(Center center) {
      this.center = center;
      return this;
    }

    public CameraBuilder yAxis(Up up) {
      this.up = up;
      return this;
    }

    public CameraBuilder zAxis(Eye eye) {
      this.eye = eye;
      return this;
    }

    public Camera build() {
      return new Camera(this);
    }
  }
}
