package tech.tablesaw.plotly.components;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;

public class Line extends Component {

  public enum Dash {
    SOLID("solid"),
    DASH("dash"),
    DOT("dot"),
    LONG_DASH("longdash"),
    LONG_DASH_DOT("longdashdot"),
    DASH_DOT("dashdot");

    private final String value;

    Dash(String value) {
      this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
      return value;
    }
  }

  private final String color;
  private final double width;
  private final double smoothing;
  private final Shape shape;
  private final Dash dash;
  private final boolean simplify;

  private Line(LineBuilder builder) {
    this.color = builder.color;
    this.shape = builder.shape;
    this.smoothing = builder.smoothing;
    this.dash = builder.dash;
    this.simplify = builder.simplify;
    this.width = builder.width;
  }

  @Override
  public Map<String, Object> getContext() {
    Map<String, Object> context = new HashMap<>();
    context.put("color", color);
    context.put("width", width);
    context.put("shape", shape);
    context.put("smoothing", smoothing);
    context.put("dash", dash);
    context.put("simplify", simplify);
    return context;
  }

  @Override
  protected Map<String, Object> getJSONContext() {
    return getContext();
  }

  @Override
  public String asJavascript() {
    return asJSON();
  }

  /**
   * Determines the shape of the line Linear (i.e. straight lines) is the default. With "spline" the
   * lines are drawn using spline interpolation. The other available values correspond to step-wise
   * line shapes.
   */
  public enum Shape {
    LINEAR("linear"),
    SPLINE("spline"),
    HV("hv"),
    VH("vh"),
    HVH("hvh"),
    VHV("vhv");

    private final String value;

    Shape(String value) {
      this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
      return value;
    }
  }

  public static LineBuilder builder() {
    return new LineBuilder();
  }

  public static class LineBuilder {
    private String color;
    private double width = 2;
    private double smoothing = 1;
    private Shape shape = Shape.LINEAR;
    private Dash dash = Dash.SOLID;
    private boolean simplify = true;

    /** Sets the line color */
    public LineBuilder color(String color) {
      this.color = color;
      return this;
    }

    public LineBuilder width(double width) {
      Preconditions.checkArgument(width >= 0, "Line width must be >= 0.");
      this.width = width;
      return this;
    }

    /**
     * Sets the smoothing parameter
     *
     * @param smoothing a value between 0 and 1.3, inclusive
     */
    public LineBuilder smoothing(double smoothing) {
      Preconditions.checkArgument(
          smoothing >= 0 && smoothing <= 1.3,
          "Smoothing parameter must be between 0 and 1.3, inclusive.");
      this.smoothing = smoothing;
      return this;
    }

    public LineBuilder dash(Dash dash) {
      this.dash = dash;
      return this;
    }

    /**
     * Simplifies lines by removing nearly-collinear points. When transitioning lines, it may be
     * desirable to disable this so that the number of points along the resulting SVG path is
     * unaffected.
     *
     * @param b true if you want to simplify. True is the default
     */
    public LineBuilder simplify(boolean b) {
      this.simplify = b;
      return this;
    }

    public LineBuilder shape(Shape shape) {
      this.shape = shape;
      return this;
    }

    public Line build() {
      return new Line(this);
    }
  }
}
