package tech.tablesaw.plotly.components.threeD;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Component;

public class Scene extends Component {

  private final Axis xAxis;

  private final Axis yAxis;

  private final Axis zAxis;

  private final Camera camera;

  private Scene(SceneBuilder builder) {
    this.xAxis = builder.xAxis;
    this.yAxis = builder.yAxis;
    this.zAxis = builder.zAxis;
    this.camera = builder.camera;
  }

  protected Map<String, Object> getContext() {
    Map<String, Object> context = new HashMap<>();
    if (xAxis != null) {
      context.put("xAxis", xAxis);
    }
    if (yAxis != null) {
      context.put("yAxis", yAxis);
    }
    if (zAxis != null) {
      context.put("zAxis", zAxis);
    }
    if (camera != null) {
      context.put("camera", camera);
    }
    return context;
  }

  public static Scene.SceneBuilder sceneBuilder() {
    return new Scene.SceneBuilder();
  }

  @Override
  public String asJavascript() {
    Writer writer = new StringWriter();
    PebbleTemplate compiledTemplate;
    try {
      compiledTemplate = engine.getTemplate("scene_template.html");
      compiledTemplate.evaluate(writer, getContext());
    } catch (PebbleException e) {
      throw new IllegalStateException(e);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return writer.toString();
  }

  public static class SceneBuilder {

    private Axis xAxis;

    private Axis yAxis;

    private Axis zAxis;

    private Camera camera;

    public SceneBuilder xAxis(Axis axis) {
      this.xAxis = axis;
      return this;
    }

    public SceneBuilder yAxis(Axis axis) {
      this.yAxis = axis;
      return this;
    }

    public SceneBuilder zAxis(Axis axis) {
      this.zAxis = axis;
      return this;
    }

    public SceneBuilder camera(Camera camera) {
      this.camera = camera;
      return this;
    }

    public Scene build() {
      return new Scene(this);
    }
  }
}
