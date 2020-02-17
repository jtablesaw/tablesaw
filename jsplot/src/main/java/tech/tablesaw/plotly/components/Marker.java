package tech.tablesaw.plotly.components;

import static tech.tablesaw.plotly.components.Marker.SizeMode.DIAMETER;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.plotly.Utils;

public class Marker extends Component {

  public enum SizeMode {
    AREA("area"),
    DIAMETER("diameter");

    private final String value;

    SizeMode(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  /** Predefined palettes */
  public enum Palette {
    GREYS("Greys"),
    GREENS("Greens"),
    YL_GN_BU("YlGnBu"),
    YL_OR_RD("YlOrRd"),
    BLUE_RED("Bluered"),
    RD_BU("RdBu"),
    REDS("Reds"),
    BLUES("Blues"),
    PICNIC("Picnic"),
    RAINBOW("Rainbow"),
    PORTLAND("Portland"),
    JET("Jet"),
    HOT("Hot"),
    BLACKBODY("Blackbody"),
    EARTH("Earth"),
    ELECTRIC("Electric"),
    VIRIDIS("Viridis"),
    CIVIDIS("Cividis");

    private final String value;

    Palette(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  private static final boolean DEFAULT_C_AUTO = true;
  private static final boolean DEFAULT_AUTO_COLOR_SCALE = true;
  private static final boolean DEFAULT_SHOW_SCALE = false;
  private static final boolean DEFAULT_REVERSE_SCALE = false;
  private static final double DEFAULT_OPACITY = 1.0;
  private static final SizeMode DEFAULT_SIZE_MODE = DIAMETER;

  private final double[] size;
  private final Line line;
  private final String[] color;
  private final Palette colorScalePalette;
  private final boolean cAuto;
  private final double cMin;
  private final double cMax;
  private final boolean autoColorScale;
  private final boolean showScale;
  private final boolean reverseScale;
  private final double opacity;
  private final Symbol symbol;
  private final SizeMode sizeMode;
  private final Gradient gradient;
  private final double[] colorArray;
  private final ColorBar colorBar;

  public static MarkerBuilder builder() {
    return new MarkerBuilder();
  }

  private Marker(MarkerBuilder builder) {
    symbol = builder.symbol;
    line = builder.line;
    size = builder.size;
    color = builder.color;
    colorArray = builder.colorArray;
    gradient = builder.gradient;
    colorScalePalette = builder.colorScalePalette;
    cAuto = builder.cAuto;
    cMin = builder.cMin;
    cMax = builder.cMax;
    autoColorScale = builder.autoColorScale;
    showScale = builder.showScale;
    reverseScale = builder.reverseScale;
    opacity = builder.opacity;
    sizeMode = builder.sizeMode;
    colorBar = builder.colorBar;
  }

  @Override
  public String asJavascript() {
    return asJavascript("marker_template.html");
  }

  @Override
  protected Map<String, Object> getContext() {
    Map<String, Object> context = new HashMap<>();
    context.put("size", size.length == 1 ? size[0] : Utils.dataAsString(size));
    if (colorScalePalette != null) {
      context.put("colorScale", colorScalePalette);
    }
    if (cAuto != DEFAULT_C_AUTO) context.put("cAuto", cAuto);
    if (color != null && color.length > 0) {
      if (color.length > 1) {
        context.put("color", Utils.dataAsString(color));
        context.put("cMin", cMin);
        context.put("cMax", cMax);
      } else {
        context.put("color", Utils.quote(color[0]));
      }
    } else if (colorArray != null) {
      context.put("color", Utils.dataAsString(colorArray));
    }
    if (line != null) context.put("line", line.asJavascript());
    if (autoColorScale != DEFAULT_AUTO_COLOR_SCALE) context.put("autoColorScale", autoColorScale);
    if (showScale != DEFAULT_SHOW_SCALE) context.put("showScale", showScale);
    if (reverseScale != DEFAULT_REVERSE_SCALE) context.put("reverseScale", reverseScale);
    if (opacity != DEFAULT_OPACITY) context.put("opacity", opacity);
    if (sizeMode != DEFAULT_SIZE_MODE) context.put("sizeMode", sizeMode);
    if (gradient != null) context.put("gradient", gradient);
    if (colorBar != null) context.put("colorBar", colorBar.asJavascript());
    context.put("symbol", symbol);
    return context;
  }

  public static class MarkerBuilder {

    private double[] size = {6};

    // Note, a marker can have a color, or color array, but not both
    private String[] color;
    private double[] colorArray;

    private Gradient gradient;
    private Palette colorScalePalette;
    private boolean cAuto = DEFAULT_C_AUTO;
    private double cMin;
    private double cMax;
    private Line line;
    private boolean autoColorScale = DEFAULT_AUTO_COLOR_SCALE;
    private boolean showScale = DEFAULT_SHOW_SCALE;
    private boolean reverseScale = DEFAULT_REVERSE_SCALE;
    private double opacity = DEFAULT_OPACITY;
    private Symbol symbol;
    private SizeMode sizeMode = DEFAULT_SIZE_MODE;
    private ColorBar colorBar;

    public MarkerBuilder size(double... size) {
      String errorMessage = "All sizes in size array must be greater than 0.";
      for (double d : size) {
        Preconditions.checkArgument(d > 0, errorMessage);
      }
      this.size = size;
      return this;
    }

    public MarkerBuilder size(NumericColumn<?> size) {
      return size(size.asDoubleArray());
    }

    /**
     * Has an effect only if `marker.color` is set to a numerical array and `cmin`, `cmax` are also
     * set. In this case, it controls whether the range of colors in `colorscale` is mapped to the
     * range of values in the `color` array (`cauto: True`), or the `cmin`/`cmax` values (`cauto:
     * False`).
     *
     * <p>Defaults to `False` when `cmin`, `cmax` are set by the user.
     */
    public MarkerBuilder cAuto(boolean b) {
      this.cAuto = b;
      return this;
    }

    /**
     * Has an effect only if `marker.color` is set to a numerical array. Reverses the color mapping
     * if True (`cmin` will correspond to the last color in the array and `cmax` will correspond to
     * the first color).
     */
    public MarkerBuilder reverseScale(boolean b) {
      this.reverseScale = b;
      return this;
    }

    /** Sets an outline around the marker */
    public MarkerBuilder line(Line line) {
      this.line = line;
      return this;
    }

    /** Sets a gradient for the marker */
    public MarkerBuilder gradient(Gradient gradient) {
      this.gradient = gradient;
      return this;
    }

    /** Sets the ColorBar to display the scale for the marker */
    public MarkerBuilder colorBar(ColorBar colorBar) {
      this.colorBar = colorBar;
      return this;
    }

    /**
     * Has an effect only if `marker.color` is set to a numerical array. Determines whether the
     * colorscale is a default palette (`autocolorscale: True`) or the palette determined by
     * `marker.colorscale`. In case `colorscale` is unspecified or `autocolorscale` is True, the
     * default palette will be chosen according to whether numbers in the `color` array are all
     * positive, all negative or mixed.
     *
     * <p>Defaults to true
     */
    public MarkerBuilder autoColorScale(boolean b) {
      this.autoColorScale = b;
      return this;
    }

    /**
     * Has an effect only if `marker.color` is set to a numerical array. Sets the lower and upper
     * bound of the color domain. Values should be associated to the `marker.color` array index
     */
    public MarkerBuilder cMinAndMax(double min, double max) {
      this.cMin = min;
      this.cMax = max;
      return this;
    }

    /**
     * Has an effect only if `marker.color` is set to a numerical array. Determines whether or not a
     * colorbar is displayed.
     */
    public MarkerBuilder showScale(boolean b) {
      this.showScale = b;
      return this;
    }

    /**
     * Sets the colorscale and only has an effect if `marker.color` is set to a numerical array. The
     * colorscale must be an array containing arrays mapping a normalized value to an rgb, rgba,
     * hex, hsl, hsv, or named color string.
     *
     * <p>At minimum, a mapping for the lowest (0) and highest (1) values are required. For example,
     * `[[0, 'rgb(0,0,255)', [1, 'rgb(255,0,0)']]`.
     *
     * <p>To control the bounds of the colorscale in color space, use `marker.cmin` and
     * `marker.cmax`.
     */
    public MarkerBuilder colorScale(Palette palette) {
      this.colorScalePalette = palette;
      return this;
    }

    /** Sets the opacity. Value must be between 0 and 1 inclusive */
    public MarkerBuilder opacity(double opacity) {
      Preconditions.checkArgument(opacity >= 0 && opacity <= 1);
      this.opacity = opacity;
      return this;
    }

    /** Sets the marker color to a single value */
    public MarkerBuilder color(String color) {
      this.color = new String[1];
      this.color[0] = color;
      this.colorArray = null;
      return this;
    }

    /** Sets the marker color to an array of color values */
    public MarkerBuilder color(String[] color) {
      this.color = color;
      this.colorArray = null;
      return this;
    }

    /**
     * Sets the marker color to an array of numeric values for use when a color scale is provided
     */
    public MarkerBuilder color(double[] color) {
      this.colorArray = color;
      this.color = null;
      return this;
    }

    /** Sets the symbol for the marker */
    public MarkerBuilder symbol(Symbol symbol) {
      this.symbol = symbol;
      return this;
    }

    /**
     * Sets the size mode for the marker
     *
     * <p>Has an effect only if `marker.size` is set to a numerical array. Sets the rule for which
     * the data in `size` is converted to pixels, either as area or the diameter
     */
    public MarkerBuilder sizeMode(SizeMode sizeMode) {
      this.sizeMode = sizeMode;
      return this;
    }

    public Marker build() {
      return new Marker(this);
    }
  }
}
