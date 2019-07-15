package tech.tablesaw.plotly.traces;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import tech.tablesaw.api.NumericColumn;
import tech.tablesaw.plotly.Utils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class Histogram2DTrace extends AbstractTrace {

    private final double[] x;
    private final double[] y;

    public static Histogram2DBuilder builder(double[] x, double[] y) {
        return new Histogram2DBuilder(x, y);
    }

    public static Histogram2DBuilder builder(NumericColumn<? extends Number> x, NumericColumn<? extends Number> y) {
        return new Histogram2DBuilder(x.asDoubleArray(), y.asDoubleArray());
    }

    private Histogram2DTrace(Histogram2DBuilder builder) {
        super(builder);
        this.x = builder.x;
        this.y = builder.y;
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
        context.put("y", Utils.dataAsString(y));
        return context;
    }

    public static class Histogram2DBuilder extends TraceBuilder {

        private final String type = "histogram2d";
        /*
        private int bins;
        private String barMode;
        private String histFunction;
        private String histNorm;
         */
        private final double[] x;
        private final double[] y;

        private Histogram2DBuilder(double[] x, double[] y) {
            this.x = x;
            this.y = y;
        }

        /*
        public Histogram2DBuilder setBins(int bins) {
            this.bins = bins;
            return this;
        }

        public Histogram2DBuilder barMode(String barMode) {
            this.barMode = barMode;
            return this;
        }

        public Histogram2DBuilder histFunction(String histFunction) {
            this.histFunction = histFunction;
            return this;
        }

        public Histogram2DBuilder histNorm(String histNorm) {
            this.histNorm = histNorm;
            return this;
        }
         */
        public Histogram2DTrace build() {
            return new Histogram2DTrace(this);
        }

        @Override
        protected String getType() {
            return type;
        }
    }
}
