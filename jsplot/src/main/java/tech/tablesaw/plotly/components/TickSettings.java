package tech.tablesaw.plotly.components;

import static tech.tablesaw.plotly.components.TickSettings.DisplayRules.ALL;
import static tech.tablesaw.plotly.components.TickSettings.ExponentFormat.B;

import com.google.common.base.Preconditions;
import java.util.Map;
import tech.tablesaw.plotly.Utils;

public class TickSettings {

  /**
   * Sets the tick mode for this axis. If "auto", the number of ticks is set via `nticks`. If
   * "linear", the placement of the ticks is determined by a starting position `tick0` and a tick
   * step `dtick` If "array", the placement of the ticks is set via `tickvals` and the tick text is
   * `ticktext`.
   */
  public enum TickMode {
    AUTO("auto"),
    LINEAR("linear"),
    ARRAY("array");

    private final String value;

    TickMode(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  /** Determines whether and where ticks are drawn */
  public enum TickPlacement {
    OUTSIDE("outside"),
    INSIDE("inside"),
    NONE("");
    private final String value;

    TickPlacement(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  /** Controls the display of prefixes, suffixes, and exponents on ticks */
  public enum DisplayRules {
    ALL("outside"),
    FIRST("first"),
    LAST("last"),
    NONE("none");
    private final String value;

    DisplayRules(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  /** Controls the display of prefixes on ticks */
  public enum Mirror {
    TRUE("true"),
    FALSE("false"),
    TICKS("ticks"),
    ALL("all"),
    ALL_TICKS("allticks");
    private final String value;

    Mirror(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  /** Controls the display of prefixes on ticks */
  public enum ExponentFormat {
    NONE("none"),
    e("e"),
    E("E"),
    POWER("power"),
    SI("SI"),
    B("B");
    private final String value;

    ExponentFormat(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  private final TickMode tickMode;

  private final int nTicks; // >= 0

  private final Object tick0;
  private final Object dTick;

  private final int
      length; // (number greater than or equal to 0) default: 5 Sets the tick length (in px).
  private final int
      width; // (number greater than or equal to 0) default: 1 , Sets the tick width (in px).
  private final String color; // (color) default: "#444" Sets the tick color.
  private final boolean
      showLabels; // (boolean) default: True Determines whether or not the tick labels are drawn.

  private final TickPlacement placement;
  private final Font tickFont;

  // the values and labels to use when TickMode is ARRAY
  private final Object[] tickText;
  private final double[] tickValues;

  private final Mirror mirror;
  private final int angle;
  private final String prefix;
  private final String suffix;
  private final boolean autoMargin;
  private final DisplayRules showPrefix;
  private final DisplayRules showSuffix;
  private final DisplayRules showExponent;
  private final ExponentFormat exponentFormat;

  private final boolean separateThousands;

  private TickSettings(TickSettingsBuilder builder) {
    this.tickMode = builder.tickMode;
    this.nTicks = builder.nTicks;

    this.color = builder.tickColor;
    this.length = builder.tickLength;
    this.width = builder.tickWidth;
    this.showLabels = builder.showTickLabels;
    this.tickFont = builder.font;
    this.placement = builder.placement;

    tickText = builder.tickText;
    tickValues = builder.tickValues;

    tick0 = builder.tick0;
    dTick = builder.dTick;

    showPrefix = builder.showPrefix;
    showSuffix = builder.showSuffix;
    showExponent = builder.showExponent;
    exponentFormat = builder.exponentFormat;

    autoMargin = builder.autoMargin;

    angle = builder.angle;
    prefix = builder.prefix;
    suffix = builder.suffix;
    mirror = builder.mirror;
    separateThousands = builder.separateThousands;
  }

  protected void updateContext(Map<String, Object> context) {
    context.put("showTickLabels", showLabels);
    context.put("tickLength", length);
    context.put("tickWidth", width);
    context.put("tickColor", color);
    context.put("tickFont", tickFont);
    context.put("ticks", placement);
    if (tickText != null) {
      context.put("tickText", Utils.dataAsString(tickText));
    }
    if (nTicks != 0) {
      context.put("nTicks", nTicks);
    }
    if (dTick != null) {
      context.put("dTick", dTick);
    }
    if (tick0 != null) {
      context.put("tick0", tick0);
    }
    if (showExponent != ALL) {
      context.put("showExponent", showExponent);
    }
    if (exponentFormat != B) {
      context.put("exponentFormat", exponentFormat);
    }
    if (tickValues != null) {
      context.put("tickValues", Utils.dataAsString(tickValues));
    }
    context.put("mirror", mirror);
    context.put("prefix", prefix);
    context.put("suffix", suffix);
    context.put("showPrefix", showPrefix);
    context.put("showSuffix", showSuffix);
    context.put("angle", angle);
    context.put("autoMargin", autoMargin);
    context.put("tickMode", tickMode);
    context.put("separateThousands", separateThousands);
  }

  public static TickSettingsBuilder builder() {
    return new TickSettingsBuilder();
  }

  public static class TickSettingsBuilder {

    private DisplayRules showExponent = ALL;
    private ExponentFormat exponentFormat = B;
    private Object tick0;
    private Object dTick;

    private TickMode tickMode = TickMode.LINEAR;
    private Object[] tickText;
    private double[] tickValues;

    private int tickLength = 5;
    private int tickWidth = 1;
    private String tickColor = "#444";
    private boolean showTickLabels = true;
    private Font font;
    private TickPlacement placement = TickPlacement.INSIDE;
    private int nTicks = 0;

    private int angle = 0;
    private String prefix;
    private String suffix;
    private boolean autoMargin = true;
    private DisplayRules showPrefix = ALL;
    private DisplayRules showSuffix = ALL;
    private Mirror mirror;
    private boolean separateThousands;

    private TickSettingsBuilder() {}

    /**
     * @param tickValues Sets the values at which ticks on this axis appear. Only has an effect if
     *     `tickmode` is set to "array". Used with `ticktext`.
     * @param tickText Sets the text displayed at the ticks position via `tickvals`. Only has an
     *     effect if `tickmode` is set to "array". Used with `tickvals`.
     */
    public TickSettings.TickSettingsBuilder arrayTicks(double[] tickValues, String[] tickText) {
      this.tickValues = tickValues;
      this.tickText = tickText;
      return this;
    }

    public TickSettings.TickSettingsBuilder arrayTicks(double[] tickValues) {
      this.tickValues = tickValues;
      return this;
    }

    public TickSettings.TickSettingsBuilder placement(TickPlacement placement) {
      this.placement = placement;
      return this;
    }

    /**
     * Specifies the maximum number of ticks for the particular axis. The actual number of ticks
     * will be chosen automatically to be less than or equal to `nticks`. Has an effect only if
     * `tickmode` is set to "auto".
     *
     * @param nTicks a non-negative int
     * @return this builder
     */
    public TickSettings.TickSettingsBuilder nTicks(int nTicks) {
      Preconditions.checkArgument(nTicks >= 0);
      this.nTicks = nTicks;
      return this;
    }

    public TickSettings.TickSettingsBuilder tickMode(TickMode tickMode) {
      this.tickMode = tickMode;
      return this;
    }

    /** Determines whether or not the tick labels are drawn. */
    public TickSettings.TickSettingsBuilder showTickLabels(boolean showTickLabels) {
      this.showTickLabels = showTickLabels;
      return this;
    }

    /** Sets the tick color */
    public TickSettings.TickSettingsBuilder color(String tickColor) {
      this.tickColor = tickColor;
      return this;
    }

    /** Sets the tick font */
    public TickSettings.TickSettingsBuilder font(Font font) {
      this.font = font;
      return this;
    }

    /**
     * Sets the tick width (in px).
     *
     * @param tickWidth number greater than or equal to 0
     */
    public TickSettings.TickSettingsBuilder width(int tickWidth) {
      Preconditions.checkArgument(tickWidth >= 0);
      this.tickWidth = tickWidth;
      return this;
    }

    /**
     * Sets the tick length (in px).
     *
     * @param tickLength number greater than or equal to 0
     */
    public TickSettings.TickSettingsBuilder length(int tickLength) {
      Preconditions.checkArgument(tickLength >= 0);
      this.tickLength = tickLength;
      return this;
    }

    /** Determines whether long tick labels automatically grow the figure margins. */
    public TickSettings.TickSettingsBuilder autoMargin(boolean adjust) {
      this.autoMargin = adjust;
      return this;
    }

    public TickSettings.TickSettingsBuilder separateThousands(boolean separate) {
      this.separateThousands = separate;
      return this;
    }

    /** */
    public TickSettings.TickSettingsBuilder showSuffix(DisplayRules showSuffix) {
      this.showSuffix = showSuffix;
      return this;
    }

    /** */
    public TickSettings.TickSettingsBuilder showExponent(DisplayRules showExponent) {
      this.showExponent = showExponent;
      return this;
    }

    /**
     * If "all", all exponents are shown besides their significands. If "first", only the exponent
     * of the first tick is shown. If "last", only the exponent of the last tick is shown. If
     * "none", no exponents appear.
     */
    public TickSettings.TickSettingsBuilder exponentFormat(ExponentFormat format) {
      this.exponentFormat = format;
      return this;
    }

    /**
     * If "all", all tick labels are displayed with a prefix. If "first", only the first tick is
     * displayed with a prefix. If "last", only the last tick is displayed with a prefix. If "none",
     * tick prefixes are hidden.
     */
    public TickSettings.TickSettingsBuilder showPrefix(DisplayRules showPrefix) {
      this.showPrefix = showPrefix;
      return this;
    }

    /**
     * Determines if the axis lines or/and ticks are mirrored to the opposite side of the plotting
     * area. If "True", the axis lines are mirrored. If "ticks", the axis lines and ticks are
     * mirrored. If "False", mirroring is disable. If "all", axis lines are mirrored on all
     * shared-axes subplots. If "allticks", axis lines and ticks are mirrored on all shared-axes
     * subplots.
     */
    public TickSettings.TickSettingsBuilder mirror(Mirror mirror) {
      this.mirror = mirror;
      return this;
    }

    /**
     * Sets the angle of the tick labels with respect to the horizontal. For example, a `tickangle`
     * of -90 draws the tick labels vertically.
     */
    public TickSettings.TickSettingsBuilder angle(int angle) {
      this.angle = angle;
      return this;
    }

    public TickSettings.TickSettingsBuilder prefix(String prefix) {
      this.prefix = prefix;
      return this;
    }

    public TickSettings.TickSettingsBuilder suffix(String suffix) {
      this.suffix = suffix;
      return this;
    }

    /**
     * TODO: this is pretty hack-y. Add a separate method for dealing with dates and maybe clean up
     * logs too
     *
     * <p>Sets the placement of the first tick on this axis. Use with `dtick`.
     *
     * <p>If the axis `type` is "log", then you must take the log of your starting tick (e.g. to set
     * the starting tick to 100, set the `tick0` to 2) except when `dtick`="L&lt;f&gt;" (see `dtick`
     * for more info).
     *
     * <p>If the axis `type` is "date", it should be a date string, like date data.
     *
     * <p>If the axis `type` is "category", it should be a number, using the scale where each
     * category is assigned a serial number from zero in the order it appears.
     */
    public TickSettings.TickSettingsBuilder tick0(Object tick0) {
      this.tick0 = tick0;
      return this;
    }

    /**
     * TODO: this is pretty hack-y. Add a separate method for dealing with dates and maybe clean up
     * logs too
     *
     * <p>Sets the step in-between ticks on this axis. Use with `tick0`. Must be a positive number,
     * or special strings available to "log" and "date" axes.
     *
     * <p>If the axis `type` is "log", then ticks are set every 10^(n"dtick) where n is the tick
     * number. For example, to set a tick mark at 1, 10, 100, 1000, ... set dtick to 1. To set tick
     * marks at 1, 100, 10000, ... set dtick to 2. To set tick marks at 1, 5, 25, 125, 625, 3125,
     * ... set dtick to log_10(5), or 0.69897000433. "log" has several special values; "L&lt;f&gt;",
     * where `f` is a positive number, gives ticks linearly spaced in value (but not position).
     *
     * <p>For example `tick0` = 0.1, `dtick` = "L0.5" will put ticks at 0.1, 0.6, 1.1, 1.6 etc. To
     * show powers of 10 plus small digits between, use "D1" (all digits) or "D2" (only 2 and 5).
     * `tick0` is ignored for "D1" and "D2".
     *
     * <p>If the axis `type` is "date", then you must convert the time to milliseconds. For example,
     * to set the interval between ticks to one day, set `dtick` to 86400000.0. "date" also has
     * special values "M&lt;n&gt;" gives ticks spaced by a number of months. `n` must be a positive
     * integer. To set ticks on the 15th of every third month, set `tick0` to "2000-01-15" and
     * `dtick` to "M3". To set ticks every 4 years, set `dtick` to "M48"
     */
    public TickSettings.TickSettingsBuilder dTick(Object dTick) {
      this.dTick = dTick;
      return this;
    }

    public TickSettings build() {
      return new TickSettings(this);
    }
  }
}
