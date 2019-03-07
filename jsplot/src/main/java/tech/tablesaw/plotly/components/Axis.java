package tech.tablesaw.plotly.components;

import com.google.common.base.Preconditions;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import tech.tablesaw.plotly.Utils;
import tech.tablesaw.plotly.traces.ScatterTrace;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static tech.tablesaw.plotly.components.Axis.Spikes.SpikeSnap.DATA;

public class Axis extends Component {

    public enum CategoryOrder {
        TRACE("trace"),
        CATEGORY_ASCENDING("category ascending"),
        CATEGORY_DESCENDING("category descending"),
        ARRAY("array");

        private final String value;

        CategoryOrder(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Sets the axis type. By default, plotly attempts to determined the axis type by looking into the data
     * of the traces that referenced the axis in question.
     */
    public enum Type {
        LINEAR("linear"),
        LOG("log"),
        DATE("date"),
        CATEGORY("category"),
        DEFAULT("-");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Determines whether or not the range of this axis is computed in relation to the input data.
     * See `rangemode` for more info. If `range` is provided, then `autorange` is set to "False".
     */
    public enum AutoRange {
        TRUE("true"),
        FALSE("false"),
        REVERSED("reversed");

        private final String value;

        AutoRange(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * If this axis needs to be compressed (either due to its own `scaleanchor` and `scaleratio`
     * or those of the other axis), determines how that happens: by increasing the "range" (default),
     * or by decreasing the "domain".
     */
    public enum Constrain {
        RANGE("range"),
        DOMAIN("domain");

        private final String value;

        Constrain(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * If this axis needs to be compressed (either due to its own `scaleanchor` and `scaleratio`
     * or those of the other axis), determines which direction we push the originally specified plot area.
     * Options are "left", "center" (default), and "right" for x axes, and "top", "middle" (default),
     * and "bottom" for y axes.
     */
    public enum ConstrainToward {
        LEFT("left"),
        CENTER("center"),
        RIGHT("right"),
        TOP("top"),
        MIDDLE("middle"),
        BOTTOM("bottom");

        private final String value;

        ConstrainToward(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * If set to another axis id (e.g. `x2`, `y`), the range of this axis changes together with the range
     * of the corresponding axis such that the scale of pixels per unit is in a constant ratio.
     * Both axes are still zoomable, but when you zoom one, the other will zoom the same amount,
     * keeping a fixed midpoint. `constrain` and `constraintoward` determine how we enforce the constraint.
     * You can chain these, ie `yaxis: {scaleanchor: "x"}, xaxis2: {scaleanchor: "y"}` but you can only link axes
     * of the same `type`.
     * The linked axis can have the opposite letter (to constrain the aspect ratio) or the same letter
     * (to match scales across subplots). Loops (`yaxis: {scaleanchor: "x"}, xaxis: {scaleanchor: "y"}` or longer)
     * are redundant and the last constraint encountered will be ignored to avoid possible inconsistent constraints
     * via `scaleratio`.
     *
     * TODO: Just make this a string?
     */
    public enum ScaleAnchor {
        X("/^x([2-9]|[1-9][0-9]+)?$/"),
        Y("/^y([2-9]|[1-9][0-9]+)?$/");

        private final String value;

        ScaleAnchor(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * If "normal", the range is computed in relation to the extrema of the input data.
     * If "tozero"`, the range extends to 0, regardless of the input data If "nonnegative",
     * the range is non-negative, regardless of the input data.
     */
    public enum RangeMode {
        NORMAL("normal"),
        TO_ZERO("tozero"),
        NON_NEGATIVE("nonnegative");
        private final String value;

        RangeMode(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    /**
     * Determines whether an x (y) axis is positioned at the "bottom" ("left") or "top" ("right") of the plotting area.
     */
    public enum Side {
        left("left"), // DEFAULT
        right("right"),
        top("top"),
        bottom("bottom");

        private final String value;

        Side(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private static final String DEFAULT_COLOR = "#444";
    private static final String DEFAULT_ZERO_LINE_COLOR = "#444";
    private static final String DEFAULT_LINE_COLOR = "#444";
    private static final String DEFAULT_GRID_COLOR = "#eee";
    private static final int DEFAULT_LINE_WIDTH = 1;

    private static final int DEFAULT_ZERO_LINE_WIDTH = 1;
    private static final int DEFAULT_GRID_WIDTH = 1;
    private static final boolean DEFAULT_SHOW_LINE = true;
    private static final boolean DEFAULT_SHOW_GRID = true;
    private static final boolean DEFAULT_ZERO_LINE = false;
    private static final double DEFAULT_SCALE_RATIO = 1.0;
    private static final Constrain DEFAULT_CONSTRAIN_RANGE = Constrain.RANGE;

    private static final AutoRange DEFAULT_AUTO_RANGE = AutoRange.TRUE;
    private static final Type DEFAULT_TYPE = Type.DEFAULT;
    private static final boolean DEFAULT_VISIBLE = true;

    private final String title;
    private final boolean visible;
    private final String color;
    private final Font font;
    private final Font titleFont;
    private final Type type;

    private final RangeMode rangeMode;
    private final AutoRange autoRange;
    private final Object[] range;
    private final boolean fixedRange;  // true means the axis cannot be zoomed
    private final Constrain constrain;
    private final ConstrainToward constrainToward;
    private final double scaleRatio;

    private final Spikes spikes;

    private final int lineWidth;
    private final int zeroLineWidth;
    private final int gridWidth;

    private final String lineColor;
    private final String zeroLineColor;
    private final String gridColor;

    private final boolean showLine;
    private final boolean zeroLine;
    private final boolean showGrid;

    private final Side side;
    private final ScatterTrace.YAxis overlaying;

    private final CategoryOrder categoryOrder;

    private final TickSettings tickSettings;

    public static AxisBuilder builder() {
        return new AxisBuilder();
    }

    private Axis(AxisBuilder builder) {
        title = builder.title;
        titleFont = builder.titleFont;
        type = builder.type;
        visible = builder.visible;
        color = builder.color;
        font = builder.font;
        autoRange = builder.autoRange;
        range = builder.range;
        rangeMode = builder.rangeMode;
        fixedRange = builder.fixedRange;
        tickSettings = builder.tickSettings;
        side = builder.side;
        overlaying = builder.overlaying;
        spikes = builder.spikes;

        showLine = builder.showLine;
        zeroLine = builder.zeroLine;
        showGrid = builder.showGrid;

        lineColor = builder.lineColor;
        zeroLineColor = builder.zeroLineColor;
        gridColor = builder.gridColor;

        lineWidth = builder.lineWidth;
        zeroLineWidth = builder.zeroLineWidth;
        gridWidth = builder.gridWidth;

        constrain = builder.constrain;
        constrainToward = builder.constrainToward;
        scaleRatio = builder.scaleRatio;
        categoryOrder = builder.categoryOrder;
    }

    public String asJavascript() {
        Writer writer = new StringWriter();
        PebbleTemplate compiledTemplate;

        try {
            compiledTemplate = engine.getTemplate("axis_template.html");
            compiledTemplate.evaluate(writer, getContext());
        } catch (PebbleException | IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    private Map<String, Object> getContext() {
        Map<String, Object> context = new HashMap<>();
        context.put("title", title);
        context.put("titleFont", titleFont);
        if(visible != DEFAULT_VISIBLE) context.put("visible", visible);
        if(!type.equals(DEFAULT_TYPE)) context.put("type", type);
        if(!color.equals(DEFAULT_COLOR)) context.put("color", color);
        if (font != null) {
            context.put("font", font);
        }
        if (side != null) {
            context.put("side", side);
        }
        if (overlaying != null) {
            context.put("overlaying", overlaying);
        }
        if (!autoRange.equals(DEFAULT_AUTO_RANGE)) context.put("autoRange", autoRange);
        context.put("rangeMode", rangeMode);
        if (range != null) {
            context.put("range", Utils.dataAsString(range));
        }
        context.put("fixedRange", fixedRange);
        if(scaleRatio != DEFAULT_SCALE_RATIO) context.put("scaleRatio", scaleRatio);
        if(!constrain.equals(DEFAULT_CONSTRAIN_RANGE)) context.put("constrain", constrain);
        if (constrainToward != null) {
            context.put("constrainToward", constrainToward);
        }
        if (spikes != null) {
            spikes.updateContext(context);
        }

        if (tickSettings != null) {
            tickSettings.updateContext(context);
        }

        if (categoryOrder != null) {
            context.put("categoryOrder", categoryOrder);
        }

        if(gridWidth != DEFAULT_GRID_WIDTH) context.put("gridWidth", gridWidth);
        if(lineWidth != DEFAULT_LINE_WIDTH) context.put("lineWidth", lineWidth);
        if(zeroLineWidth != DEFAULT_ZERO_LINE_WIDTH) context.put("zeroLineWidth", zeroLineWidth);
        if(!lineColor.equals(DEFAULT_LINE_COLOR)) context.put("lineColor", lineColor);
        if(!zeroLineColor.equals(DEFAULT_ZERO_LINE_COLOR)) context.put("zeroLineColor", zeroLineColor);
        if(!gridColor.equals(DEFAULT_GRID_COLOR)) context.put("gridColor", gridColor);
        if(showLine != DEFAULT_SHOW_LINE) context.put("showLine", showLine);
        if(zeroLine != DEFAULT_ZERO_LINE) context.put("zeroLine", zeroLine);
        if(showGrid != DEFAULT_SHOW_GRID) context.put("showGrid", showGrid);
        return context;
    }

    public static class AxisBuilder {

        private Constrain constrain = DEFAULT_CONSTRAIN_RANGE;
        private ConstrainToward constrainToward;
        private double scaleRatio = DEFAULT_SCALE_RATIO;

        private Font titleFont;
        private String title = "";
        private boolean visible = DEFAULT_VISIBLE;
        private String color = DEFAULT_COLOR;
        private Font font;
        private Side side;

        private Type type = DEFAULT_TYPE;
        private RangeMode rangeMode = RangeMode.NORMAL;
        private AutoRange autoRange = DEFAULT_AUTO_RANGE;
        private Object[] range;
        private boolean fixedRange = true;  // true means the axis cannot be zoomed

        private TickSettings tickSettings;

        private Spikes spikes = null;

        private boolean showLine = DEFAULT_SHOW_LINE;
        private String lineColor = DEFAULT_LINE_COLOR;
        private int lineWidth = DEFAULT_LINE_WIDTH;

        private boolean zeroLine = DEFAULT_ZERO_LINE;
        private String zeroLineColor = DEFAULT_ZERO_LINE_COLOR;
        private int zeroLineWidth = DEFAULT_ZERO_LINE_WIDTH;

        private boolean showGrid = DEFAULT_SHOW_GRID;
        private String gridColor = DEFAULT_GRID_COLOR;
        private int gridWidth = DEFAULT_GRID_WIDTH;

        private ScatterTrace.YAxis overlaying;

        private CategoryOrder categoryOrder;

        private AxisBuilder() {}

        public AxisBuilder title(String title) {
            this.title = title;
            return this;
        }

        public AxisBuilder titleFont(Font titleFont) {
            this.titleFont = titleFont;
            return this;
        }

        public AxisBuilder type(Type type) {
            this.type = type;
            return this;
        }

        public AxisBuilder categoryOrder(CategoryOrder categoryOrder) {
            this.categoryOrder = categoryOrder;
            return this;
        }

        public AxisBuilder visible(boolean visible) {
            this.visible = visible;
            return this;
        }

        public AxisBuilder side(Side side) {
            this.side = side;
            return this;
        }

        /**
         * Instructs plotly to overly the trace with this axis on top of a trace with another axis
         * @param axisToOverlay The axis we want to overlay
         * @return  this AxisBuilder
         */
        public AxisBuilder overlaying(ScatterTrace.YAxis axisToOverlay) {
            this.overlaying = axisToOverlay;
            return this;
        }

        /**
         * Determines whether or not this axis is zoom-able. If True, then zoom is disabled.
         */
        public AxisBuilder fixedRange(boolean fixedRange) {
            this.fixedRange = fixedRange;
            return this;
        }

        public AxisBuilder color(String color) {
            this.color = color;
            return this;
        }

        public AxisBuilder font(Font font) {
            this.font = font;
            return this;
        }

        /**
         * If "normal", the range is computed in relation to the extrema of the input data.
         * If "tozero"`, the range extends to 0, regardless of the input data
         * If "nonnegative", the range is non-negative, regardless of the input data.
         *
         * The default is normal.
         */
        public AxisBuilder rangeMode(RangeMode rangeMode) {
            this.rangeMode = rangeMode;
            return this;
        }

        public AxisBuilder spikes(Spikes spikes) {
            this.spikes = spikes;
            return this;
        }

        /**
         * Determines whether or not the range of this axis is computed in relation to the input data.
         * See `rangemode` for more info. If `range` is provided, then `autorange` is set to "False".
         */
        public AxisBuilder autoRange(AutoRange autoRange) {
            this.autoRange = autoRange;
            if (range != null && autoRange != AutoRange.FALSE) {
                throw new IllegalArgumentException("Can't set autoRange to anything but FALSE after specifying a range.");
            }
            return this;
        }

        /**
         * Sets the range of this axis. If the axis `type` is "log", then you must take the log of your desired
         * range (e.g. to set the range from 1 to 100, set the range from 0 to 2).
         *
         * If the axis `type` is "date", it should be date strings, like date data,
         * though Date objects and unix milliseconds will be accepted and converted to strings.
         * If the axis `type` is "category", it should be numbers, using the scale where each category is assigned
         * a serial number from zero in the order it appears.
         */
        public AxisBuilder range(Object[] range) {
            this.range = range;
            this.autoRange = AutoRange.FALSE;
            return this;
        }

        /**
         * Sets the range of this axis. If the axis `type` is "log", then you must take the log of your desired
         * range (e.g. to set the range from 1 to 100, set the range from 0 to 2).
         *
         * If the axis `type` is "date", it should be date strings, like date data,
         * though Date objects and unix milliseconds will be accepted and converted to strings.
         * If the axis `type` is "category", it should be numbers, using the scale where each category is assigned
         * a serial number from zero in the order it appears.
         */
        public AxisBuilder range(Object low, Object high) {
            Object[] range = new Object[2];
            range[0] = low;
            range[1] = high;
            this.range = range;
            this.autoRange = AutoRange.FALSE;
            return this;
        }

        public AxisBuilder constrain(Constrain constrain) {
            this.constrain = constrain;
            return this;
        }

        public AxisBuilder constrainToward(ConstrainToward constrainToward) {
            this.constrainToward = constrainToward;
            return this;
        }

        /**
         * If this axis is linked to another by `scaleanchor`, this determines the pixel to unit scale ratio.
         * For example, if this value is 10, then every unit on this axis spans 10 times the number of pixels
         * as a unit on the linked axis. Use this for example to create an elevation profile where the vertical
         * scale is exaggerated a fixed amount with respect to the horizontal.
         * @param scaleRatio    a number &gt;= 1
         * @return  this AxisBuilder
         */
        public AxisBuilder scaleRatio(double scaleRatio) {
            Preconditions.checkArgument(scaleRatio >= 1.0);
            this.scaleRatio = scaleRatio;
            return this;
        }

        /**
         * Defines all the settings related to the display of tick marks on this axis
         */
        public AxisBuilder tickSettings(TickSettings tickSettings) {
            this.tickSettings = tickSettings;
            return this;
        }

        public AxisBuilder lineWidth(int lineWidth) {
            Preconditions.checkArgument(lineWidth >= 0);
            this.lineWidth = lineWidth;
            return this;
        }

        public AxisBuilder zeroLineWidth(int zeroLineWidth) {
            Preconditions.checkArgument(zeroLineWidth >= 0);
            this.zeroLineWidth = zeroLineWidth;
            return this;
        }

        public AxisBuilder gridWidth(int width) {
            Preconditions.checkArgument(width >= 0);
            this.gridWidth = width;
            return this;
        }

        public AxisBuilder lineColor(String color) {
            this.lineColor = color;
            return this;
        }

        public AxisBuilder gridColor(String color) {
            this.gridColor = color;
            return this;
        }

        public AxisBuilder zeroLineColor(String color) {
            this.zeroLineColor = color;
            return this;
        }

        public AxisBuilder showLine(boolean showLine) {
            this.showLine = showLine;
            return this;
        }

        public AxisBuilder showGrid(boolean showGrid) {
            this.showGrid = showGrid;
            return this;
        }

        public AxisBuilder showZeroLine(boolean zeroLine) {
            this.zeroLine = zeroLine;
            return this;
        }

        public Axis build() {
            return new Axis(this);
        }
    }

    public static class Spikes {
        private final String color;
        private final int thickness;
        private final String dash;
        private final SpikeMode mode;
        private final SpikeSnap snap;

        private Spikes(SpikesBuilder builder) {
            this.color = builder.color;
            this.thickness = builder.thickness;
            this.dash = builder.dash;
            this.mode = builder.mode;
            this.snap = builder.snap;
        }

        private void updateContext(Map<String, Object> context) {
            context.put("showSpikes", true);
            context.put("spikeMode", mode);
            context.put("spikeThickness", thickness);
            context.put("spikeDash", dash);
            context.put("spikeColor", color);
            context.put("spikeSnap", snap);
        }

        public enum SpikeSnap {
            DATA("data"),
            CURSOR("cursor");

            private final String value;

            SpikeSnap(String value) {
                this.value = value;
            }

            @Override
            public String toString() {
                return value;
            }
        }

        public enum SpikeMode {
            TO_AXIS("toaxis"),
            ACROSS("across"),
            MARKER("marker"),
            TO_AXIS_AND_ACROSS("toaxis+across"),
            TO_AXIS_AND_MARKER("toaxis+marker"),
            ACROSS_AND_MARKER("across+marker"),
            TO_AXIS_AND_ACROSS_AND_MARKER("toaxis+across+marker");

            private final String value;

            SpikeMode(String value) {
                this.value = value;
            }

            @Override
            public String toString() {
                return value;
            }
        }

        public static SpikesBuilder builder() {
            return new SpikesBuilder();
        }

        public static class SpikesBuilder {
            private String color = null;
            private int thickness = 3;
            private String dash = "dash";
            private SpikeMode mode = SpikeMode.TO_AXIS;
            private SpikeSnap snap = DATA;

            private SpikesBuilder() {}

            public SpikesBuilder color(String color) {
                this.color = color;
                return this;
            }

            /**
             * Sets the dash style of lines. Set to a dash type string ("solid", "dot", "dash", "longdash", "dashdot",
             * or "longdashdot") or a dash length list in px (eg "5px,10px,2px,2px").
             */
            public SpikesBuilder dash(String dash) {
                this.dash = dash;
                return this;
            }

            /**
             * Any combination of "toaxis", "across", "marker"
             * examples: "toaxis", "across", "toaxis+across", "toaxis+across+marker"
             * default: "toaxis"
             */
            public SpikesBuilder mode(SpikeMode mode) {
                this.mode = mode;
                return this;
            }

            /**
             * Determines whether spikelines are stuck to the cursor or to the closest datapoints.
             * default: DATA
             */
            public SpikesBuilder snap(SpikeSnap snap) {
                this.snap = snap;
                return this;
            }

            /**
             * Sets the width (in px) of the zero line.
             * default: 3
             */
            public SpikesBuilder thickness(int thickness) {
                this.thickness = thickness;
                return this;
            }

            public Spikes build() {
                return new Spikes(this);
            }
        }
    }
}
