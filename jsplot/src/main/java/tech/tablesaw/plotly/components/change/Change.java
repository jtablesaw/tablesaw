package tech.tablesaw.plotly.components.change;

import java.util.HashMap;
import java.util.Map;
import tech.tablesaw.plotly.components.Component;

public abstract class Change extends Component {

  // private static final ChangeLine DEFAULT_CHANGE_LINE = new LineBuilder().build();

  private final ChangeLine changeLine;
  private final String fillColor;

  @Override
  public String asJavascript() {
    return asJSON();
  }

  Change(ChangeBuilder builder) {
    this.changeLine = builder.changeLine;
    this.fillColor = builder.fillColor;
  }

  @Override
  protected Map<String, Object> getJSONContext() {
    Map<String, Object> context = new HashMap<>();
    if (changeLine != null) context.put("line", changeLine.getJSONContext());
    if (fillColor != null) context.put("fillcolor", fillColor);
    return context;
  }

  @Override
  protected Map<String, Object> getContext() {
    return getJSONContext();
  }

  public static class ChangeBuilder {

    protected String fillColor;
    protected ChangeLine changeLine;

    public ChangeBuilder fillColor(String color) {
      this.fillColor = color;
      return this;
    }

    public ChangeBuilder changeLine(ChangeLine line) {
      this.changeLine = line;
      return this;
    }
  }
}
