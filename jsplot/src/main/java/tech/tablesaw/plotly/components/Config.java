package tech.tablesaw.plotly.components;

import java.util.HashMap;
import java.util.Map;

public class Config extends Component {

  private final Boolean displayModeBar;
  private final Boolean responsive;

  private Config(Builder builder) {
    this.displayModeBar = builder.displayModeBar;
    this.responsive = builder.responsive;
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public String asJavascript() {
    return "var config = " + asJSON();
  }

  @Override
  protected Map<String, Object> getJSONContext() {
    return getContext();
  }

  @Override
  protected Map<String, Object> getContext() {
    Map<String, Object> context = new HashMap<>();
    context.put("displayModeBar", displayModeBar);
    context.put("responsive", responsive);
    return context;
  }

  public static class Builder {

    Boolean displayModeBar;
    Boolean responsive;

    private Builder() {}

    public Builder displayModeBar(boolean displayModeBar) {
      this.displayModeBar = displayModeBar;
      return this;
    }

    public Builder responsive(boolean responsive) {
      this.responsive = responsive;
      return this;
    }

    public Config build() {
      return new Config(this);
    }
  }
}
