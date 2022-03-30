package tech.tablesaw.plotly.components;

import java.util.HashMap;
import java.util.Map;

public class Config extends Component {

  private final Boolean displayModeBar;
  private final Boolean responsive;
  private final Boolean displayLogo;

  private Config(Builder builder) {
    this.displayModeBar = builder.displayModeBar;
    this.responsive = builder.responsive;
    this.displayLogo = builder.displayLogo;
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
    context.put("displaylogo", displayLogo);
    return context;
  }

  public static class Builder {

    Boolean displayModeBar;
    Boolean responsive;
    Boolean displayLogo;

    private Builder() {}

    public Builder displayModeBar(boolean displayModeBar) {
      this.displayModeBar = displayModeBar;
      return this;
    }

    public Builder responsive(boolean responsive) {
      this.responsive = responsive;
      return this;
    }

    public Builder displayLogo(boolean displayLogo) {
      this.displayLogo = displayLogo;
      return this;
    }

    public Config build() {
      return new Config(this);
    }
  }
}
