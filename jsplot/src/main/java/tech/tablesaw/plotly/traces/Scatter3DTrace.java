package tech.tablesaw.plotly.traces;

import com.google.common.base.Preconditions;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.plotly.components.HoverLabel;
import tech.tablesaw.plotly.components.Marker;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import static tech.tablesaw.plotly.Utils.dataAsString;

public class Scatter3DTrace extends AbstractTrace {

    private final double[] y;
    private final double[] x;
    private final double[] z;
    private final String[] text;
    private final Mode mode;
    private final HoverLabel hoverLabel;
    private final boolean showLegend;
    private final Marker marker;

    public static Scatter3DBuilder builder(double[] x, double[] y, double[] z) {
        return new Scatter3DBuilder(x, y, z);
    }

    public static Scatter3DBuilder builder(NumericColumn<? extends Number> x, NumericColumn<? extends Number> y, NumericColumn<? extends Number> z) {
        return new Scatter3DBuilder(x, y, z);
    }

    private Scatter3DTrace(Scatter3DBuilder builder) {
        super(builder);
        this.mode = builder.mode;
        this.y = builder.y;
        this.x = builder.x;
        this.z = builder.z;
        this.text = builder.text;
        this.hoverLabel = builder.hoverLabel;
        this.showLegend = builder.showLegend;
        this.marker = builder.marker;
    }

    private Map<String, Object> getContext(int i) {

        Map<String, Object> context = super.getContext();
        context.put("variableName", "trace" + i);
        context.put("mode", mode);
        context.put("y", dataAsString(y));
        context.put("x", dataAsString(x));
        context.put("z", dataAsString(z));
        context.put("showlegend", showLegend);
        if (marker != null) {
            context.put("marker", marker);
        }
        if (hoverLabel != null) {
            context.put("hoverlabel", hoverLabel.asJavascript());
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

        final String value;

        Mode(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static class Scatter3DBuilder extends TraceBuilder {

        private String type = "scatter3d";
        private Mode mode = Mode.MARKERS;
        private final double[] x;
        private final double[] y;
        private final double[] z;
        private String[] text;
        private Marker marker;

        private Scatter3DBuilder(double[] x, double[] y, double[] z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private Scatter3DBuilder(NumericColumn<? extends Number> x, NumericColumn<? extends Number> y, NumericColumn<? extends Number> z) {
            this.x = x.asDoubleArray();
            this.y = y.asDoubleArray();
            this.z = z.asDoubleArray();
        }

        public Scatter3DBuilder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public Scatter3DBuilder type(String kind) {
            this.type = kind;
            return this;
        }

        public Scatter3DBuilder text(String[] text) {
            this.text = text;
            return this;
        }

        public Scatter3DTrace build() {
            return new Scatter3DTrace(this);
        }

        protected String getType() {
            return type;
        }

        public Scatter3DBuilder name(String name) {
            return (Scatter3DBuilder) super.name(name);
        }

        public Scatter3DBuilder opacity(double n) {
            Preconditions.checkArgument(n >= 0 && n <= 1);
            return (Scatter3DBuilder) super.opacity(n);
        }

        public Scatter3DBuilder legendGroup(String group) {
            return (Scatter3DBuilder) super.legendGroup(group);
        }

        public Scatter3DBuilder marker(Marker marker) {
            this.marker = marker;
            return this;
        }

        public Scatter3DBuilder showLegend(boolean showLegend) {
            return (Scatter3DBuilder) super.showLegend(showLegend);
        }

        public Scatter3DBuilder visible(Visibility visibility) {
            return (Scatter3DBuilder) super.visible(visibility);
        }

        public Scatter3DBuilder hoverLabel(HoverLabel hoverLabel) {
            return (Scatter3DBuilder) super.hoverLabel(hoverLabel);
        }
    }
}
