package tech.tablesaw.plotly.components;

import com.google.common.base.Preconditions;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class ColorBar extends Component {

  private static final ThicknessMode DEFAULT_THICKNESS_MODE = ThicknessMode.PIXELS;
  private static final double DEFAULT_THICKNESS = 30.0;

  private static final LenMode DEFAULT_LEN_MODE = LenMode.FRACTION;
  private static final double DEFAULT_LEN = 1.0;

  private static final double DEFAULT_X = 1.02;
  private static final double DEFAULT_Y = 0.5;

  private static final int DEFAULT_X_PAD = 10;
  private static final int DEFAULT_Y_PAD = 10;

  private static final Xanchor DEFAULT_X_ANCHOR = Xanchor.LEFT;
  private static final Yanchor DEFAULT_Y_ANCHOR = Yanchor.MIDDLE;

  private static final String DEFAULT_OUTLINE_COLOR = "444";
  private static final String DEFAULT_BORDER_COLOR = "444";

  private static final int DEFAULT_BORDER_WIDTH = 1;
  private static final int DEFAULT_OUTLINE_WIDTH = 0;

  private static final String DEFAULT_BG_COLOR = "rgba(0,0,0,0)";

  /**
   * Determines whether this color bar's thickness (i.e. the measure in the constant color
   * direction) is set in units of plot "fraction" or in "pixels". Use `thickness` to set the value.
   */
  public enum ThicknessMode {
    FRACTION("fraction"),
    PIXELS("pixels");

    private final String value;

    ThicknessMode(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  public enum LenMode {
    FRACTION("fraction"),
    PIXELS("pixels");

    private final String value;

    LenMode(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  public enum Xanchor {
    LEFT("left"),
    CENTER("center"),
    RIGHT("right");

    private final String value;

    Xanchor(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  public enum Yanchor {
    TOP("top"),
    MIDDLE("middle"),
    BOTTOM("bottom");

    private final String value;

    Yanchor(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  private final ThicknessMode thicknessMode;

  private final double thickness;

  private final LenMode lenMode;

  private final double len;

  private final double x;

  private final int xPad;

  private final int yPad;

  private final double y;

  private final Xanchor xAnchor;

  private final Yanchor yAnchor;

  private final String outlineColor;

  private final int outlineWidth;

  private final String borderColor;

  private final int borderWidth;

  private final String bgColor;

  private final TickSettings tickSettings;

  private ColorBar(ColorBarBuilder builder) {
    this.thicknessMode = builder.thicknessMode;
    this.lenMode = builder.lenMode;
    this.thickness = builder.thickness;
    this.len = builder.len;
    this.x = builder.x;
    this.y = builder.y;
    this.xPad = builder.xPad;
    this.yPad = builder.yPad;
    this.xAnchor = builder.xAnchor;
    this.yAnchor = builder.yAnchor;
    this.outlineColor = builder.outlineColor;
    this.borderColor = builder.borderColor;
    this.bgColor = builder.bgColor;
    this.borderWidth = builder.borderWidth;
    this.outlineWidth = builder.outlineWidth;
    this.tickSettings = builder.tickSettings;
  }

  @Override
  public String asJavascript() {
    Writer writer = new StringWriter();
    PebbleTemplate compiledTemplate;

    try {
      compiledTemplate = engine.getTemplate("colorbar_template.html");
      compiledTemplate.evaluate(writer, getContext());
    } catch (PebbleException | IOException e) {
      e.printStackTrace();
    }
    return writer.toString();
  }

  private Map<String, Object> getContext() {

    Map<String, Object> context = new HashMap<>();
    if (!thicknessMode.equals(DEFAULT_THICKNESS_MODE)) context.put("thicknessMode", thicknessMode);
    if (!lenMode.equals(DEFAULT_LEN_MODE)) context.put("lenMode", lenMode);

    if (len != DEFAULT_LEN) context.put("len", len);
    if (thickness != DEFAULT_THICKNESS) context.put("thickness", thickness);

    if (x != DEFAULT_X) context.put("x", x);
    if (y != DEFAULT_Y) context.put("y", y);

    if (xPad != DEFAULT_X_PAD) context.put("xPad", xPad);
    if (yPad != DEFAULT_Y_PAD) context.put("yPad", yPad);

    if (borderWidth != DEFAULT_BORDER_WIDTH) context.put("borderWidth", borderWidth);
    if (outlineWidth != DEFAULT_OUTLINE_WIDTH) context.put("outlineWidth", outlineWidth);

    if (!xAnchor.equals(DEFAULT_X_ANCHOR)) context.put("xAnchor", xAnchor);
    if (!yAnchor.equals(DEFAULT_Y_ANCHOR)) context.put("yAnchor", yAnchor);

    if (!outlineColor.equals(DEFAULT_OUTLINE_COLOR)) context.put("outlineColor", outlineColor);
    if (!borderColor.equals(DEFAULT_BORDER_COLOR)) context.put("borderColor", borderColor);
    if (!bgColor.equals(DEFAULT_BG_COLOR)) context.put("bgColor", bgColor);

    if (tickSettings != null) tickSettings.updateContext(context);
    return context;
  }

  public static ColorBarBuilder builder() {
    return new ColorBarBuilder();
  }

  public static class ColorBarBuilder {

    private ThicknessMode thicknessMode = DEFAULT_THICKNESS_MODE;

    private double thickness = DEFAULT_THICKNESS; // (number greater than or equal to 0)

    private LenMode lenMode = DEFAULT_LEN_MODE;

    private double len = DEFAULT_LEN;

    private double x = DEFAULT_X;

    private int xPad = DEFAULT_X_PAD;

    private int yPad = DEFAULT_Y_PAD;

    private double y = DEFAULT_Y;

    private Xanchor xAnchor = DEFAULT_X_ANCHOR;

    private Yanchor yAnchor = DEFAULT_Y_ANCHOR;

    private String outlineColor = DEFAULT_OUTLINE_COLOR;

    private int outlineWidth = DEFAULT_OUTLINE_WIDTH;

    private String borderColor = DEFAULT_BORDER_COLOR;

    private int borderWidth = DEFAULT_BORDER_WIDTH;

    private String bgColor = DEFAULT_BG_COLOR;

    private TickSettings tickSettings;

    /**
     * Sets the thickness of the color bar, This measure excludes the size of the padding, ticks and
     * labels.
     *
     * @param thickness a double greater than 0
     * @return this ColorBar
     */
    public ColorBarBuilder thickness(double thickness) {
      Preconditions.checkArgument(thickness >= 0);
      this.thickness = thickness;
      return this;
    }

    /**
     * Sets the length of the color bar, This measure excludes the size of the padding, ticks and
     * labels.
     *
     * @param len a double greater than 0
     * @return this ColorBar
     */
    public ColorBarBuilder len(double len) {
      Preconditions.checkArgument(len >= 0);
      this.len = len;
      return this;
    }

    /**
     * Determines whether this color bar's length (i.e. the measure in the color variation
     * direction) is set in units of plot "fraction" or in "pixels. Use `len` to set the value.
     */
    public ColorBarBuilder lenMode(LenMode lenMode) {
      this.lenMode = lenMode;
      return this;
    }

    public ColorBarBuilder thicknessMode(ThicknessMode mode) {
      this.thicknessMode = mode;
      return this;
    }

    /**
     * A number between or equal to -2and 3) default:1.02 Sets the x position of the color bar(in
     * plot fraction).
     */
    public ColorBarBuilder x(double x) {
      Preconditions.checkArgument(x >= -2 && x <= 3);
      this.x = x;
      return this;
    }

    /**
     * A number between or equal to -2and 3) default:0.5 Sets the y position of the color bar (in
     * plot fraction).
     */
    public ColorBarBuilder y(double y) {
      Preconditions.checkArgument(y >= -2 && y <= 3);
      this.y = y;
      return this;
    }

    /**
     * Sets this color bar's horizontal position anchor. This anchor binds the `x` position to the
     * "left", "center" or "right" of the color bar.
     */
    public ColorBarBuilder xAnchor(Xanchor xAnchor) {
      this.xAnchor = xAnchor;
      return this;
    }

    /**
     * Sets this color bar's vertical position anchor This anchor binds the `y` position to the
     * "top", "middle" or "bottom" of the color bar.
     */
    public ColorBarBuilder yAnchor(Yanchor yAnchor) {
      this.yAnchor = yAnchor;
      return this;
    }

    /** Sets the amount of paddng (in px) along the y direction. */
    public ColorBarBuilder yPad(int yPad) {
      Preconditions.checkArgument(yPad >= 0);
      this.yPad = yPad;
      return this;
    }

    /** Sets the amount of padding(in px) along the x direction. */
    public ColorBarBuilder xPad(int xPad) {
      Preconditions.checkArgument(y >= 0);
      this.xPad = xPad;
      return this;
    }

    /** Sets the axis line color. */
    public ColorBarBuilder outlineColor(String outlineColor) {
      this.outlineColor = outlineColor;
      return this;
    }

    /** Sets the color of the border enclosing this color bar. */
    public ColorBarBuilder borderColor(String color) {
      this.borderColor = color;
      return this;
    }

    /** Sets the color of padded area. */
    public ColorBarBuilder bgColor(String color) {
      this.bgColor = color;
      return this;
    }

    /** Sets the width (in px) or the border enclosing this color bar. */
    public ColorBarBuilder borderWidth(int width) {
      Preconditions.checkArgument(width >= 0);
      this.borderWidth = width;
      return this;
    }

    /** Sets the width (in px) of the axis line. */
    public ColorBarBuilder outlineWidth(int width) {
      Preconditions.checkArgument(width >= 0);
      this.outlineWidth = width;
      return this;
    }

    public ColorBarBuilder tickSettings(TickSettings tickSettings) {
      this.tickSettings = tickSettings;
      return this;
    }

    public ColorBar build() {
      return new ColorBar(this);
    }
  }
}
