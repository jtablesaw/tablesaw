package tech.tablesaw.plotly.components;

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
    return asJavascript("config_template.html");
  }

  @Override
  protected Map<String, Object> getContext() {
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
