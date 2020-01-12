package tech.tablesaw.plotly.components.threeD;

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
    return asJavascript("xyz_template.html");
  }

  @Override
  protected Map<String, Object> getContext() {
    Map<String, Object> context = new HashMap<>();
    context.put("x", x);
    context.put("y", y);
    context.put("z", z);
    return context;
  }
}
