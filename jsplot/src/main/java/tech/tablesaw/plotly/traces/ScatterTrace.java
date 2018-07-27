package tech.tablesaw.plotly.traces;

import com.google.common.base.Preconditions;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DateTimeColumn;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.api.TimeColumn;
import tech.tablesaw.plotly.components.HoverLabel;
import tech.tablesaw.plotly.components.Line;
import tech.tablesaw.plotly.components.Marker;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import static tech.tablesaw.plotly.Utils.dataAsString;

public class ScatterTrace extends AbstractTrace {

    static final Fill DEFAULT_FILL = Fill.NONE;

    public static enum Fill {
        NONE("none"),
        TO_ZERO_Y("tozeroy"),
        TO_ZERO_X("tozerox"),
        TO_NEXT_Y("tonexty"),
        TO_NEXT_X("tonextx"),
        TO_SELF("toself"),
        TO_NEXT("tonext");

        private String value;

        Fill(String value) {
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

    public static ScatterBuilder builder(double[] x, double[] y) {
        return new ScatterBuilder(x, y);
    }

    public static ScatterBuilder builder(NumberColumn x, NumberColumn y) {
        return new ScatterBuilder(x, y);
    }

    public static ScatterBuilder builder(DateColumn x, NumberColumn y) {
        return new ScatterBuilder(x, y);
    }

    public static ScatterBuilder builder(DateTimeColumn x, NumberColumn y) {
        return new ScatterBuilder(x, y);
    }

    public static ScatterBuilder builder(TimeColumn x, NumberColumn y) {
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
    }

    private Map<String, Object> getContext(int i) {

        Map<String, Object> context = super.getContext();
        context.put("variableName", "trace" + i);
        context.put("mode", mode);
        context.put("y", dataAsString(y));
        context.put("x", dataAsString(x));
        context.put("marker", marker);
        context.put("showlegend", showLegend);
        if (!fill.equals(DEFAULT_FILL)) context.put("fill", fill);
        if (fillColor != null) context.put("fillColor", fillColor);
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
        LINE("line"),
        MARKERS("markers"),
        LINE_AND_MARKERS("line + markers"),
        LINE_AND_TEXT("line + text"),
        TEXT_AND_MARKERS("text + text"),
        LINE_TEXT_AND_MARKERS("line + text + markers"),
        TEXT("text"),
        NONE("none");

        String value;

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
        private Object[] x;
        private double[] y;
        private String[] text;
        private Marker marker;
        private Line line;

        /**
         * Sets the area to fill with a solid color. Use with `fillcolor` if not "none". "tozerox" and "tozeroy"
         * fill to x=0 and y=0 respectively. "tonextx" and "tonexty" fill between the endpoints of this trace and the
         * endpoints of the trace before it, connecting those endpoints with straight lines (to make a stacked area graph);
         * if there is no trace before it, they behave like "tozerox" and "tozeroy". "toself" connects the endpoints of
         * the trace (or each segment of the trace if it has gaps) into a closed shape. "tonext" fills the space between
         * two traces if one completely encloses the other (eg consecutive contour lines), and behaves like "toself"
         * if there is no trace before it. "tonext" should not be used if one trace does not enclose the other.
         */
        ScatterTrace.Fill fill = DEFAULT_FILL;

        /**
         * Sets the fill color. Defaults to a half-transparent variant of the line color, marker color, or marker line
         * color, whichever is available.
         */
        String fillColor;

        private ScatterBuilder(double[] x, double[] y) {
            Double[] x1 = new Double[x.length];
            for (int i = 0; i < x1.length; i++) {
                x1[i] = x[i];
            }
            this.x = x1;
            this.y = y;
        }

        private ScatterBuilder(NumberColumn x, NumberColumn y) {
            this.x = x.asObjectArray();
            this.y = y.asDoubleArray();
        }

        private ScatterBuilder(DateColumn x, NumberColumn y) {
            this.x = x.asObjectArray();
            this.y = y.asDoubleArray();
        }

        private ScatterBuilder(DateTimeColumn x, NumberColumn y) {
            this.x = x.asObjectArray();
            this.y = y.asDoubleArray();
        }

        private ScatterBuilder(TimeColumn x, NumberColumn y) {
            this.x = x.asObjectArray();
            this.y = y.asDoubleArray();
        }

        public ScatterBuilder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public ScatterBuilder line(Line line) {
            this.line = line;
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
    }
}
