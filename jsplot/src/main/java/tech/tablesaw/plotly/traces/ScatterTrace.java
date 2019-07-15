package tech.tablesaw.plotly.traces;

import com.google.common.base.Preconditions;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.columns.Column;
import tech.tablesaw.plotly.components.HoverLabel;
import tech.tablesaw.plotly.components.Line;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.components.change.Decreasing;
import tech.tablesaw.plotly.components.change.Increasing;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import static tech.tablesaw.plotly.Utils.dataAsString;

public class ScatterTrace extends AbstractTrace {

    private static final Fill DEFAULT_FILL = Fill.NONE;
    private static final double DEFAULT_WHISKER_WIDTH = 0;

    public enum Fill {
        NONE("none"),
        TO_ZERO_Y("tozeroy"),
        TO_ZERO_X("tozerox"),
        TO_NEXT_Y("tonexty"),
        TO_NEXT_X("tonextx"),
        TO_SELF("toself"),
        TO_NEXT("tonext");

        private final String value;

        Fill(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum YAxis {
        Y("y"), // DEFAULT
        Y2("y2"),
        Y3("y3"),
        Y4("y4");

        private final String value;

        YAxis(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private final Fill fill;
    private final String fillColor;
    private final double[] y;
    private final Object[] x;
    private final String[] text;
    private final Mode mode;
    private final HoverLabel hoverLabel;
    private final boolean showLegend;
    private final Marker marker;
    private final Line line;
    private final YAxis yAxis;

    private final double[] open;
    private final double[] high;
    private final double[] low;
    private final double[] close;
    private final double whiskerWidth;
    private final Increasing increasing;
    private final Decreasing decreasing;

    public static ScatterBuilder builder(double[] x, double[] y) {
        return new ScatterBuilder(x, y);
    }

    public static ScatterBuilder builder(DateColumn x, NumericColumn<? extends Number> y) {
        return new ScatterBuilder(x, y);
    }

    public static ScatterBuilder builder(Column<?> x, NumericColumn<? extends Number> y) {
        return new ScatterBuilder(x, y);
    }

    public static ScatterBuilder builder(Column<?> x, NumericColumn<? extends Number> open, NumericColumn<? extends Number> high, NumericColumn<? extends Number> low, NumericColumn<? extends Number> close) {
        return new ScatterBuilder(x, open, high, low, close);
    }

    public static ScatterBuilder builder(DateTimeColumn x, NumericColumn<? extends Number> y) {
        return new ScatterBuilder(x, y);
    }

    public static ScatterBuilder builder(TimeColumn x, NumericColumn<? extends Number> y) {
        return new ScatterBuilder(x, y);
    }

    private ScatterTrace(ScatterBuilder builder) {
        super(builder);
        this.mode = builder.mode;
        this.y = builder.y;
        this.x = builder.x;
        this.text = builder.text;
        this.marker = builder.marker;
        this.hoverLabel = builder.hoverLabel;
        this.showLegend = builder.showLegend;
        this.line = builder.line;
        this.fill = builder.fill;
        this.fillColor = builder.fillColor;
        this.yAxis = builder.yAxis;
        this.open = builder.open;
        this.high = builder.high;
        this.low = builder.low;
        this.close = builder.close;
        this.whiskerWidth = builder.whiskerWidth;
        this.increasing = builder.increasing;
        this.decreasing = builder.decreasing;
    }

    private Map<String, Object> getContext(int i) {

        Map<String, Object> context = super.getContext();
        context.put("variableName", "trace" + i);
        context.put("mode", mode);
        context.put("x", dataAsString(x));
        if (y != null) {
            context.put("y", dataAsString(y));
        }

        // for pricing data (candlesticks and OHLC)
        if (open != null) {
            context.put("open", dataAsString(open));
        }
        if (high != null) {
            context.put("high", dataAsString(high));
        }
        if (low != null) {
            context.put("low", dataAsString(low));
        }
        if (close != null) {
            context.put("close", dataAsString(close));
        }
        if (whiskerWidth != DEFAULT_WHISKER_WIDTH) {
            context.put("whiskerWidth", whiskerWidth);
        }
        if (increasing != null) {
            context.put("increasing", increasing);
        }
        if (decreasing != null) {
            context.put("increasing", decreasing);
        }
        if (yAxis != null) {
            context.put("yAxis", yAxis);
        }
        context.put("marker", marker);
        context.put("showlegend", showLegend);
        if (!fill.equals(DEFAULT_FILL)) {
            context.put("fill", fill);
        }
        if (fillColor != null) {
            context.put("fillColor", fillColor);
        }
        if (hoverLabel != null) {
            context.put("hoverlabel", hoverLabel.asJavascript());
        }
        if (line != null) {
            context.put("line", line.asJavascript());
        }
        if (text != null) {
            context.put("text", dataAsString(text));
        }
        return context;
    }

    @Override
    public String asJavascript(int i) {
        Writer writer = new StringWriter();
        PebbleTemplate compiledTemplate;

        try {
            compiledTemplate = engine.getTemplate("trace_template.html");
            Map<String, Object> context = getContext(i);
            compiledTemplate.evaluate(writer, context);
        } catch (PebbleException | IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    public enum Mode {
        LINE("lines"),
        MARKERS("markers"),
        LINE_AND_MARKERS("lines+markers"),
        LINE_AND_TEXT("lines+text"),
        TEXT_AND_MARKERS("markers+text"),
        LINE_TEXT_AND_MARKERS("lines+markers+text"),
        TEXT("text"),
        NONE("none");

        final String value;

        Mode(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static class ScatterBuilder extends TraceBuilder {

        private String type = "scatter";
        private Mode mode = Mode.MARKERS;
        private final Object[] x;
        private double[] y;
        private String[] text;
        private Marker marker;
        private YAxis yAxis;
        private Line line;
        private double[] open;
        private double[] close;
        private double[] high;
        private double[] low;
        private Increasing increasing;
        private Decreasing decreasing;

        /**
         * Sets the area to fill with a solid color. Use with `fillcolor` if not
         * "none". "tozerox" and "tozeroy" fill to x=0 and y=0 respectively.
         * "tonextx" and "tonexty" fill between the endpoints of this trace and
         * the endpoints of the trace before it, connecting those endpoints with
         * straight lines (to make a stacked area graph); if there is no trace
         * before it, they behave like "tozerox" and "tozeroy". "toself"
         * connects the endpoints of the trace (or each segment of the trace if
         * it has gaps) into a closed shape. "tonext" fills the space between
         * two traces if one completely encloses the other (eg consecutive
         * contour lines), and behaves like "toself" if there is no trace before
         * it. "tonext" should not be used if one trace does not enclose the
         * other.
         */
        private ScatterTrace.Fill fill = DEFAULT_FILL;

        /**
         * Sets the fill color. Defaults to a half-transparent variant of the
         * line color, marker color, or marker line color, whichever is
         * available.
         */
        private String fillColor;

        /**
         * Sets the width of the whiskers relative to the box' width. For
         * example, with 1, the whiskers are as wide as the box(es).
         */
        private double whiskerWidth = DEFAULT_WHISKER_WIDTH;

        private ScatterBuilder(double[] x, double[] y) {
            Double[] x1 = new Double[x.length];
            for (int i = 0; i < x1.length; i++) {
                x1[i] = x[i];
            }
            this.x = x1;
            this.y = y;
        }

        private ScatterBuilder(Column<?> x, NumericColumn<? extends Number> y) {
            this.x = x.asObjectArray();
            this.y = y.asDoubleArray();
        }

        private ScatterBuilder(Column<?> x,
                NumericColumn<? extends Number> open,
                NumericColumn<? extends Number> high,
                NumericColumn<? extends Number> low,
                NumericColumn<? extends Number> close) {
            this.x = x.asObjectArray();
            this.open = open.asDoubleArray();
            this.high = high.asDoubleArray();
            this.low = low.asDoubleArray();
            this.close = close.asDoubleArray();
        }

        private ScatterBuilder(DateColumn x, NumericColumn<? extends Number> y) {
            this.x = x.asObjectArray();
            this.y = y.asDoubleArray();
        }

        private ScatterBuilder(DateTimeColumn x, NumericColumn<? extends Number> y) {
            this.x = x.asObjectArray();
            this.y = y.asDoubleArray();
        }

        private ScatterBuilder(TimeColumn x, NumericColumn<? extends Number> y) {
            this.x = x.asObjectArray();
            this.y = y.asDoubleArray();
        }

        public ScatterBuilder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        /**
         * Sets a specific yAxis to this trace when you want to display more
         * than one yAxis in a plot. This can be ignored if only one y axis is
         * desired for the whole plot, and need not be set if this trace should
         * get the default y-axis.
         *
         * There must be a corresponding Y Axis defined in the layout, e.g., if
         * you specify YAxis.Y2 here, you must provide a value for yAxis2 in the
         * layout
         *
         * @param axis The Axis to use for this trace
         * @return this ScatterBuilder
         */
        public ScatterBuilder yAxis(YAxis axis) {
            this.yAxis = axis;
            return this;
        }

        public ScatterBuilder line(Line line) {
            this.line = line;
            return this;
        }

        /**
         * For candlestick plots
         */
        public ScatterBuilder whiskerWidth(double width) {
            Preconditions.checkArgument(width >= 0 && width <= 1);
            this.whiskerWidth = width;
            return this;
        }

        public ScatterBuilder marker(Marker marker) {
            this.marker = marker;
            return this;
        }

        public ScatterBuilder type(String kind) {
            this.type = kind;
            return this;
        }

        public ScatterBuilder text(String[] text) {
            this.text = text;
            return this;
        }

        /**
         * For candlestick plots
         */
        public ScatterBuilder increasing(Increasing increasing) {
            this.increasing = increasing;
            return this;
        }

        /**
         * For candlestick plots
         */
        public ScatterBuilder decreasing(Decreasing decreasing) {
            this.decreasing = decreasing;
            return this;
        }

        public ScatterBuilder fill(ScatterTrace.Fill fill) {
            this.fill = fill;
            return this;
        }

        public ScatterBuilder fillColor(String fillColor) {
            this.fillColor = fillColor;
            return this;
        }

        public ScatterTrace build() {
            return new ScatterTrace(this);
        }

        protected String getType() {
            return type;
        }

        public ScatterBuilder name(String name) {
            return (ScatterBuilder) super.name(name);
        }

        public ScatterBuilder opacity(double n) {
            Preconditions.checkArgument(n >= 0 && n <= 1);
            return (ScatterBuilder) super.opacity(n);
        }

        public ScatterBuilder legendGroup(String group) {
            return (ScatterBuilder) super.legendGroup(group);
        }

        public ScatterBuilder showLegend(boolean showLegend) {
            return (ScatterBuilder) super.showLegend(showLegend);
        }

        public ScatterBuilder visible(Visibility visibility) {
            return (ScatterBuilder) super.visible(visibility);
        }

        public ScatterBuilder hoverLabel(HoverLabel hoverLabel) {
            return (ScatterBuilder) super.hoverLabel(hoverLabel);
        }

        @Override
        public ScatterBuilder xAxis(String xAxis) {
            super.xAxis(xAxis);
            return this;
        }

        @Override
        public ScatterBuilder yAxis(String yAxis) {
            super.yAxis(yAxis);
            return this;
        }
    }
}
