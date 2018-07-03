package tech.tablesaw.plotly.traces;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import tech.tablesaw.api.NumberColumn;
import tech.tablesaw.plotly.Utils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class HistogramTrace extends AbstractTrace {

    private double[] x;
    private double opacity;

    public static HistogramBuilder builder(double[] values) {
        return new HistogramBuilder(values);
    }

    public static HistogramBuilder builder(NumberColumn values) {
        return new HistogramBuilder(values.asDoubleArray());
    }

    private HistogramTrace(HistogramBuilder builder) {
        super(builder);
        this.x = builder.x;
        this.opacity = builder.opacity;
    }

    @Override
    public String asJavascript(int i) {
        Writer writer = new StringWriter();
        PebbleTemplate compiledTemplate;

        try {
            compiledTemplate = engine.getTemplate("trace_template.html");
            compiledTemplate.evaluate(writer, getContext(i));
        } catch (PebbleException | IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    private Map<String, Object> getContext(int i) {

        Map<String, Object> context = super.getContext();
        context.put("variableName", "trace" + i);
        context.put("x", Utils.dataAsString(x));
        context.put("opacity", opacity);
        return context;
    }

    public static class HistogramBuilder extends TraceBuilder {

        String type = "histogram";
        int bins;
        String barMode;
        String histFunction;
        String histNorm;
        double[] x;

        private HistogramBuilder(double[] values) {
            this.x = values;
        }

        public HistogramBuilder setBins(int bins) {
            this.bins = bins;
            return this;
        }

        public HistogramBuilder barMode(String barMode) {
            this.barMode = barMode;
            return this;
        }

        public HistogramBuilder histFunction(String histFunction) {
            this.histFunction = histFunction;
            return this;
        }

        public HistogramBuilder histNorm(String histNorm) {
            this.histNorm = histNorm;
            return this;
        }

        public HistogramBuilder opacity(double opacity) {
            super.opacity(opacity);
            return this;
        }

        public HistogramTrace build() {
            return new HistogramTrace(this);
        }

        @Override
        protected String getType() {
            return type;
        }
    }
}
