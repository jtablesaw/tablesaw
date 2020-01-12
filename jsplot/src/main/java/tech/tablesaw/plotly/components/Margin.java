package tech.tablesaw.plotly.components;

import java.util.HashMap;
import java.util.Map;

/** The margin for the plot */
public class Margin extends Component {

  /** The left margin, in px */
  private final int left;

  /** The right margin, in px */
  private final int right;

  /** The top margin, in px */
  private final int top;

  /** The bottom margin, in px */
  private final int bottom;

  /** The amount of padding between the plotting area and the axis lines, in px */
  private final int pad;

  private final boolean autoExpand;

  public static MarginBuilder builder() {
    return new MarginBuilder();
  }

  private Margin(MarginBuilder builder) {
    this.left = builder.left;
    this.right = builder.right;
    this.top = builder.top;
    this.bottom = builder.bottom;
    this.pad = builder.pad;
    this.autoExpand = builder.autoExpand;
  }

  @Override
  public String asJavascript() {
    return asJavascript("margin_template.html");
  }

  @Override
  protected Map<String, Object> getContext() {
    Map<String, Object> context = new HashMap<>();
    context.put("top", top);
    context.put("bottom", bottom);
    context.put("right", right);
    context.put("left", left);
    context.put("pad", pad);
    context.put("autoExpand", autoExpand);
    return context;
  }

  public static class MarginBuilder {
    /** The left margin, in px */
    private int left = 80;

    /** The right margin, in px */
    private int right = 80;

    /** The top margin, in px */
    private int top = 100;

    /** The bottom margin, in px */
    private int bottom = 80;

    /** The amount of padding between the plotting area and the axis lines, in px */
    private int pad = 0;

    private boolean autoExpand = true;

    private MarginBuilder() {}

    public MarginBuilder top(int top) {
      this.top = top;
      return this;
    }

    public MarginBuilder bottom(int bottom) {
      this.bottom = bottom;
      return this;
    }

    public MarginBuilder left(int left) {
      this.left = left;
      return this;
    }

    public MarginBuilder right(int right) {
      this.right = right;
      return this;
    }

    public MarginBuilder padding(int padding) {
      this.pad = padding;
      return this;
    }

    public MarginBuilder autoExpand(boolean autoExpand) {
      this.autoExpand = autoExpand;
      return this;
    }

    public Margin build() {
      return new Margin(this);
    }
  }
}
